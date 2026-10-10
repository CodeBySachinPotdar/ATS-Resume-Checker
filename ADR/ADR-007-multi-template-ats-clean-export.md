# ADR-007: Multi-Template ATS-Clean Document Export Architecture

## Status
Accepted

## Context
Many modern resume builder tools output visually stylized resumes with tables, icons, and dual columns. While visually attractive to humans, these decorative elements frequently scramble text streams in enterprise ATS parsers.

## Decision
We engineered a dedicated server-side export subsystem supporting 4 ATS-safe templates:
1. `CLASSIC_PROFESSIONAL`: Standard serif-neutral single-column layout.
2. `MODERN_MINIMAL`: Contemporary typography with clear whitespace hierarchy.
3. `SOFTWARE_ENGINEER`: Places core tech competencies and repositories prominently at the top.
4. `EXECUTIVE`: Emphasizes strategic accomplishments and leadership scope.

### Technology:
- **PDF**: Apache PDFBox 3.x generating pure vector text streams with Standard-14 fonts. Zero image-rasterized text.
- **DOCX**: Apache POI XWPF generating standard Word paragraphs and bullet styles without nested tables.
- **TXT**: Plain UTF-8 formatted text.

## Consequences
- **Positive**: Resumes generated are guaranteed 100% machine-readable without table re-ordering glitches; works in any standard PDF reader or Microsoft Word.
- **Negative**: Visual layouts are intentionally minimal and avoid complex graphic flourishes.
