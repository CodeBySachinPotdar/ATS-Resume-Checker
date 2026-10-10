import React from 'react';
import { KeywordMatchDto } from '../types';

interface KeywordBadgeProps {
  keyword: KeywordMatchDto;
  showCategory?: boolean;
}

export const KeywordBadge: React.FC<KeywordBadgeProps> = ({ keyword, showCategory = true }) => {
  const getBadgeStyle = () => {
    switch (keyword.matchStatus) {
      case 'MATCHED':
        return 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30 hover:bg-emerald-500/20';
      case 'RELATED':
        return 'bg-amber-500/10 text-amber-300 border-amber-500/30 hover:bg-amber-500/20';
      case 'MISSING':
        return 'bg-rose-500/10 text-rose-300 border-rose-500/30 hover:bg-rose-500/20';
    }
  };

  const getStatusIcon = () => {
    switch (keyword.matchStatus) {
      case 'MATCHED':
        return '✓';
      case 'RELATED':
        return '≈';
      case 'MISSING':
        return '✕';
    }
  };

  return (
    <div
      className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-lg text-xs font-medium border transition-all ${getBadgeStyle()}`}
      title={keyword.importanceExplanation}
    >
      <span className="font-bold">{getStatusIcon()}</span>
      <span>{keyword.keyword}</span>

      {keyword.matchStatus === 'RELATED' && keyword.relatedTerm && (
        <span className="text-[10px] text-amber-400/80 italic font-mono">
          (via {keyword.relatedTerm})
        </span>
      )}

      {keyword.isMustHave && (
        <span className="ml-1 px-1.5 py-0.2 rounded text-[9px] font-bold uppercase bg-rose-500/20 text-rose-300">
          Required
        </span>
      )}
    </div>
  );
};
