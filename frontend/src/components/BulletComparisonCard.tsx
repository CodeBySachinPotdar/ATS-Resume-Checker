import React, { useState } from 'react';
import { Check, X, ArrowDown, ShieldAlert, Sparkles, Copy } from 'lucide-react';
import { RecommendationDto } from '../types';

interface BulletComparisonCardProps {
  item: RecommendationDto;
  onApply?: (original: string, improved: string) => void;
}

export const BulletComparisonCard: React.FC<BulletComparisonCardProps> = ({ item, onApply }) => {
  const [applied, setApplied] = useState(item.isApplied || false);
  const [copied, setCopied] = useState(false);

  const handleApply = () => {
    setApplied(true);
    if (onApply && item.originalBullet && item.improvedBullet) {
      onApply(item.originalBullet, item.improvedBullet);
    }
  };

  const handleCopy = () => {
    if (item.improvedBullet) {
      navigator.clipboard.writeText(item.improvedBullet);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  return (
    <div className={`rounded-xl border transition-all p-5 space-y-4 ${
      applied
        ? 'border-emerald-500/40 bg-emerald-950/10'
        : 'border-slate-800 bg-slate-900/60 hover:border-slate-700'
    }`}>
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Sparkles className="h-4 w-4 text-emerald-400" />
          <span className="text-xs font-semibold text-slate-300 uppercase tracking-wider font-mono">
            {item.sectionTarget || 'EXPERIENCE'} BULLET OPTIMIZATION
          </span>
        </div>
        <div className="flex items-center gap-2">
          <button
            onClick={handleCopy}
            className="flex items-center gap-1 px-2.5 py-1 rounded text-xs font-medium text-slate-400 hover:text-white bg-slate-800 hover:bg-slate-700 transition-colors"
          >
            <Copy className="h-3.5 w-3.5" />
            {copied ? 'Copied!' : 'Copy'}
          </button>
          {onApply && (
            <button
              onClick={handleApply}
              disabled={applied}
              className={`flex items-center gap-1.5 px-3 py-1 rounded text-xs font-semibold transition-all ${
                applied
                  ? 'bg-emerald-500/20 text-emerald-300 border border-emerald-500/30'
                  : 'bg-emerald-500 hover:bg-emerald-400 text-slate-950 shadow-sm'
              }`}
            >
              <Check className="h-3.5 w-3.5" />
              {applied ? 'Applied' : 'Accept Improvement'}
            </button>
          )}
        </div>
      </div>

      {/* Flow: Original -> Problem -> Improved -> Why Better */}
      <div className="space-y-3 text-sm">
        {/* Original */}
        <div className="space-y-1">
          <span className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">
            Original Statement
          </span>
          <div className="p-3 rounded-lg bg-slate-950/70 border border-slate-800/80 text-slate-300 font-mono text-xs">
            {item.originalBullet || item.description}
          </div>
        </div>

        {/* Problem */}
        {item.problemIdentified && (
          <div className="flex items-start gap-2 text-xs text-amber-300 bg-amber-500/10 border border-amber-500/20 p-2.5 rounded-lg">
            <span className="font-bold shrink-0">Problem:</span>
            <span>{item.problemIdentified}</span>
          </div>
        )}

        <div className="flex justify-center text-slate-600">
          <ArrowDown className="h-4 w-4" />
        </div>

        {/* Improved */}
        <div className="space-y-1">
          <span className="text-[11px] font-semibold text-emerald-400 uppercase tracking-wider">
            Optimized Recommendation
          </span>
          <div className="p-3 rounded-lg bg-emerald-950/20 border border-emerald-500/30 text-emerald-200 text-sm font-medium leading-relaxed">
            {item.improvedBullet}
          </div>
        </div>

        {/* Why Better */}
        {item.whyBetter && (
          <div className="text-xs text-slate-400 space-y-0.5">
            <span className="font-semibold text-slate-300">Why it is better: </span>
            <span>{item.whyBetter}</span>
          </div>
        )}

        {/* Anti-Fraud Note */}
        {item.antiFraudNote && (
          <div className="flex items-start gap-2 text-[11px] text-slate-400 border-t border-slate-800/60 pt-2.5">
            <ShieldAlert className="h-4 w-4 text-emerald-400 shrink-0 mt-0.5" />
            <span className="italic">{item.antiFraudNote}</span>
          </div>
        )}
      </div>
    </div>
  );
};
