# ADR-002: Multi-Format Document Parsing Strategy

## Status
Accepted

## Context
Candidates submit resumes in various document formats, predominantly PDF, Microsoft Word DOCX, and plain text. PDF files often contain complex internal object streams, two-column column layouts, tables, or scanned bitmap graphics. DOCX files contain nested XML paragraph and table hierarchies.

## Decision
We implemented a polymorphic Parser Factory pattern centered around a common interface:
```java
public interface ResumeParser {
    boolean supports(String fileType);
    ResumeContent parse(InputStream inputStream);
}
```
Implementations:
- `PdfResumeParser`: Built on Apache PDFBox 3.x with position-sorted text extraction (`PDFTextStripper`), encryption detection, and scanned image warnings.
- `DocxResumeParser`: Built on Apache POI 5.x (`XWPFDocument`) extracting paragraphs and table cells while flagging layout anomalies.
- `TxtResumeParser`: UTF-8 stream processing.

All parsers feed raw extracted text into `ResumeTextParserHelper`, which identifies sections, parses contact details via RFC-compliant regexes, categorizes skills, and performs a dedicated formatting inspection for ATS compatibility traps.

## Consequences
- **Positive**: Zero coupling between upload endpoints and document formats; uniform internal `ResumeContent` representation; automatic layout trap warnings.
- **Negative**: Scanned image PDFs without text layers require OCR pre-processing.
