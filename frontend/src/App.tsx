import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Navbar } from './components/Navbar';
import { Footer } from './components/Footer';
import { LandingPage } from './pages/LandingPage';
import { AnalysisInputPage } from './pages/AnalysisInputPage';
import { AnalysisResultPage } from './pages/AnalysisResultPage';
import { ComparisonPage } from './pages/ComparisonPage';
import { ImprovementPage } from './pages/ImprovementPage';
import { ExportPage } from './pages/ExportPage';
import { DashboardPage } from './pages/DashboardPage';
import { LoginPage } from './pages/LoginPage';
import { AnalysisResponseDto, UserProfile } from './types';
import { api } from './api/client';

export const App: React.FC = () => {
  const [user, setUser] = useState<UserProfile | null>(null);
  const [analysis, setAnalysis] = useState<AnalysisResponseDto | null>(() => {
    const saved = localStorage.getItem('last_analysis');
    return saved ? JSON.parse(saved) : null;
  });

  useEffect(() => {
    api.getCurrentUser().then((u) => {
      if (u) setUser(u);
    });
  }, []);

  const handleAnalysisComplete = (result: AnalysisResponseDto) => {
    setAnalysis(result);
    try {
      localStorage.setItem('last_analysis', JSON.stringify(result));
    } catch {}
  };

  const handleLogout = () => {
    api.logout();
    setUser(null);
  };

  return (
    <BrowserRouter>
      <div className="min-h-screen flex flex-col bg-slate-950 text-slate-100">
        <Navbar user={user} onLogout={handleLogout} />

        <main className="flex-1">
          <Routes>
            <Route path="/" element={<LandingPage />} />
            <Route
              path="/check"
              element={<AnalysisInputPage onAnalysisComplete={handleAnalysisComplete} />}
            />
            <Route
              path="/results"
              element={<AnalysisResultPage analysis={analysis} />}
            />
            <Route
              path="/compare"
              element={<ComparisonPage analysis={analysis} />}
            />
            <Route
              path="/improve"
              element={<ImprovementPage analysis={analysis} />}
            />
            <Route
              path="/export"
              element={<ExportPage analysis={analysis} />}
            />
            <Route
              path="/dashboard"
              element={
                <DashboardPage
                  currentAnalysis={analysis}
                  onSelectAnalysis={(a) => setAnalysis(a)}
                />
              }
            />
            <Route
              path="/login"
              element={<LoginPage onLoginSuccess={(u) => setUser(u)} />}
            />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>

        <Footer />
      </div>
    </BrowserRouter>
  );
};

export default App;
