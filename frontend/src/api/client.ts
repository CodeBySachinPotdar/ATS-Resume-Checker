import {
  AnalysisResponseDto,
  OptimizationResponseDto,
  TemplateType,
  UserProfile,
} from '../types';

const API_BASE = ((import.meta as any).env?.VITE_API_URL ? (import.meta as any).env.VITE_API_URL.replace(/\/$/, '') : '') + '/api/v1';

function getAuthHeaders(): HeadersInit {
  const token = localStorage.getItem('token');
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
}

export const api = {
  // Authentication
  async login(email: string, password: string):Promise<{ token: string; user: UserProfile }> {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Login failed. Please check credentials.');
    }
    const data = await res.json();
    localStorage.setItem('token', data.token);
    return {
      token: data.token,
      user: { userId: data.userId, email: data.email, fullName: data.fullName },
    };
  },

  async register(email: string, password: string, fullName: string): Promise<{ token: string; user: UserProfile }> {
    const res = await fetch(`${API_BASE}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password, fullName }),
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Registration failed.');
    }
    const data = await res.json();
    localStorage.setItem('token', data.token);
    return {
      token: data.token,
      user: { userId: data.userId, email: data.email, fullName: data.fullName },
    };
  },

  logout() {
    localStorage.removeItem('token');
  },

  async getCurrentUser(): Promise<UserProfile | null> {
    const token = localStorage.getItem('token');
    if (!token) return null;
    try {
      const res = await fetch(`${API_BASE}/auth/me`, {
        headers: getAuthHeaders(),
      });
      if (!res.ok) return null;
      return await res.json();
    } catch {
      return null;
    }
  },

  // Samples
  async getSampleJava(): Promise<{
    resumeTitle: string;
    resumeText: string;
    jobTitle: string;
    company: string;
    jobDescriptionText: string;
  }> {
    const res = await fetch(`${API_BASE}/samples/java`);
    if (!res.ok) throw new Error('Failed to load sample data.');
    return await res.json();
  },

  // Analysis Quick Check
  async quickCheck(payload: {
    resumeTitle?: string;
    resumeText: string;
    jobTitle?: string;
    company?: string;
    jobDescriptionText: string;
  }): Promise<AnalysisResponseDto> {
    const res = await fetch(`${API_BASE}/analyses/quick-check`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(payload),
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'ATS Analysis failed.');
    }
    return await res.json();
  },

  // File Upload
  async uploadResumeFile(file: File): Promise<{ id: number; title: string; rawText: string }> {
    const formData = new FormData();
    formData.append('file', file);
    const token = localStorage.getItem('token');
    const headers: Record<string, string> = {};
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const res = await fetch(`${API_BASE}/resumes/upload`, {
      method: 'POST',
      headers,
      body: formData,
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Failed to upload resume file.');
    }
    return await res.json();
  },

  // Analysis Retrieval
  async getAnalysis(id: number): Promise<AnalysisResponseDto> {
    const res = await fetch(`${API_BASE}/analyses/${id}`, {
      headers: getAuthHeaders(),
    });
    if (!res.ok) throw new Error('Failed to fetch analysis report.');
    return await res.json();
  },

  async getUserAnalyses(): Promise<AnalysisResponseDto[]> {
    const res = await fetch(`${API_BASE}/analyses`, {
      headers: getAuthHeaders(),
    });
    if (!res.ok) return [];
    return await res.json();
  },

  // Improvements
  async getImprovements(resumeId: number, targetRole?: string): Promise<OptimizationResponseDto> {
    const url = targetRole
      ? `${API_BASE}/resumes/${resumeId}/improvements?targetRole=${encodeURIComponent(targetRole)}`
      : `${API_BASE}/resumes/${resumeId}/improvements`;
    const res = await fetch(url, {
      method: 'POST',
      headers: getAuthHeaders(),
    });
    if (!res.ok) throw new Error('Failed to load improvements.');
    return await res.json();
  },

  async applyOptimizations(resumeId: number, appliedChanges: Record<string, string>): Promise<any> {
    const res = await fetch(`${API_BASE}/resumes/${resumeId}/optimize`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(appliedChanges),
    });
    if (!res.ok) throw new Error('Failed to save optimized resume version.');
    return await res.json();
  },

  // Exports
  getExportUrl(resumeId: number, format: 'pdf' | 'docx' | 'txt', template: TemplateType): string {
    return `${API_BASE}/resumes/${resumeId}/export?format=${format}&template=${template}`;
  },

  async exportPreviewBlob(text: string, format: 'pdf' | 'docx' | 'txt', template: TemplateType): Promise<Blob> {
    const res = await fetch(`${API_BASE}/resumes/export-preview?format=${format}&template=${template}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ text }),
    });
    if (!res.ok) throw new Error('Failed to generate export file.');
    return await res.blob();
  },
};
