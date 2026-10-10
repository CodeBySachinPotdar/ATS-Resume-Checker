import React from 'react';
import { Link } from 'react-router-dom';
import {
  Sparkles,
  FileCheck,
  Search,
  Sliders,
  CheckCircle2,
  ArrowRight,
  Shield,
  Layers,
  BarChart3,
  Cpu,
} from 'lucide-react';

export const LandingPage: React.FC = () => {
  const features = [
    {
      title: 'Estimated ATS Compatibility Score',
      desc: 'Transparent 8-category deterministic scoring weighted across keywords, experience alignment, structure, and semantic fit.',
      icon: BarChart3,
    },
    {
      title: 'Keyword & Semantic Matching',
      desc: 'Identifies exact matches, missing technologies, and related conceptual skills (e.g. REST APIs vs RESTful Web Services).',
      icon: Search,
    },
    {
      title: 'Missing Skills Detection',
      desc: 'Distinguishes between must-have core requirements and optional pluses without encouraging skill falsification.',
      icon: Layers,
    },
    {
      title: 'Resume Formatting Analysis',
      desc: 'Scans for ATS layout hazards such as tables, multiple columns, text boxes, and non-standard heading conventions.',
      icon: FileCheck,
    },
    {
      title: 'Actionable Bullet Optimization',
      desc: 'Rewrites weak bullets into high-impact Google-style achievements with action verbs, scale, and metric placeholders.',
      icon: Sliders,
    },
    {
      title: 'ATS-Friendly Multi-Template Export',
      desc: 'Export optimized resumes in PDF, DOCX, or TXT formats designed specifically to parse flawlessly through ATS systems.',
      icon: Cpu,
    },
  ];

  return (
    <div className="flex flex-col min-h-screen">
      {/* Hero Section */}
      <section className="relative overflow-hidden py-20 sm:py-28 lg:py-32 border-b border-slate-800/80 bg-[radial-gradient(ellipse_80%_80%_at_50%_-20%,rgba(16,185,129,0.15),rgba(255,255,255,0))]">
        <div className="mx-auto max-w-5xl px-4 sm:px-6 lg:px-8 text-center space-y-8">
          <div className="inline-flex items-center gap-2 rounded-full border border-emerald-500/30 bg-emerald-500/10 px-4 py-1.5 text-xs font-semibold text-emerald-300">
            <Sparkles className="h-3.5 w-3.5" />
            <span>Next-Generation ATS Resume Intelligence</span>
          </div>

          <h1 className="text-4xl sm:text-6xl lg:text-7xl font-extrabold tracking-tight text-white leading-[1.1]">
            Is Your Resume Ready for the Job?
          </h1>

          <p className="mx-auto max-w-2xl text-base sm:text-xl text-slate-300 leading-relaxed font-light">
            Check how well your resume matches the job description and discover exactly what you should improve to maximize ATS pass rates.
          </p>

          <div className="flex flex-col sm:flex-row items-center justify-center gap-4 pt-4">
            <Link
              to="/check"
              className="w-full sm:w-auto flex items-center justify-center gap-2.5 rounded-xl bg-emerald-500 px-8 py-4 text-base font-bold text-slate-950 hover:bg-emerald-400 transition-all shadow-xl shadow-emerald-500/20"
            >
              <span>Check My Resume</span>
              <ArrowRight className="h-5 w-5" />
            </Link>
            <Link
              to="/check?demo=true"
              className="w-full sm:w-auto flex items-center justify-center gap-2 rounded-xl border border-slate-700 bg-slate-900/80 px-7 py-4 text-base font-semibold text-slate-200 hover:bg-slate-800 hover:text-white transition-colors"
            >
              <span>Load Interactive Demo</span>
            </Link>
          </div>

          <div className="pt-8 flex flex-wrap items-center justify-center gap-6 text-xs text-slate-400">
            <span className="flex items-center gap-1.5">
              <CheckCircle2 className="h-4 w-4 text-emerald-400" /> Upload PDF, DOCX, TXT or Paste
            </span>
            <span className="flex items-center gap-1.5">
              <CheckCircle2 className="h-4 w-4 text-emerald-400" /> Deterministic & AI-Enriched Scoring
            </span>
            <span className="flex items-center gap-1.5">
              <CheckCircle2 className="h-4 w-4 text-emerald-400" /> Anti-Fraud Skill Principles
            </span>
          </div>
        </div>
      </section>

      {/* 3 Step Workflow */}
      <section className="py-20 border-b border-slate-800 bg-slate-900/40">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-2xl mx-auto mb-16 space-y-3">
            <h2 className="text-xs font-mono font-bold tracking-widest text-emerald-400 uppercase">Simple 3-Step Process</h2>
            <p className="text-3xl font-bold text-white tracking-tight">How ResumeMatch AI Works</p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="rounded-2xl border border-slate-800 bg-slate-900 p-7 space-y-4 relative">
              <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-emerald-500/10 text-emerald-400 font-mono font-bold text-lg border border-emerald-500/20">
                01
              </div>
              <h3 className="text-lg font-bold text-white">Input Resume & Job Description</h3>
              <p className="text-sm text-slate-400 leading-relaxed">
                Upload your resume (PDF, DOCX, TXT) or paste raw text. Paste the target job description to match against.
              </p>
            </div>

            <div className="rounded-2xl border border-slate-800 bg-slate-900 p-7 space-y-4 relative">
              <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-blue-500/10 text-blue-400 font-mono font-bold text-lg border border-blue-500/20">
                02
              </div>
              <h3 className="text-lg font-bold text-white">Deterministic & Semantic Audit</h3>
              <p className="text-sm text-slate-400 leading-relaxed">
                Our hybrid engine extracts hard/soft skills, measures experience alignment, checks formatting traps, and scores compatibility.
              </p>
            </div>

            <div className="rounded-2xl border border-slate-800 bg-slate-900 p-7 space-y-4 relative">
              <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-purple-500/10 text-purple-400 font-mono font-bold text-lg border border-purple-500/20">
                03
              </div>
              <h3 className="text-lg font-bold text-white">Improve & Export ATS Resume</h3>
              <p className="text-sm text-slate-400 leading-relaxed">
                Review bullet rewrites, accept authentic recommendations, and download a cleanly structured ATS-friendly document.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Feature Grid */}
      <section className="py-20 bg-slate-950">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 space-y-12">
          <div className="text-center max-w-2xl mx-auto space-y-3">
            <h2 className="text-xs font-mono font-bold tracking-widest text-emerald-400 uppercase">Engine Capabilities</h2>
            <p className="text-3xl font-bold text-white tracking-tight">Built with Real ATS Mechanics in Mind</p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {features.map((f, i) => {
              const Icon = f.icon;
              return (
                <div key={i} className="rounded-xl border border-slate-800 bg-slate-900/60 p-6 space-y-3 hover:border-slate-700 transition-colors">
                  <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-emerald-500/10 text-emerald-400">
                    <Icon className="h-5 w-5" />
                  </div>
                  <h3 className="text-base font-bold text-white">{f.title}</h3>
                  <p className="text-xs text-slate-400 leading-relaxed">{f.desc}</p>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      {/* Anti-Fraud Callout */}
      <section className="py-16 border-t border-slate-800 bg-slate-900/30">
        <div className="mx-auto max-w-4xl px-4 text-center space-y-4">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-500/10 text-emerald-400 text-xs font-bold border border-emerald-500/20">
            <Shield className="h-4 w-4" />
            <span>Anti-Fraud Guarantee</span>
          </div>
          <h2 className="text-2xl sm:text-3xl font-bold text-white tracking-tight">
            We Never Advise Fabricating Skills
          </h2>
          <p className="text-sm text-slate-300 max-w-2xl mx-auto leading-relaxed">
            Applicant Tracking Systems filter resumes, but human hiring managers conduct the interviews. ResumeMatch AI only recommends highlighting skills and metrics that you genuinely possess.
          </p>
          <div className="pt-4">
            <Link
              to="/check"
              className="inline-flex items-center gap-2 px-6 py-3 rounded-xl bg-emerald-500 text-slate-950 font-bold text-sm hover:bg-emerald-400 transition-all"
            >
              Analyze Your Resume Now
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
};
