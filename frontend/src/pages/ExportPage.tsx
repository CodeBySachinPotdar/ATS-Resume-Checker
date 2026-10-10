import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Download, FileText, CheckCircle2, ArrowLeft, Shield, Sparkles, FileType } from 'lucide-react';
import { TemplateType, AnalysisResponseDto } from '../types';
import { api } from '../api/client';

interface ExportPageProps {
  analysis: AnalysisResponseDto | null;
}

export const ExportPage: React.FC<ExportPageProps> = ({ analysis }) => {
  const [template, setTemplate] = useState<TemplateType>('CLASSIC_PROFESSIONAL');
  const [downloading, setDownloading] = useState<string | null>(null);

  const templates = [
    {
      id: 'CLASSIC_PROFESSIONAL' as TemplateType,
      name: 'Classic Professional',
      desc: 'Clean single-column standard format with serif-neutral hierarchy. Universally parsed across all ATS engines.',
    },
    {
      id: 'MODERN_MINIMAL' as TemplateType,
      name: 'Modern Minimal',
      desc: 'Crisp typography and structured whitespace. Eliminates all complex lines, borders, and graphics.',
    },
    {
      id: 'SOFTWARE_ENGINEER' as TemplateType,
      name: 'Software Engineer',
      desc: 'Places Technical Skills and Core Competencies at the top for immediate parser entity recognition.',
    },
    {
      id: 'EXECUTIVE' as TemplateType,
      name: 'Executive',
      desc: 'Emphasizes leadership scope, strategic accomplishments, and executive summary narrative.',
    },
  ];

  const handleDownload = async (format: 'pdf' | 'docx' | 'txt') => {
    setDownloading(format);
    try {
      const resumeId = analysis?.resumeId || 1;
      // In web app, trigger browser download
      const downloadUrl = api.getExportUrl(resumeId, format, template);

      const res = await fetch(downloadUrl);
      if (!res.ok) {
        throw new Error('Download request failed.');
      }
      const blob = await res.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${analysis?.resumeTitle || 'Resume'}_ATS.${format}`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (err: any) {
      alert('Could not download file: ' + err.message);
    } finally {
      setDownloading(null);
    }
  };

  return (
    <div className="mx-auto max-w-6xl px-4 py-10 sm:px-6 lg:px-8 space-y-10">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-800 pb-6">
        <div>
          <Link to="/results" className="inline-flex items-center gap-1.5 text-xs text-slate-400 hover:text-white mb-2">
            <ArrowLeft className="h-3.5 w-3.5" />
            <span>Back to Analysis Report</span>
          </Link>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
            Export ATS-Optimized Resume
          </h1>
          <p className="text-sm text-slate-400 mt-1">
            Choose from 4 proven single-column ATS layouts and download as PDF, Word DOCX, or TXT.
          </p>
        </div>

        {/* Download Buttons */}
        <div className="flex flex-wrap items-center gap-2.5">
          <button
            onClick={() => handleDownload('pdf')}
            disabled={downloading !== null}
            className="flex items-center gap-1.5 px-4 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs transition-all shadow-md shadow-emerald-500/10"
          >
            <Download className="h-3.5 w-3.5" />
            <span>{downloading === 'pdf' ? 'Generating PDF...' : 'Download PDF'}</span>
          </button>
          <button
            onClick={() => handleDownload('docx')}
            disabled={downloading !== null}
            className="flex items-center gap-1.5 px-4 py-2.5 rounded-xl border border-slate-700 bg-slate-900 hover:bg-slate-800 text-white font-semibold text-xs transition-colors"
          >
            <Download className="h-3.5 w-3.5" />
            <span>{downloading === 'docx' ? 'Generating DOCX...' : 'Download DOCX'}</span>
          </button>
          <button
            onClick={() => handleDownload('txt')}
            disabled={downloading !== null}
            className="flex items-center gap-1.5 px-3 py-2.5 rounded-xl border border-slate-800 bg-slate-950 hover:bg-slate-900 text-slate-400 hover:text-white font-medium text-xs transition-colors"
          >
            <span>TXT</span>
          </button>
        </div>
      </div>

      {/* Template Selection Grid */}
      <div className="space-y-4">
        <h2 className="text-base font-bold text-white tracking-tight">Select ATS-Friendly Template</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {templates.map((t) => {
            const isSelected = template === t.id;
            return (
              <button
                key={t.id}
                onClick={() => setTemplate(t.id)}
                className={`text-left rounded-xl p-5 border transition-all space-y-2 flex flex-col justify-between ${
                  isSelected
                    ? 'border-emerald-500 bg-emerald-950/20 shadow-md shadow-emerald-500/10'
                    : 'border-slate-800 bg-slate-900/60 hover:border-slate-700 hover:bg-slate-900'
                }`}
              >
                <div className="space-y-1">
                  <div className="flex items-center justify-between">
                    <span className="text-sm font-bold text-white">{t.name}</span>
                    {isSelected && <CheckCircle2 className="h-4 w-4 text-emerald-400" />}
                  </div>
                  <p className="text-xs text-slate-400 leading-relaxed">{t.desc}</p>
                </div>
                <span className={`text-[11px] font-mono font-semibold ${isSelected ? 'text-emerald-400' : 'text-slate-500'}`}>
                  {isSelected ? '✓ Active Template' : 'Click to select'}
                </span>
              </button>
            );
          })}
        </div>
      </div>

      {/* Compliance Guarantee Badges */}
      <div className="rounded-xl border border-slate-800 bg-slate-900/40 p-4 flex flex-wrap items-center justify-between gap-4 text-xs text-slate-400">
        <div className="flex items-center gap-2">
          <Shield className="h-4 w-4 text-emerald-400" />
          <span>No tables, no multi-column layouts, no images or text boxes.</span>
        </div>
        <div className="flex items-center gap-4 text-slate-500">
          <span>✓ Standard Section Headings</span>
          <span>✓ Standard Font Metrics</span>
          <span>✓ UTF-8 Machine-Readable</span>
        </div>
      </div>

      {/* Live Preview Paper */}
      <div className="rounded-2xl border border-slate-800 bg-slate-900 p-6 space-y-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <FileText className="h-4 w-4 text-emerald-400" />
            <h3 className="text-sm font-bold text-white">Document Structure Preview ({template})</h3>
          </div>
          <span className="text-xs text-slate-500 font-mono">100% ATS Safe Format</span>
        </div>

        {/* Paper Simulation */}
        <div className="rounded-xl border border-slate-800 bg-slate-950 p-8 font-sans text-xs text-slate-300 space-y-5 max-w-3xl mx-auto shadow-inner leading-relaxed">
          <div className="border-b border-slate-800 pb-4 text-center space-y-1">
            <h2 className="text-xl font-bold text-white tracking-wide uppercase">Alexander Chen</h2>
            <p className="text-slate-400 text-[11px]">
              San Francisco, CA | (415) 555-0192 | alex.chen.dev@example.com | linkedin.com/in/alexanderchen-dev
            </p>
          </div>

          <div className="space-y-1.5">
            <h3 className="font-bold text-slate-200 tracking-wider uppercase text-[11px] border-b border-slate-800/80 pb-0.5">
              Professional Summary
            </h3>
            <p className="text-slate-400">
              Results-driven Senior Java Developer with 6+ years of hands-on experience designing and implementing distributed backend services. Proficient in Java, Spring Boot, Microservices, and REST APIs with strong relational database skills in PostgreSQL.
            </p>
          </div>

          <div className="space-y-1.5">
            <h3 className="font-bold text-slate-200 tracking-wider uppercase text-[11px] border-b border-slate-800/80 pb-0.5">
              Technical Skills
            </h3>
            <p className="text-slate-400">
              Java 17/21, Spring Boot, Microservices, RESTful APIs, PostgreSQL, Redis, Docker, AWS (S3, EC2), JUnit 5, CI/CD, Git, Agile
            </p>
          </div>

          <div className="space-y-2">
            <h3 className="font-bold text-slate-200 tracking-wider uppercase text-[11px] border-b border-slate-800/80 pb-0.5">
              Work Experience
            </h3>
            <div>
              <div className="flex justify-between font-semibold text-slate-300">
                <span>Senior Software Developer — Nexus Financial Technologies</span>
                <span>2021 - Present</span>
              </div>
              <ul className="list-disc list-inside text-slate-400 space-y-1 mt-1 pl-1">
                <li>Architected Spring Boot REST microservices for high-volume payment processing, improving throughput by 28%.</li>
                <li>Refactored monolithic services into modular microservices using Docker containers and PostgreSQL.</li>
                <li>Implemented Redis caching layers, reducing database read latency by 45% across peak trading periods.</li>
              </ul>
            </div>
          </div>

          <div className="space-y-1.5">
            <h3 className="font-bold text-slate-200 tracking-wider uppercase text-[11px] border-b border-slate-800/80 pb-0.5">
              Education & Certifications
            </h3>
            <p className="text-slate-400">
              B.S. in Computer Science — University of California, Berkeley (2018)
            </p>
            <p className="text-slate-400">
              AWS Certified Solutions Architect - Associate | Oracle Certified Professional: Java SE 11
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};
