package com.resumematch.parser.docx;

import com.resumematch.exception.ResumeParsingException;
import com.resumematch.parser.ResumeParser;
import com.resumematch.parser.ResumeTextParserHelper;
import com.resumematch.parser.model.ResumeContent;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class DocxResumeParser implements ResumeParser {

    @Override
    public boolean supports(String fileType) {
        if (fileType == null) return false;
        String type = fileType.toLowerCase().trim();
        return type.endsWith("docx") || type.endsWith("doc") || type.contains("word");
    }

    @Override
    public ResumeContent parse(InputStream inputStream) {
        try {
            try (XWPFDocument document = new XWPFDocument(inputStream)) {
                StringBuilder sb = new StringBuilder();

                // Paragraphs
                List<XWPFParagraph> paragraphs = document.getParagraphs();
                for (XWPFParagraph p : paragraphs) {
                    String text = p.getText();
                    if (text != null && !text.isBlank()) {
                        sb.append(text).append("\n");
                    }
                }

                // Tables
                List<XWPFTable> tables = document.getTables();
                boolean hadTables = !tables.isEmpty();
                for (XWPFTable table : tables) {
                    table.getRows().forEach(row -> {
                        row.getTableCells().forEach(cell -> {
                            String cellText = cell.getText();
                            if (cellText != null && !cellText.isBlank()) {
                                sb.append(cellText).append("\t");
                            }
                        });
                        sb.append("\n");
                    });
                }

                String fullText = sb.toString();
                if (fullText.trim().length() < 20) {
                    throw new ResumeParsingException("The DOCX document contains insufficient readable text.");
                }

                ResumeContent content = ResumeTextParserHelper.parseFromText(fullText);
                if (hadTables) {
                    content.getFormattingInspection().setHasTables(true);
                    content.getFormattingInspection().setScore(Math.max(40.0, content.getFormattingInspection().getScore() - 15.0));
                    content.getFormattingInspection().getWarningIssues().add(
                            "Document contains Word tables. ATS engines frequently parse table cell contents out of chronological order."
                    );
                }
                return content;
            }
        } catch (ResumeParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ResumeParsingException("Failed to parse Word document: " + e.getMessage(), e);
        }
    }
}
