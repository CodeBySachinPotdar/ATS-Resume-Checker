import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Sparkles, User, Lock, Mail, ArrowRight, AlertCircle } from 'lucide-react';
import { api } from '../api/client';
import { UserProfile } from '../types';

interface LoginPageProps {
  onLoginSuccess: (user: UserProfile) => void;
}

export const LoginPage: React.FC<LoginPageProps> = ({ onLoginSuccess }) => {
  const navigate = useNavigate();
  const [isRegister, setIsRegister] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [fullName, setFullName] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      if (isRegister) {
        const res = await api.register(email, password, fullName);
        onLoginSuccess(res.user);
      } else {
        const res = await api.login(email, password);
        onLoginSuccess(res.user);
      }
      navigate('/dashboard');
    } catch (err: any) {
      setError(err.message || 'Authentication failed.');
    } finally {
      setLoading(false);
    }
  };

  const handleFillDemo = () => {
    setEmail('demo@resumematch.ai');
    setPassword('password123');
    setIsRegister(false);
    setError(null);
  };

  return (
    <div className="mx-auto max-w-md px-4 py-16 sm:px-6 space-y-6">
      <div className="text-center space-y-2">
        <div className="inline-flex h-12 w-12 items-center justify-center rounded-2xl bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
          <Sparkles className="h-6 w-6" />
        </div>
        <h1 className="text-2xl font-extrabold text-white tracking-tight">
          {isRegister ? 'Create Your Account' : 'Welcome to ResumeMatch AI'}
        </h1>
        <p className="text-xs text-slate-400">
          {isRegister
            ? 'Access historical analyses and version tracking'
            : 'Sign in to access your saved resume evaluations'}
        </p>
      </div>

      {/* Demo Account Quick-Fill Card */}
      <div className="rounded-xl border border-emerald-500/30 bg-emerald-950/20 p-4 flex items-center justify-between text-xs">
        <div>
          <p className="font-semibold text-emerald-300">Quick Test Account</p>
          <p className="text-slate-400 text-[11px]">demo@resumematch.ai / password123</p>
        </div>
        <button
          type="button"
          onClick={handleFillDemo}
          className="px-3 py-1.5 rounded-lg bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-[11px] transition-all"
        >
          Fill Demo
        </button>
      </div>

      {error && (
        <div className="rounded-xl border border-rose-500/30 bg-rose-500/10 p-3.5 flex items-start gap-2.5 text-xs text-rose-300">
          <AlertCircle className="h-4 w-4 shrink-0 mt-0.5" />
          <span>{error}</span>
        </div>
      )}

      {/* Form */}
      <div className="rounded-2xl border border-slate-800 bg-slate-900 p-6 shadow-xl">
        <form onSubmit={handleSubmit} className="space-y-4 text-xs">
          {isRegister && (
            <div>
              <label className="block font-medium text-slate-300 mb-1">Full Name</label>
              <div className="relative">
                <User className="absolute left-3 top-2.5 h-4 w-4 text-slate-500" />
                <input
                  type="text"
                  required
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  placeholder="e.g. Alexander Chen"
                  className="w-full rounded-xl border border-slate-800 bg-slate-950 pl-9 pr-3.5 py-2.5 text-white placeholder-slate-600 focus:border-emerald-500 focus:outline-none"
                />
              </div>
            </div>
          )}

          <div>
            <label className="block font-medium text-slate-300 mb-1">Email Address</label>
            <div className="relative">
              <Mail className="absolute left-3 top-2.5 h-4 w-4 text-slate-500" />
              <input
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="name@example.com"
                className="w-full rounded-xl border border-slate-800 bg-slate-950 pl-9 pr-3.5 py-2.5 text-white placeholder-slate-600 focus:border-emerald-500 focus:outline-none"
              />
            </div>
          </div>

          <div>
            <label className="block font-medium text-slate-300 mb-1">Password</label>
            <div className="relative">
              <Lock className="absolute left-3 top-2.5 h-4 w-4 text-slate-500" />
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full rounded-xl border border-slate-800 bg-slate-950 pl-9 pr-3.5 py-2.5 text-white placeholder-slate-600 focus:border-emerald-500 focus:outline-none"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full flex items-center justify-center gap-2 py-3 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs transition-all shadow-md shadow-emerald-500/10 mt-2"
          >
            <span>{loading ? 'Authenticating...' : isRegister ? 'Create Account' : 'Sign In'}</span>
            <ArrowRight className="h-4 w-4" />
          </button>
        </form>

        <div className="mt-5 text-center text-xs text-slate-400 border-t border-slate-800/80 pt-4">
          <button
            type="button"
            onClick={() => {
              setIsRegister(!isRegister);
              setError(null);
            }}
            className="hover:text-emerald-400 transition-colors"
          >
            {isRegister ? 'Already have an account? Sign in here' : "Don't have an account? Create one"}
          </button>
        </div>
      </div>
    </div>
  );
};
