import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Sparkles, Check, ArrowRight, ArrowLeft, ShieldAlert, Sliders, CheckCircle2 } from 'lucide-react';
import { AnalysisResponseDto, RecommendationDto } from '../types';
import { BulletComparisonCard } from '../components/BulletComparisonCard';

interface ImprovementPageProps {
  analysis: AnalysisResponseDto | null;
}

export const ImprovementPage: React.FC<ImprovementPageProps> = ({ analysis }) => {
  const [appliedSummary, setAppliedSummary] = useState(false);
  const [appliedBullets, setAppliedBullets] = useState<Record<string, string>>({});

  if (!analysis) {
    return (
      <div className="mx-auto max-w-3xl px-4 py-20 text-center space-y-4">
        <h2 className="text-xl font-bold text-white">No Analysis Available</h2>
        <p className="text-sm text-slate-400">Run an ATS check first to generate optimization suggestions.</p>
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

  // Sample or extracted bullet improvements from analysis recommendations
  const bulletImprovements = (analysis.recommendations || []).filter(
    (r) => r.originalBullet && r.improvedBullet
  );

  const fallbackBullets: RecommendationDto[] = [
    {
      id: 1,
      priorityLevel: 'HIGH',
      sectionTarget: 'EXPERIENCE',
      title: 'Strengthen action verb and specify technical throughput',
      originalBullet: 'Worked on Java application development and bug fixing.',
      problemIdentified: "Passive phrasing ('Worked on') with no mention of technology stack, scale, or business outcome.",
      improvedBullet:
        'Architected high-throughput Spring Boot REST microservices handling 1.5M+ daily API requests, reducing p99 response latency by 28%.',
      whyBetter:
        'Replaced passive verb with decisive technical action ("Architected"), specified key framework ("Spring Boot"), and highlighted business throughput metrics.',
      antiFraudNote: 'Insert your actual team request volume and latency figures. Do not fabricate metrics.',
    },
    {
      id: 2,
      priorityLevel: 'MEDIUM',
      sectionTarget: 'EXPERIENCE',
      title: 'Highlight database optimization and quantifiable latency improvements',
      originalBullet: 'Created relational database schemas and queries in PostgreSQL.',
      problemIdentified: 'Describes routine job tasks rather than measurable performance optimization.',
      improvedBullet:
        'Designed relational schemas and indexed query plans in PostgreSQL, cutting complex reporting query runtime by 42% for financial reconciliation operations.',
      whyBetter:
        'Demonstrates engineering depth (indexed query plans) and concrete percentage performance gains.',
      antiFraudNote: 'Use genuine query optimization metrics from your professional history.',
    },
  ];

  const displayBullets = bulletImprovements.length > 0 ? bulletImprovements : fallbackBullets;

  const handleApplyBullet = (original: string, improved: string) => {
    setAppliedBullets((prev) => ({ ...prev, [original]: improved }));
  };

  const totalAccepted = (appliedSummary ? 1 : 0) + Object.keys(appliedBullets).length;

  return (
    <div className="mx-auto max-w-5xl px-4 py-10 sm:px-6 lg:px-8 space-y-10">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-800 pb-6">
        <div>
          <Link to="/results" className="inline-flex items-center gap-1.5 text-xs text-slate-400 hover:text-white mb-2">
            <ArrowLeft className="h-3.5 w-3.5" />
            <span>Back to Analysis Report</span>
          </Link>
          <div className="flex items-center gap-2">
            <Sliders className="h-6 w-6 text-emerald-400" />
            <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
              Resume Improvement Studio
            </h1>
          </div>
          <p className="text-sm text-slate-400 mt-1">
            Review proposed enhancements item by item. Accept what matches your background; reject what doesn't.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <Link
            to="/export"
            className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs transition-all shadow-md shadow-emerald-500/10"
          >
            <span>Proceed to Export</span>
            <ArrowRight className="h-4 w-4" />
          </Link>
        </div>
      </div>

      {/* Anti-Fraud Banner */}
      <div className="rounded-xl border border-emerald-500/30 bg-emerald-950/15 p-4 flex items-start gap-3.5 text-xs">
        <ShieldAlert className="h-5 w-5 text-emerald-400 shrink-0 mt-0.5" />
        <div className="space-y-1 text-slate-300">
          <p className="font-semibold text-emerald-300">Important Product Principle — Anti-Fraud Safeguard:</p>
          <p className="leading-relaxed text-slate-400">
            Never fabricate metrics or claim experience you do not possess. Our AI generates verified phrasing structures and metric placeholders so you can insert genuine facts from your career.
          </p>
        </div>
      </div>

      {/* Summary Section Improvement */}
      <div className="rounded-2xl border border-slate-800 bg-slate-900 p-6 space-y-5">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Sparkles className="h-4 w-4 text-emerald-400" />
            <h2 className="text-base font-bold text-white uppercase tracking-wider font-mono text-xs">
              PROFESSIONAL SUMMARY REFINEMENT
            </h2>
          </div>
          <button
            onClick={() => setAppliedSummary(!appliedSummary)}
            className={`flex items-center gap-1.5 px-3 py-1 rounded text-xs font-semibold transition-all ${
              appliedSummary
                ? 'bg-emerald-500/20 text-emerald-300 border border-emerald-500/30'
                : 'bg-emerald-500 hover:bg-emerald-400 text-slate-950'
            }`}
          >
            <Check className="h-3.5 w-3.5" />
            {appliedSummary ? 'Summary Accepted' : 'Accept Suggested Summary'}
          </button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs">
          <div className="space-y-1.5">
            <span className="font-semibold text-slate-400 uppercase tracking-wider">Current Summary</span>
            <div className="p-4 rounded-xl bg-slate-950 border border-slate-800 text-slate-300 leading-relaxed min-h-[90px]">
              Dedicated Software Engineer with experience designing and implementing distributed backend services. Proficient in Java, Spring Boot, Microservices, and REST APIs.
            </div>
          </div>

          <div className="space-y-1.5">
            <span className="font-semibold text-emerald-400 uppercase tracking-wider">Suggested ATS-Aligned Summary</span>
            <div className="p-4 rounded-xl bg-emerald-950/20 border border-emerald-500/30 text-emerald-200 leading-relaxed font-medium min-h-[90px]">
              Results-driven Senior Java Developer with 6+ years of expertise architecting high-throughput distributed microservices with Spring Boot, Apache Kafka, and PostgreSQL. Proven track record of delivering resilient cloud architectures and driving measurable latency reductions across mission-critical systems.
            </div>
          </div>
        </div>

        <p className="text-[11px] text-slate-400">
          <strong>Why this is better:</strong> Directly anchors the headline with target title ("Senior Java Developer"), surfaces high-priority technologies within the first two lines, and adds measurable accomplishment framing.
        </p>
      </div>

      {/* Bullet Point Improvement Section */}
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-bold text-white tracking-tight">
            Experience Bullet Refinements ({displayBullets.length})
          </h2>
          <span className="text-xs text-slate-400 font-mono">
            {totalAccepted} improvement{totalAccepted === 1 ? '' : 's'} accepted
          </span>
        </div>

        <div className="space-y-6">
          {displayBullets.map((item, idx) => (
            <BulletComparisonCard key={idx} item={item} onApply={handleApplyBullet} />
          ))}
        </div>
      </div>
    </div>
  );
};
