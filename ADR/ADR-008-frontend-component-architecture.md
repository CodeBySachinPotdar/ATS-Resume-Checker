# ADR-008: React + Vite + Tailwind CSS Single-Page Application Architecture

## Status
Accepted

## Context
Candidates reviewing ATS scores require fast feedback loops, side-by-side comparative views, interactive modals, and real-time stage progress without full-page reloads.

## Decision
We chose a modern frontend architecture:
- **Vite + React 18 + TypeScript**: Sub-second Hot Module Replacement (HMR) and strict compile-time type safety.
- **Tailwind CSS**: Utility-first CSS providing a sleek dark-mode SaaS interface with customized emerald/slate palettes.
- **Lucide Icons**: Accessible, crisp vector SVG iconography.
- **React Router DOM 6**: Declarative client-side routing across Landing, Checker, Results, Comparison, Improvement, Export, and Dashboard views.
- **Interactive Inspection Modals**: Clickable category breakdowns provide detailed reasoning on demand without cluttering the primary gauge dashboard.

## Consequences
- **Positive**: Blazing fast client bundle (<260KB gzipped); responsive mobile/desktop layouts; clean separation of concerns.
- **Negative**: Single-page apps require client-side routing fallback configuration on production Nginx web servers (handled via `try_files $uri $uri/ /index.html`).
