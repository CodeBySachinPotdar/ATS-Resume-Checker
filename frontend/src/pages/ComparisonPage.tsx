import React from 'react';
import { Link } from 'react-router-dom';
import { CheckCircle2, ArrowRight, ArrowLeft, AlertCircle, HelpCircle } from 'lucide-react';
import { AnalysisResponseDto } from '../types';

interface ComparisonPageProps {
  analysis: AnalysisResponseDto | null;
}

export const ComparisonPage: React.FC<ComparisonPageProps> = ({ analysis }) => {
  if (!analysis) {
    return (
      <div className="mx-auto max-w-3xl px-4 py-20 text-center space-y-4">
        <h2 className="text-xl font-bold text-white">No Analysis Selected</h2>
        <p className="text-sm text-slate-400">
          Please run an ATS check first to view side-by-side comparison.
        </p>
        <Link
          to="/check"
          className="inline-flex items-center gap-2 px-6 py-2.5 rounded-xl bg-emerald-500 text-slate-950 font-bold text-sm"
        >
          <span>Run Check</span>
          <ArrowRight className="h-4 w-4" />
        </Link>
      </div>
    );
  }

  const matches = analysis.keywordMatches || [];

  return (
    <div className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8 space-y-8">
      {/* Top Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-800 pb-6">
        <div>
          <Link to="/results" className="inline-flex items-center gap-1.5 text-xs text-slate-400 hover:text-white mb-2">
            <ArrowLeft className="h-3.5 w-3.5" />
            <span>Back to Full Report</span>
          </Link>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
            Side-by-Side ATS Comparison
          </h1>
          <p className="text-sm text-slate-400 mt-1">
            Visual alignment between your resume keywords and the job description requirements.
          </p>
        </div>

        {/* Legend */}
        <div className="flex items-center gap-3 text-xs bg-slate-900 px-4 py-2 rounded-xl border border-slate-800 self-start">
          <span className="flex items-center gap-1.5 text-emerald-400">
            <span className="h-2.5 w-2.5 rounded-full bg-emerald-500" /> Matched (Direct)
          </span>
          <span className="flex items-center gap-1.5 text-amber-400">
            <span className="h-2.5 w-2.5 rounded-full bg-amber-500" /> Related (Partial)
          </span>
          <span className="flex items-center gap-1.5 text-rose-400">
            <span className="h-2.5 w-2.5 rounded-full bg-rose-500" /> Missing
          </span>
        </div>
      </div>

      {/* Comparison Table / Split View */}
      <div className="rounded-2xl border border-slate-800 bg-slate-900 overflow-hidden shadow-xl">
        {/* Column Headers */}
        <div className="grid grid-cols-1 md:grid-cols-2 border-b border-slate-800 bg-slate-950/80 text-xs font-mono font-bold tracking-wider uppercase text-slate-400">
          <div className="p-4 border-b md:border-b-0 md:border-r border-slate-800 flex items-center justify-between">
            <span>RESUME DEMONSTRATION ({analysis.resumeTitle})</span>
            <span className="text-emerald-400">MATCH STATUS</span>
          </div>
          <div className="p-4 flex items-center justify-between">
            <span>TARGET REQUIREMENTS ({analysis.jobTitle})</span>
            <span className="text-slate-400">IMPORTANCE</span>
          </div>
        </div>

        {/* Rows */}
        <div className="divide-y divide-slate-800/80 text-sm">
          {matches.map((item, idx) => {
            const isMatched = item.matchStatus === 'MATCHED';
            const isRelated = item.matchStatus === 'RELATED';
            const isMissing = item.matchStatus === 'MISSING';

            return (
              <div key={idx} className="grid grid-cols-1 md:grid-cols-2 hover:bg-slate-800/30 transition-colors">
                {/* Left (Resume) */}
                <div className={`p-4 md:border-r border-slate-800/80 flex items-center justify-between ${
                  isMatched
                    ? 'bg-emerald-950/10'
                    : isRelated
                    ? 'bg-amber-950/10'
                    : 'bg-rose-950/5'
                }`}>
                  <div className="flex items-center gap-2.5">
                    <span className={`h-2 w-2 rounded-full shrink-0 ${
                      isMatched ? 'bg-emerald-400' : isRelated ? 'bg-amber-400' : 'bg-rose-400'
                    }`} />
                    <span className={`font-medium ${
                      isMatched ? 'text-emerald-300' : isRelated ? 'text-amber-200' : 'text-slate-500 line-through'
                    }`}>
                      {isMatched
                        ? item.keyword
                        : isRelated
                        ? `${item.relatedTerm} (related to ${item.keyword})`
                        : `Not detected on resume`}
                    </span>
                  </div>

                  <span className={`text-[11px] font-mono font-semibold px-2 py-0.5 rounded ${
                    isMatched
                      ? 'bg-emerald-500/10 text-emerald-400'
                      : isRelated
                      ? 'bg-amber-500/10 text-amber-300'
                      : 'bg-rose-500/10 text-rose-400'
                  }`}>
                    {item.matchStatus}
                  </span>
                </div>

                {/* Right (Job Description) */}
                <div className="p-4 flex items-center justify-between bg-slate-900/40">
                  <div className="flex items-center gap-2">
                    <span className="text-white font-semibold">{item.keyword}</span>
                    <span className="text-xs text-slate-500 font-mono">[{item.skillCategory}]</span>
                  </div>

                  <div className="flex items-center gap-2">
                    {item.isMustHave ? (
                      <span className="px-2 py-0.5 rounded text-[10px] font-bold uppercase bg-rose-500/20 text-rose-300 border border-rose-500/30">
                        Must Have
                      </span>
                    ) : (
                      <span className="px-2 py-0.5 rounded text-[10px] text-slate-400 border border-slate-800">
                        Nice to Have
                      </span>
                    )}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Guidance Card */}
      <div className="rounded-xl border border-slate-800 bg-slate-900/60 p-5 flex items-start gap-3.5 text-xs text-slate-300">
        <HelpCircle className="h-5 w-5 text-emerald-400 shrink-0 mt-0.5" />
        <div className="space-y-1">
          <p className="font-semibold text-white">How ATS parsers evaluate this comparison:</p>
          <p className="text-slate-400 leading-relaxed">
            Leading ATS algorithms parse resumes into structured entity databases before ranking applicants. Aligning keywords with the exact terminology in the right-hand column directly improves your ranking, while yellow concepts ensure partial semantic credit.
          </p>
        </div>
      </div>
    </div>
  );
};
