import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Sparkles, FileText, CheckCircle2, Sliders, Download, History, User } from 'lucide-react';

interface NavbarProps {
  user: { fullName: string; email: string } | null;
  onLogout: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({ user, onLogout }) => {
  const location = useLocation();

  const navLinks = [
    { to: '/check', label: 'ATS Checker', icon: FileText },
    { to: '/compare', label: 'Resume Comparison', icon: CheckCircle2 },
    { to: '/improve', label: 'Improve Resume', icon: Sliders },
    { to: '/export', label: 'ATS Export', icon: Download },
    { to: '/dashboard', label: 'Dashboard', icon: History },
  ];

  return (
    <header className="sticky top-0 z-40 w-full border-b border-slate-800 bg-slate-950/80 backdrop-blur-md">
      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8">
        {/* Brand */}
        <Link to="/" className="flex items-center gap-2.5 group">
          <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 group-hover:bg-emerald-500/20 transition-all">
            <Sparkles className="h-5 w-5" />
          </div>
          <div className="flex flex-col">
            <span className="text-lg font-bold tracking-tight text-white flex items-center gap-1.5">
              ResumeMatch <span className="text-xs uppercase px-1.5 py-0.5 rounded bg-emerald-500/20 text-emerald-300 font-semibold border border-emerald-500/30">AI</span>
            </span>
            <span className="text-[10px] text-slate-400 -mt-1 font-mono">ATS COMPATIBILITY ENGINE</span>
          </div>
        </Link>

        {/* Navigation items */}
        <nav className="hidden md:flex items-center gap-1">
          {navLinks.map((link) => {
            const Icon = link.icon;
            const isActive = location.pathname === link.to;
            return (
              <Link
                key={link.to}
                to={link.to}
                className={`flex items-center gap-2 px-3.5 py-2 rounded-lg text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-slate-800 text-emerald-400 shadow-sm border border-slate-700/60'
                    : 'text-slate-300 hover:text-white hover:bg-slate-900'
                }`}
              >
                <Icon className={`h-4 w-4 ${isActive ? 'text-emerald-400' : 'text-slate-400'}`} />
                {link.label}
              </Link>
            );
          })}
        </nav>

        {/* User / Actions */}
        <div className="flex items-center gap-3">
          {user ? (
            <div className="flex items-center gap-3">
              <div className="hidden sm:flex flex-col text-right">
                <span className="text-xs font-medium text-slate-200">{user.fullName}</span>
                <span className="text-[11px] text-slate-400">{user.email}</span>
              </div>
              <button
                onClick={onLogout}
                className="px-3 py-1.5 rounded-lg border border-slate-800 bg-slate-900 text-xs font-medium text-slate-300 hover:bg-slate-800 hover:text-white transition-colors"
              >
                Sign Out
              </button>
            </div>
          ) : (
            <div className="flex items-center gap-2">
              <Link
                to="/login"
                className="flex items-center gap-1.5 px-3.5 py-2 rounded-lg text-xs font-semibold text-slate-300 hover:text-white hover:bg-slate-900 transition-colors"
              >
                <User className="h-3.5 w-3.5" />
                Sign In
              </Link>
              <Link
                to="/check"
                className="px-4 py-2 rounded-lg text-xs font-semibold bg-emerald-500 hover:bg-emerald-400 text-slate-950 shadow-md shadow-emerald-500/10 transition-all font-medium"
              >
                Check Resume
              </Link>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
