export type AtsCategory = 'EXCELLENT_MATCH' | 'STRONG_MATCH' | 'GOOD_MATCH' | 'NEEDS_IMPROVEMENT' | 'POOR_MATCH';

export type MatchStatus = 'MATCHED' | 'RELATED' | 'MISSING';

export type PriorityLevel = 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'LOW' | 'OPTIONAL';

export type SkillCategory = 'TECHNICAL' | 'SOFT_SKILL' | 'TOOL_FRAMEWORK' | 'DATABASE' | 'DEVOPS_CLOUD' | 'METHODOLOGY';

export type TemplateType = 'CLASSIC_PROFESSIONAL' | 'MODERN_MINIMAL' | 'SOFTWARE_ENGINEER' | 'EXECUTIVE';

export interface CategoryBreakdownDetail {
  categoryKey: string;
  title: string;
  score: number;
  weight: number;
  weightedContribution: number;
  whyAssigned: string;
  whatDetected: string[];
  whatMissing: string[];
  howToImprove: string[];
}

export interface ScoreBreakdownDto {
  keywordMatchScore: number;
  requiredSkillsScore: number;
  experienceAlignmentScore: number;
  jobTitleAlignmentScore: number;
  semanticSimilarityScore: number;
  resumeStructureScore: number;
  achievementQualityScore: number;
  formattingCompatibilityScore: number;
  breakdownDetails?: Record<string, CategoryBreakdownDetail>;
}

export interface KeywordMatchDto {
  id?: number;
  keyword: string;
  matchStatus: MatchStatus;
  skillCategory: SkillCategory;
  priorityLevel: PriorityLevel;
  relatedTerm?: string;
  importanceExplanation?: string;
  isMustHave?: boolean;
}

export interface RecommendationDto {
  id?: number;
  priorityLevel: PriorityLevel;
  sectionTarget: string;
  title: string;
  description?: string;
  antiFraudNote?: string;
  originalBullet?: string;
  problemIdentified?: string;
  improvedBullet?: string;
  whyBetter?: string;
  isApplied?: boolean;
}

export interface AnalysisResponseDto {
  id: number;
  resumeId: number;
  resumeTitle: string;
  jobDescriptionId: number;
  jobTitle: string;
  company: string;
  overallScore: number;
  scoreCategory: AtsCategory;
  status: string;
  formattingScore: number;
  processingDurationMs?: number;
  createdAt: string;
  scoreBreakdown?: ScoreBreakdownDto;
  keywordMatches?: KeywordMatchDto[];
  recommendations?: RecommendationDto[];
  topStrengths?: string[];
  criticalGaps?: string[];
  sectionScores?: Record<string, number>;
}

export interface OptimizationResponseDto {
  resumeId: number;
  targetJobTitle: string;
  originalSummary: string;
  improvedSummary: string;
  summaryRationale: string;
  bulletImprovements: RecommendationDto[];
  generalRecommendations?: RecommendationDto[];
}

export interface UserProfile {
  userId: number;
  email: string;
  fullName: string;
}
