import React from 'react';
import { X, CheckCircle, AlertTriangle, Lightbulb, HelpCircle, BarChart3 } from 'lucide-react';
import { CategoryBreakdownDetail } from '../types';

interface CategoryModalProps {
  detail: CategoryBreakdownDetail | null;
  onClose: () => void;
}

export const CategoryModal: React.FC<CategoryModalProps> = ({ detail, onClose }) => {
  if (!detail) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="relative w-full max-w-2xl rounded-2xl border border-slate-800 bg-slate-900 p-6 sm:p-8 shadow-2xl space-y-6 max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="flex items-start justify-between border-b border-slate-800 pb-4">
          <div>
            <div className="flex items-center gap-3">
              <h3 className="text-xl font-bold text-white tracking-tight">{detail.title}</h3>
              <span className="px-2.5 py-0.5 rounded-full text-xs font-mono font-bold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                {detail.score}% Score
              </span>
              <span className="text-xs text-slate-400 font-mono">
                Weight: {(detail.weight * 100).toFixed(0)}%
              </span>
            </div>
            <p className="text-xs text-slate-400 mt-1">
              Weighted contribution to overall ATS score: <strong>{detail.weightedContribution} pts</strong>
            </p>
          </div>
          <button
            onClick={onClose}
            className="rounded-lg p-1.5 text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        {/* Why Assigned */}
        <div className="space-y-2">
          <div className="flex items-center gap-2 text-sm font-semibold text-slate-200">
            <HelpCircle className="h-4 w-4 text-blue-400" />
            <span>Why this score was assigned</span>
          </div>
          <div className="rounded-xl border border-slate-800 bg-slate-950/60 p-4 text-sm text-slate-300 leading-relaxed">
            {detail.whyAssigned}
          </div>
        </div>

        {/* What was detected */}
        {detail.whatDetected && detail.whatDetected.length > 0 && (
          <div className="space-y-2">
            <div className="flex items-center gap-2 text-sm font-semibold text-emerald-400">
              <CheckCircle className="h-4 w-4" />
              <span>What was detected ({detail.whatDetected.length})</span>
            </div>
            <div className="rounded-xl border border-emerald-950/40 bg-emerald-950/10 p-4">
              <ul className="space-y-1.5 text-sm text-slate-300">
                {detail.whatDetected.map((item, idx) => (
                  <li key={idx} className="flex items-start gap-2">
                    <span className="text-emerald-400 text-xs mt-1">✓</span>
                    <span>{item}</span>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        )}

        {/* What is missing */}
        {detail.whatMissing && detail.whatMissing.length > 0 && (
          <div className="space-y-2">
            <div className="flex items-center gap-2 text-sm font-semibold text-amber-400">
              <AlertTriangle className="h-4 w-4" />
              <span>What is missing or weak ({detail.whatMissing.length})</span>
            </div>
            <div className="rounded-xl border border-amber-950/40 bg-amber-950/10 p-4">
              <ul className="space-y-1.5 text-sm text-slate-300">
                {detail.whatMissing.map((item, idx) => (
                  <li key={idx} className="flex items-start gap-2">
                    <span className="text-amber-400 text-xs mt-1">⚠</span>
                    <span>{item}</span>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        )}

        {/* How to improve */}
        {detail.howToImprove && detail.howToImprove.length > 0 && (
          <div className="space-y-2">
            <div className="flex items-center gap-2 text-sm font-semibold text-indigo-400">
              <Lightbulb className="h-4 w-4" />
              <span>How you can improve this score</span>
            </div>
            <div className="rounded-xl border border-indigo-950/40 bg-indigo-950/10 p-4">
              <ul className="space-y-2 text-sm text-slate-300">
                {detail.howToImprove.map((item, idx) => (
                  <li key={idx} className="flex items-start gap-2.5">
                    <span className="flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-indigo-500/20 text-indigo-300 text-xs font-bold">
                      {idx + 1}
                    </span>
                    <span className="leading-relaxed">{item}</span>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        )}

        <div className="pt-2 flex justify-end">
          <button
            onClick={onClose}
            className="px-5 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-white text-sm font-medium transition-colors"
          >
            Close Details
          </button>
        </div>
      </div>
    </div>
  );
};
