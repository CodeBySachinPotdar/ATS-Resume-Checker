import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  FileText,
  Sparkles,
  ArrowRight,
  History,
  TrendingUp,
  Clock,
  Layers,
  ChevronRight,
} from 'lucide-react';
import { AnalysisResponseDto } from '../types';
import { api } from '../api/client';
import { ScoreGauge } from '../components/ScoreGauge';

interface DashboardPageProps {
  currentAnalysis: AnalysisResponseDto | null;
  onSelectAnalysis: (analysis: AnalysisResponseDto) => void;
}

export const DashboardPage: React.FC<DashboardPageProps> = ({ currentAnalysis, onSelectAnalysis }) => {
  const [history, setHistory] = useState<AnalysisResponseDto[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadUserHistory();
  }, []);

  const loadUserHistory = async () => {
    setLoading(true);
    try {
      const list = await api.getUserAnalyses();
      setHistory(list);
    } catch {
      // Fallback to active current analysis if exists
      if (currentAnalysis) {
        setHistory([currentAnalysis]);
      }
    } finally {
      setLoading(false);
    }
  };

  const active = currentAnalysis || (history.length > 0 ? history[0] : null);

  return (
    <div className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8 space-y-10">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-800 pb-6">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
            ATS Candidate Dashboard
          </h1>
          <p className="text-sm text-slate-400 mt-1">
            Track evaluation metrics across multiple target applications and resume versions.
          </p>
        </div>
        <Link
          to="/check"
          className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs transition-all shadow-md shadow-emerald-500/10 self-start sm:self-auto"
        >
          <Sparkles className="h-4 w-4" />
          <span>New ATS Analysis</span>
        </Link>
      </div>

      {active ? (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Active Evaluation Overview Card */}
          <div className="rounded-2xl border border-slate-800 bg-slate-900/90 p-6 flex flex-col justify-between space-y-6 shadow-xl">
            <div className="space-y-4">
              <div className="flex items-center justify-between border-b border-slate-800 pb-3">
                <span className="text-xs font-mono font-bold text-emerald-400 uppercase tracking-wider">
                  ACTIVE TARGET JOB
                </span>
                <span className="text-[11px] text-slate-500 font-mono">
                  {new Date(active.createdAt).toLocaleDateString()}
                </span>
              </div>

              <div>
                <h3 className="text-lg font-bold text-white">{active.jobTitle}</h3>
                <p className="text-xs text-slate-400">{active.company}</p>
                <p className="text-xs text-slate-500 mt-2 font-mono">
                  Resume: {active.resumeTitle}
                </p>
              </div>

              <div className="py-2">
                <ScoreGauge score={active.overallScore} category={active.scoreCategory} size="md" />
              </div>
            </div>

            <div className="space-y-2 pt-4 border-t border-slate-800">
              <Link
                to="/results"
                className="w-full flex items-center justify-center gap-2 py-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-white text-xs font-semibold transition-colors"
              >
                <span>View Full ATS Report</span>
                <ArrowRight className="h-3.5 w-3.5" />
              </Link>
              <Link
                to="/improve"
                className="w-full flex items-center justify-center gap-2 py-2.5 rounded-xl bg-blue-600/20 hover:bg-blue-600/30 text-blue-300 text-xs font-semibold border border-blue-500/30 transition-colors"
              >
                <span>Improve Bullet Points</span>
              </Link>
            </div>
          </div>

          {/* Score Breakdown & Top Improvements */}
          <div className="lg:col-span-2 space-y-6">
            {/* Score Breakdown Bars */}
            <div className="rounded-2xl border border-slate-800 bg-slate-900/70 p-6 space-y-4">
              <h3 className="text-sm font-bold text-white uppercase tracking-wider font-mono">
                Key Match Dimensions
              </h3>

              <div className="space-y-3 text-xs">
                <div>
                  <div className="flex justify-between text-slate-300 mb-1">
                    <span>Keyword Match</span>
                    <span className="font-mono font-bold text-white">
                      {Math.round(active.scoreBreakdown?.keywordMatchScore || 85)}%
                    </span>
                  </div>
                  <div className="h-2 w-full rounded-full bg-slate-800 overflow-hidden">
                    <div
                      className="h-full rounded-full bg-emerald-400 transition-all duration-700"
                      style={{ width: `${active.scoreBreakdown?.keywordMatchScore || 85}%` }}
                    />
                  </div>
                </div>

                <div>
                  <div className="flex justify-between text-slate-300 mb-1">
                    <span>Required Skills</span>
                    <span className="font-mono font-bold text-white">
                      {Math.round(active.scoreBreakdown?.requiredSkillsScore || 88)}%
                    </span>
                  </div>
                  <div className="h-2 w-full rounded-full bg-slate-800 overflow-hidden">
                    <div
                      className="h-full rounded-full bg-blue-400 transition-all duration-700"
                      style={{ width: `${active.scoreBreakdown?.requiredSkillsScore || 88}%` }}
                    />
                  </div>
                </div>

                <div>
                  <div className="flex justify-between text-slate-300 mb-1">
                    <span>Experience Match</span>
                    <span className="font-mono font-bold text-white">
                      {Math.round(active.scoreBreakdown?.experienceAlignmentScore || 78)}%
                    </span>
                  </div>
                  <div className="h-2 w-full rounded-full bg-slate-800 overflow-hidden">
                    <div
                      className="h-full rounded-full bg-indigo-400 transition-all duration-700"
                      style={{ width: `${active.scoreBreakdown?.experienceAlignmentScore || 78}%` }}
                    />
                  </div>
                </div>

                <div>
                  <div className="flex justify-between text-slate-300 mb-1">
                    <span>Formatting Compatibility</span>
                    <span className="font-mono font-bold text-white">
                      {Math.round(active.formattingScore || 90)}%
                    </span>
                  </div>
                  <div className="h-2 w-full rounded-full bg-slate-800 overflow-hidden">
                    <div
                      className="h-full rounded-full bg-emerald-400 transition-all duration-700"
                      style={{ width: `${active.formattingScore || 90}%` }}
                    />
                  </div>
                </div>
              </div>
            </div>

            {/* Top Improvements List */}
            <div className="rounded-2xl border border-slate-800 bg-slate-900/70 p-6 space-y-4">
              <h3 className="text-sm font-bold text-white uppercase tracking-wider font-mono">
                Top Priority Improvements
              </h3>

              <div className="space-y-2.5 text-xs text-slate-300">
                {(active.recommendations || []).slice(0, 4).map((rec, i) => (
                  <div key={i} className="flex items-start gap-3 p-3 rounded-xl bg-slate-950 border border-slate-800/80">
                    <span className="flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-emerald-500/20 text-emerald-400 font-bold font-mono text-[11px]">
                      {i + 1}
                    </span>
                    <div>
                      <p className="font-semibold text-slate-200">{rec.title}</p>
                      <p className="text-slate-400 text-[11px] mt-0.5">{rec.description}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      ) : (
        <div className="rounded-2xl border border-slate-800 bg-slate-900 p-12 text-center space-y-4">
          <History className="h-10 w-10 text-slate-600 mx-auto" />
          <h3 className="text-base font-bold text-white">No Previous Analyses</h3>
          <p className="text-xs text-slate-400 max-w-sm mx-auto">
            You haven't run any ATS checks yet. Start with our interactive demo or upload your resume.
          </p>
          <Link
            to="/check?demo=true"
            className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-emerald-500 text-slate-950 font-bold text-xs"
          >
            <span>Run Sample Analysis</span>
            <ArrowRight className="h-3.5 w-3.5" />
          </Link>
        </div>
      )}
    </div>
  );
};
