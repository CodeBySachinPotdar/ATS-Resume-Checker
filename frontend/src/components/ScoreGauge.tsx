import React from 'react';
import { AtsCategory } from '../types';

interface ScoreGaugeProps {
  score: number;
  category?: AtsCategory;
  size?: 'sm' | 'md' | 'lg';
}

export const ScoreGauge: React.FC<ScoreGaugeProps> = ({ score, category, size = 'lg' }) => {
  const rounded = Math.round(score);

  const getColorConfig = (s: number) => {
    if (s >= 90) {
      return {
        label: 'Excellent Match',
        textColor: 'text-emerald-400',
        strokeColor: '#34d399',
        badgeBg: 'bg-emerald-500/10 text-emerald-300 border-emerald-500/20',
      };
    }
    if (s >= 80) {
      return {
        label: 'Strong Match',
        textColor: 'text-blue-400',
        strokeColor: '#60a5fa',
        badgeBg: 'bg-blue-500/10 text-blue-300 border-blue-500/20',
      };
    }
    if (s >= 70) {
      return {
        label: 'Good Match',
        textColor: 'text-amber-400',
        strokeColor: '#fbbf24',
        badgeBg: 'bg-amber-500/10 text-amber-300 border-amber-500/20',
      };
    }
    if (s >= 60) {
      return {
        label: 'Needs Improvement',
        textColor: 'text-orange-400',
        strokeColor: '#fb923c',
        badgeBg: 'bg-orange-500/10 text-orange-300 border-orange-500/20',
      };
    }
    return {
      label: 'Poor Match',
      textColor: 'text-rose-400',
      strokeColor: '#f87171',
      badgeBg: 'bg-rose-500/10 text-rose-300 border-rose-500/20',
    };
  };

  const config = getColorConfig(rounded);
  const strokeWidth = size === 'lg' ? 12 : 8;
  const radius = size === 'lg' ? 70 : 44;
  const circumference = 2 * Math.PI * radius;
  const strokeDashoffset = circumference - (rounded / 100) * circumference;

  return (
    <div className="flex flex-col items-center justify-center text-center">
      <div className="relative flex items-center justify-center">
        <svg
          className="transform -rotate-90"
          width={size === 'lg' ? 180 : 110}
          height={size === 'lg' ? 180 : 110}
        >
          {/* Track */}
          <circle
            cx={size === 'lg' ? 90 : 55}
            cy={size === 'lg' ? 90 : 55}
            r={radius}
            stroke="#1e293b"
            strokeWidth={strokeWidth}
            fill="transparent"
          />
          {/* Value Progress */}
          <circle
            cx={size === 'lg' ? 90 : 55}
            cy={size === 'lg' ? 90 : 55}
            r={radius}
            stroke={config.strokeColor}
            strokeWidth={strokeWidth}
            strokeDasharray={circumference}
            strokeDashoffset={strokeDashoffset}
            strokeLinecap="round"
            fill="transparent"
            className="transition-all duration-1000 ease-out"
          />
        </svg>

        <div className="absolute flex flex-col items-center justify-center">
          <span className={`font-extrabold tracking-tight font-mono ${size === 'lg' ? 'text-4xl' : 'text-2xl'} ${config.textColor}`}>
            {rounded}
          </span>
          <span className="text-[10px] text-slate-400 font-semibold uppercase tracking-wider">
            / 100
          </span>
        </div>
      </div>

      <div className="mt-3 flex flex-col items-center gap-1">
        <span className="text-[11px] font-mono tracking-widest text-slate-400 uppercase">
          ESTIMATED ATS SCORE
        </span>
        <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold border ${config.badgeBg}`}>
          {config.label}
        </span>
      </div>
    </div>
  );
};
