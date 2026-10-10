import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { Upload, FileText, Sparkles, Check, AlertCircle, ArrowRight, Loader2 } from 'lucide-react';
import { api } from '../api/client';
import { AnalysisResponseDto } from '../types';

interface AnalysisInputPageProps {
  onAnalysisComplete: (result: AnalysisResponseDto) => void;
}

export const AnalysisInputPage: React.FC<AnalysisInputPageProps> = ({ onAnalysisComplete }) => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  // Mode: 'file' | 'paste'
  const [resumeMode, setResumeMode] = useState<'file' | 'paste'>('file');
  const [resumeFile, setResumeFile] = useState<File | null>(null);
  const [resumeTitle, setResumeTitle] = useState('My Resume');
  const [resumeText, setResumeText] = useState('');

  const [jobTitle, setJobTitle] = useState('Senior Java Developer');
  const [company, setCompany] = useState('ABC Global FinTech');
  const [jobDescriptionText, setJobDescriptionText] = useState('');

  const [loading, setLoading] = useState(false);
  const [activeStep, setActiveStep] = useState(0);
  const [error, setError] = useState<string | null>(null);

  const steps = [
    'Reading resume document and inspecting formatting',
    'Extracting candidate competencies and work history',
    'Analyzing target job description requirements',
    'Executing exact and semantic keyword matching',
    'Evaluating experience alignment and achievement quality',
    'Generating actionable recommendations and ATS score',
  ];

  // Auto load demo if query param ?demo=true is present
  useEffect(() => {
    if (searchParams.get('demo') === 'true') {
      loadSampleData();
    }
  }, [searchParams]);

  const loadSampleData = async () => {
    try {
      const data = await api.getSampleJava();
      setResumeMode('paste');
      setResumeTitle(data.resumeTitle);
      setResumeText(data.resumeText);
      setJobTitle(data.jobTitle);
      setCompany(data.company);
      setJobDescriptionText(data.jobDescriptionText);
      setError(null);
    } catch (e: any) {
      setError('Could not load sample data: ' + e.message);
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      setResumeFile(file);
      setResumeTitle(file.name.replace(/\.[^/.]+$/, ''));
    }
  };

  const handleAnalyze = async () => {
    setError(null);

    // Validation
    if (resumeMode === 'file' && !resumeFile) {
      setError('Please select a resume file (PDF, DOCX, or TXT) to upload.');
      return;
    }
    if (resumeMode === 'paste' && (!resumeText || resumeText.trim().length < 50)) {
      setError('Please paste your resume text (minimum 50 characters required).');
      return;
    }
    if (!jobDescriptionText || jobDescriptionText.trim().length < 50) {
      setError('Please paste the job description text (minimum 50 characters required).');
      return;
    }

    setLoading(true);
    setActiveStep(0);

    // Animate stage updates for realistic SaaS user feedback
    const interval = setInterval(() => {
      setActiveStep((prev) => (prev < steps.length - 1 ? prev + 1 : prev));
    }, 600);

    try {
      let finalResumeText = resumeText;

      // If file mode, upload first to extract text
      if (resumeMode === 'file' && resumeFile) {
        const uploadRes = await api.uploadResumeFile(resumeFile);
        finalResumeText = uploadRes.rawText;
      }

      // Execute quick check
      const result = await api.quickCheck({
        resumeTitle,
        resumeText: finalResumeText,
        jobTitle,
        company,
        jobDescriptionText,
      });

      clearInterval(interval);
      setActiveStep(steps.length);
      onAnalysisComplete(result);
      navigate('/results');
    } catch (err: any) {
      clearInterval(interval);
      setError(err.message || 'Analysis failed. Please check inputs and try again.');
      setLoading(false);
    }
  };

  return (
    <div className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8 space-y-8">
      {/* Page Title & Fast Demo Button */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-800 pb-6">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
            ATS Resume Compatibility Checker
          </h1>
          <p className="text-sm text-slate-400 mt-1">
            Compare your resume against any job description with hybrid deterministic & semantic evaluation.
          </p>
        </div>
        <button
          onClick={loadSampleData}
          className="inline-flex items-center gap-2 self-start px-4 py-2 rounded-xl border border-emerald-500/30 bg-emerald-500/10 text-emerald-400 hover:bg-emerald-500/20 text-xs font-semibold transition-all shadow-sm"
        >
          <Sparkles className="h-4 w-4" />
          <span>Load Senior Java Dev Sample</span>
        </button>
      </div>

      {error && (
        <div className="rounded-xl border border-rose-500/30 bg-rose-500/10 p-4 flex items-start gap-3 text-rose-300 text-sm">
          <AlertCircle className="h-5 w-5 shrink-0 mt-0.5" />
          <span>{error}</span>
        </div>
      )}

      {/* Main Input Grid: 2 Columns (Resume vs Job Description) */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Left Column: Resume Input */}
        <div className="rounded-2xl border border-slate-800 bg-slate-900/70 p-6 space-y-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <FileText className="h-5 w-5 text-emerald-400" />
              <h2 className="text-lg font-bold text-white">1. Your Resume</h2>
            </div>
            {/* Toggle File / Paste */}
            <div className="flex items-center p-1 rounded-lg bg-slate-950 border border-slate-800 text-xs">
              <button
                type="button"
                onClick={() => setResumeMode('file')}
                className={`px-3 py-1 rounded-md font-medium transition-all ${
                  resumeMode === 'file'
                    ? 'bg-slate-800 text-emerald-400 shadow-sm'
                    : 'text-slate-400 hover:text-white'
                }`}
              >
                Upload File
              </button>
              <button
                type="button"
                onClick={() => setResumeMode('paste')}
                className={`px-3 py-1 rounded-md font-medium transition-all ${
                  resumeMode === 'paste'
                    ? 'bg-slate-800 text-emerald-400 shadow-sm'
                    : 'text-slate-400 hover:text-white'
                }`}
              >
                Paste Text
              </button>
            </div>
          </div>

          {resumeMode === 'file' ? (
            <div className="space-y-4">
              <label
                htmlFor="resume-upload"
                className={`flex flex-col items-center justify-center p-8 border-2 border-dashed rounded-xl cursor-pointer transition-all ${
                  resumeFile
                    ? 'border-emerald-500/50 bg-emerald-950/10'
                    : 'border-slate-700 bg-slate-950/40 hover:border-emerald-500/40 hover:bg-slate-950/70'
                }`}
              >
                <div className="flex flex-col items-center justify-center text-center space-y-2">
                  <div className="p-3 rounded-full bg-slate-800 text-emerald-400">
                    <Upload className="h-6 w-6" />
                  </div>
                  {resumeFile ? (
                    <div>
                      <p className="text-sm font-semibold text-emerald-300">{resumeFile.name}</p>
                      <p className="text-xs text-slate-400 mt-0.5">
                        {(resumeFile.size / 1024).toFixed(1)} KB — Click to change file
                      </p>
                    </div>
                  ) : (
                    <div>
                      <p className="text-sm font-semibold text-slate-200">
                        Click to upload or drag & drop resume
                      </p>
                      <p className="text-xs text-slate-500 mt-1">
                        Supported formats: PDF, DOC, DOCX, TXT (Max 10MB)
                      </p>
                    </div>
                  )}
                </div>
                <input
                  id="resume-upload"
                  type="file"
                  accept=".pdf,.docx,.doc,.txt"
                  className="hidden"
                  onChange={handleFileChange}
                />
              </label>

              <div className="text-xs text-slate-500 space-y-1">
                <p>• Scanned PDFs containing only images will require OCR text conversion.</p>
                <p>• Password-protected files must have encryption removed prior to upload.</p>
              </div>
            </div>
          ) : (
            <div className="space-y-3">
              <div className="flex items-center justify-between text-xs text-slate-400">
                <span>Paste plain text resume:</span>
                <span className="font-mono">{resumeText.length.toLocaleString()} characters</span>
              </div>
              <textarea
                rows={16}
                value={resumeText}
                onChange={(e) => setResumeText(e.target.value)}
                placeholder="Paste full resume text here (Summary, Skills, Work Experience, Education)..."
                className="w-full rounded-xl border border-slate-800 bg-slate-950 p-4 text-xs font-mono text-slate-200 placeholder-slate-600 focus:border-emerald-500 focus:outline-none focus:ring-1 focus:ring-emerald-500"
              />
            </div>
          )}
        </div>

        {/* Right Column: Job Description Input */}
        <div className="rounded-2xl border border-slate-800 bg-slate-900/70 p-6 space-y-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Sparkles className="h-5 w-5 text-blue-400" />
              <h2 className="text-lg font-bold text-white">2. Target Job Description</h2>
            </div>
            <span className="text-xs text-slate-400 font-mono">
              {jobDescriptionText.length.toLocaleString()} characters
            </span>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1">Target Job Title</label>
              <input
                type="text"
                value={jobTitle}
                onChange={(e) => setJobTitle(e.target.value)}
                placeholder="e.g. Senior Java Developer"
                className="w-full rounded-lg border border-slate-800 bg-slate-950 px-3.5 py-2 text-xs text-slate-200 focus:border-emerald-500 focus:outline-none"
              />
            </div>
            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1">Company (Optional)</label>
              <input
                type="text"
                value={company}
                onChange={(e) => setCompany(e.target.value)}
                placeholder="e.g. ABC FinTech Corp"
                className="w-full rounded-lg border border-slate-800 bg-slate-950 px-3.5 py-2 text-xs text-slate-200 focus:border-emerald-500 focus:outline-none"
              />
            </div>
          </div>

          <div className="space-y-2">
            <label className="block text-xs font-medium text-slate-400">
              Paste Complete Job Posting
            </label>
            <textarea
              rows={13}
              value={jobDescriptionText}
              onChange={(e) => setJobDescriptionText(e.target.value)}
              placeholder="Paste the full job posting including responsibilities, required qualifications, tech stack, and nice-to-haves..."
              className="w-full rounded-xl border border-slate-800 bg-slate-950 p-4 text-xs font-mono text-slate-200 placeholder-slate-600 focus:border-emerald-500 focus:outline-none focus:ring-1 focus:ring-emerald-500"
            />
          </div>
        </div>
      </div>

      {/* Analysis Action / Progress Tracker */}
      <div className="rounded-2xl border border-slate-800 bg-slate-900 p-6 flex flex-col items-center justify-center space-y-6">
        {loading ? (
          <div className="w-full max-w-xl space-y-5 text-center">
            <div className="flex items-center justify-center gap-3">
              <Loader2 className="h-6 w-6 animate-spin text-emerald-400" />
              <h3 className="text-lg font-bold text-white tracking-tight">
                Analyzing Resume Against Job Requirements...
              </h3>
            </div>

            {/* Steps Checklist */}
            <div className="space-y-2.5 text-left bg-slate-950/70 p-5 rounded-xl border border-slate-800">
              {steps.map((step, idx) => {
                const isPassed = idx < activeStep;
                const isCurrent = idx === activeStep;
                return (
                  <div key={idx} className="flex items-center gap-3 text-xs">
                    {isPassed ? (
                      <span className="flex h-5 w-5 items-center justify-center rounded-full bg-emerald-500/20 text-emerald-400 font-bold">
                        ✓
                      </span>
                    ) : isCurrent ? (
                      <span className="flex h-5 w-5 items-center justify-center rounded-full bg-blue-500/20 text-blue-400 animate-pulse font-bold">
                        ●
                      </span>
                    ) : (
                      <span className="flex h-5 w-5 items-center justify-center rounded-full bg-slate-800 text-slate-600 font-bold">
                        ○
                      </span>
                    )}
                    <span className={isPassed ? 'text-slate-300' : isCurrent ? 'text-white font-semibold' : 'text-slate-600'}>
                      {step}
                    </span>
                  </div>
                );
              })}
            </div>
          </div>
        ) : (
          <div className="flex flex-col sm:flex-row items-center justify-between w-full gap-4">
            <div className="text-xs text-slate-400">
              <span className="text-slate-200 font-medium">Ready for evaluation.</span> Our scoring engine runs 100% locally and safely.
            </div>
            <button
              onClick={handleAnalyze}
              className="w-full sm:w-auto flex items-center justify-center gap-2 px-8 py-3.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-sm transition-all shadow-lg shadow-emerald-500/20"
            >
              <span>Analyze Resume</span>
              <ArrowRight className="h-4 w-4" />
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
