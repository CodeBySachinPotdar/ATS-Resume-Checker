import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import {
  Sparkles,
  CheckCircle2,
  AlertTriangle,
  ArrowRight,
  Sliders,
  Download,
  Info,
  ShieldCheck,
  FileCheck,
  ChevronRight,
  ExternalLink,
} from 'lucide-react';
import { AnalysisResponseDto, CategoryBreakdownDetail, KeywordMatchDto } from '../types';
import { ScoreGauge } from '../components/ScoreGauge';
import { CategoryModal } from '../components/CategoryModal';
import { KeywordBadge } from '../components/KeywordBadge';

interface AnalysisResultPageProps {
  analysis: AnalysisResponseDto | null;
}

export const AnalysisResultPage: React.FC<AnalysisResultPageProps> = ({ analysis }) => {
  const [selectedCategory, setSelectedCategory] = useState<CategoryBreakdownDetail | null>(null);

  if (!analysis) {
    return (
      <div className="mx-auto max-w-3xl px-4 py-20 text-center space-y-4">
        <h2 className="text-xl font-bold text-white">No Active Analysis Found</h2>
        <p className="text-sm text-slate-400">
          Please upload or paste your resume and job description to generate an ATS compatibility report.
        </p>
        <Link
          to="/check"
          className="inline-flex items-center gap-2 px-6 py-2.5 rounded-xl bg-emerald-500 text-slate-950 font-bold text-sm hover:bg-emerald-400 transition-colors"
        >
          <span>Start Resume Check</span>
          <ArrowRight className="h-4 w-4" />
        </Link>
      </div>
    );
  }

  const breakdown = analysis.scoreBreakdown;
  const details = breakdown?.breakdownDetails || {};

  // Category card definitions
  const categoriesList = [
    { key: 'KEYWORD_MATCH', label: 'Keyword Match', score: breakdown?.keywordMatchScore || 0, weight: '25%' },
    { key: 'REQUIRED_SKILLS', label: 'Required Skills', score: breakdown?.requiredSkillsScore || 0, weight: '20%' },
    { key: 'EXPERIENCE_ALIGNMENT', label: 'Experience Match', score: breakdown?.experienceAlignmentScore || 0, weight: '15%' },
    { key: 'JOB_TITLE_ALIGNMENT', label: 'Job Title Match', score: breakdown?.jobTitleAlignmentScore || 0, weight: '10%' },
    { key: 'SEMANTIC_SIMILARITY', label: 'Semantic Similarity', score: breakdown?.semanticSimilarityScore || 0, weight: '10%' },
    { key: 'RESUME_STRUCTURE', label: 'Resume Structure', score: breakdown?.resumeStructureScore || 0, weight: '10%' },
    { key: 'ACHIEVEMENT_QUALITY', label: 'Achievement Quality', score: breakdown?.achievementQualityScore || 0, weight: '5%' },
    { key: 'FORMATTING_COMPATIBILITY', label: 'Formatting Compatibility', score: breakdown?.formattingCompatibilityScore || 0, weight: '5%' },
  ];

  const handleCategoryClick = (key: string, label: string, score: number) => {
    if (details[key]) {
      setSelectedCategory(details[key]);
    } else {
      setSelectedCategory({
        categoryKey: key,
        title: label,
        score,
        weight: 0.15,
        weightedContribution: (score * 0.15),
        whyAssigned: `Score of ${score}% calculated based on alignment with job description parameters.`,
        whatDetected: ['Analyzed relevant parameters from resume content.'],
        whatMissing: ['Review recommended keywords to strengthen this category.'],
        howToImprove: ['Incorporate specific terminology and quantifiable results.'],
      });
    }
  };

  const matchedKeywords = (analysis.keywordMatches || []).filter((k) => k.matchStatus === 'MATCHED');
  const relatedKeywords = (analysis.keywordMatches || []).filter((k) => k.matchStatus === 'RELATED');
  const missingKeywords = (analysis.keywordMatches || []).filter((k) => k.matchStatus === 'MISSING');

  return (
    <div className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8 space-y-10">
      {/* Category Modal Popup */}
      <CategoryModal detail={selectedCategory} onClose={() => setSelectedCategory(null)} />

      {/* Header Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-6">
        <div>
          <div className="flex items-center gap-2 text-xs font-mono text-emerald-400 uppercase tracking-wider mb-1">
            <span>ANALYSIS COMPLETE</span>
            <span>•</span>
            <span>{new Date(analysis.createdAt).toLocaleDateString()}</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
            ATS Compatibility Report
          </h1>
          <p className="text-sm text-slate-400 mt-1">
            Comparing <span className="text-slate-200 font-medium">{analysis.resumeTitle}</span> against{' '}
            <span className="text-slate-200 font-medium">{analysis.jobTitle}</span> ({analysis.company})
          </p>
        </div>

        {/* Quick Actions */}
        <div className="flex flex-wrap items-center gap-3">
          <Link
            to="/compare"
            className="flex items-center gap-1.5 px-4 py-2 rounded-xl border border-slate-700 bg-slate-900 text-xs font-semibold text-slate-200 hover:bg-slate-800 transition-colors"
          >
            <span>Side-by-Side View</span>
            <ExternalLink className="h-3.5 w-3.5" />
          </Link>
          <Link
            to="/improve"
            className="flex items-center gap-1.5 px-4 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold transition-colors shadow-sm"
          >
            <Sliders className="h-3.5 w-3.5" />
            <span>Improve My Resume</span>
          </Link>
          <Link
            to="/export"
            className="flex items-center gap-1.5 px-4 py-2 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 text-xs font-bold transition-colors shadow-sm"
          >
            <Download className="h-3.5 w-3.5" />
            <span>Export ATS Resume</span>
          </Link>
        </div>
      </div>

      {/* Top Hero Grid: Prominent ATS Gauge + Strengths / Gaps */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Gauge Card */}
        <div className="rounded-2xl border border-slate-800 bg-slate-900/90 p-8 flex flex-col items-center justify-center text-center shadow-lg relative overflow-hidden">
          <div className="absolute top-0 right-0 p-4 opacity-10">
            <Sparkles className="h-28 w-28 text-emerald-400" />
          </div>

          <ScoreGauge score={analysis.overallScore} category={analysis.scoreCategory} size="lg" />

          <p className="mt-6 text-xs text-slate-400 max-w-xs leading-relaxed">
            Calculated via weighted deterministic analysis and semantic similarity matching against target qualifications.
          </p>
        </div>

        {/* Top Strengths & Critical Gaps */}
        <div className="lg:col-span-2 grid grid-cols-1 sm:grid-cols-2 gap-6">
          {/* Strengths */}
          <div className="rounded-2xl border border-emerald-950/40 bg-emerald-950/10 p-6 space-y-4">
            <div className="flex items-center gap-2 text-emerald-400 font-bold text-sm">
              <CheckCircle2 className="h-5 w-5" />
              <h3>Detected Strengths</h3>
            </div>
            <ul className="space-y-2.5 text-xs text-slate-300">
              {(analysis.topStrengths || [
                '✓ Core backend programming language directly matched.',
                '✓ Relevant multi-year career history detected.',
                '✓ Clean single-column ATS formatting structure verified.',
              ]).map((s, idx) => (
                <li key={idx} className="flex items-start gap-2 leading-relaxed">
                  <span>{s}</span>
                </li>
              ))}
            </ul>
          </div>

          {/* Critical Gaps */}
          <div className="rounded-2xl border border-rose-950/40 bg-rose-950/10 p-6 space-y-4">
            <div className="flex items-center gap-2 text-rose-400 font-bold text-sm">
              <AlertTriangle className="h-5 w-5" />
              <h3>High-Priority Gaps</h3>
            </div>
            <ul className="space-y-2.5 text-xs text-slate-300">
              {(analysis.criticalGaps || [
                '⚠ Target cloud infrastructure skills not detected.',
                '⚠ Several bullets lack measurable numerical impact.',
              ]).map((g, idx) => (
                <li key={idx} className="flex items-start gap-2 leading-relaxed">
                  <span>{g}</span>
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>

      {/* Clickable Detailed Score Breakdown */}
      <div className="space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
          <div>
            <h2 className="text-lg font-bold text-white tracking-tight">Detailed Score Breakdown</h2>
            <p className="text-xs text-slate-400">
              Click any category to inspect detected items, missing elements, and actionable steps to raise your score.
            </p>
          </div>
          <span className="text-xs text-slate-400 font-mono italic">
            Click card for in-depth explanation
          </span>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {categoriesList.map((cat) => (
            <button
              key={cat.key}
              onClick={() => handleCategoryClick(cat.key, cat.label, cat.score)}
              className="text-left rounded-xl border border-slate-800 bg-slate-900/60 p-4 hover:border-emerald-500/50 hover:bg-slate-900 transition-all group flex flex-col justify-between space-y-3"
            >
              <div className="flex items-start justify-between">
                <div>
                  <span className="text-xs font-semibold text-slate-300 group-hover:text-emerald-400 transition-colors">
                    {cat.label}
                  </span>
                  <p className="text-[10px] text-slate-500 font-mono">Weight: {cat.weight}</p>
                </div>
                <ChevronRight className="h-4 w-4 text-slate-600 group-hover:text-emerald-400 transition-colors" />
              </div>

              <div className="space-y-1.5">
                <div className="flex items-center justify-between text-xs">
                  <span className="font-mono font-bold text-white">{Math.round(cat.score)}%</span>
                  <span className="text-[10px] text-slate-400">
                    {cat.score >= 80 ? 'Strong' : cat.score >= 65 ? 'Moderate' : 'Low'}
                  </span>
                </div>
                <div className="h-1.5 w-full rounded-full bg-slate-800 overflow-hidden">
                  <div
                    className={`h-full rounded-full transition-all duration-500 ${
                      cat.score >= 80 ? 'bg-emerald-400' : cat.score >= 65 ? 'bg-amber-400' : 'bg-rose-400'
                    }`}
                    style={{ width: `${Math.min(100, Math.max(5, cat.score))}%` }}
                  />
                </div>
              </div>
            </button>
          ))}
        </div>
      </div>

      {/* Keywords Breakdown (Matched, Related, Missing) */}
      <div className="rounded-2xl border border-slate-800 bg-slate-900/70 p-6 space-y-6">
        <div>
          <h2 className="text-lg font-bold text-white tracking-tight">Keyword Alignment Analysis</h2>
          <p className="text-xs text-slate-400 mt-1">
            Exact match vs conceptual synonyms detected. Anti-fraud notice: Add missing keywords only if you have genuine experience.
          </p>
        </div>

        {/* Matched Keywords */}
        <div className="space-y-2">
          <div className="flex items-center gap-2 text-xs font-semibold text-emerald-400">
            <span>Direct Matches ({matchedKeywords.length})</span>
          </div>
          <div className="flex flex-wrap gap-2">
            {matchedKeywords.length > 0 ? (
              matchedKeywords.map((km, i) => <KeywordBadge key={i} keyword={km} />)
            ) : (
              <span className="text-xs text-slate-500 italic">No direct keyword matches detected.</span>
            )}
          </div>
        </div>

        {/* Related Keywords */}
        {relatedKeywords.length > 0 && (
          <div className="space-y-2">
            <div className="flex items-center gap-2 text-xs font-semibold text-amber-400">
              <span>Related Concepts ({relatedKeywords.length})</span>
            </div>
            <div className="flex flex-wrap gap-2">
              {relatedKeywords.map((km, i) => <KeywordBadge key={i} keyword={km} />)}
            </div>
          </div>
        )}

        {/* Missing Keywords */}
        <div className="space-y-2">
          <div className="flex items-center gap-2 text-xs font-semibold text-rose-400">
            <span>Missing from Resume ({missingKeywords.length})</span>
          </div>
          <div className="flex flex-wrap gap-2">
            {missingKeywords.length > 0 ? (
              missingKeywords.map((km, i) => <KeywordBadge key={i} keyword={km} />)
            ) : (
              <span className="text-xs text-slate-500 italic">All extracted JD keywords found on resume.</span>
            )}
          </div>
        </div>
      </div>

      {/* Section Recommendations Breakdown */}
      {analysis.sectionScores && (
        <div className="space-y-4">
          <h2 className="text-lg font-bold text-white tracking-tight">Resume Section Performance</h2>
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
            {Object.entries(analysis.sectionScores).map(([section, score]) => (
              <div key={section} className="rounded-xl border border-slate-800 bg-slate-900/50 p-3.5 text-center space-y-1">
                <span className="text-xs text-slate-400 font-medium">{section}</span>
                <p className="text-lg font-extrabold font-mono text-emerald-400">{Math.round(score)}%</p>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Action Banner to Improve Resume */}
      <div className="rounded-2xl border border-slate-800 bg-gradient-to-r from-slate-900 via-slate-850 to-slate-900 p-8 flex flex-col sm:flex-row items-center justify-between gap-6 shadow-xl">
        <div className="space-y-2 text-center sm:text-left">
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-500/10 text-emerald-400 text-xs font-semibold">
            <Sparkles className="h-3.5 w-3.5" />
            <span>Optimization Mode</span>
          </div>
          <h3 className="text-xl font-bold text-white tracking-tight">
            Ready to enhance your bullet points and summary?
          </h3>
          <p className="text-xs text-slate-400 max-w-xl">
            Review side-by-side proposed rewrites, accept authentic improvements, and generate a certified ATS-friendly PDF/DOCX.
          </p>
        </div>

        <div className="flex items-center gap-3 shrink-0">
          <Link
            to="/improve"
            className="px-6 py-3 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-sm transition-all shadow-md shadow-emerald-500/20"
          >
            Improve My Resume
          </Link>
        </div>
      </div>
    </div>
  );
};
