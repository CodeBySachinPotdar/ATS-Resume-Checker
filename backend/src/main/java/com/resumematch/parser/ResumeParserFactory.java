package com.resumematch.parser;

import com.resumematch.exception.ResumeParsingException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResumeParserFactory {

    private final List<ResumeParser> parsers;

    public ResumeParserFactory(List<ResumeParser> parsers) {
        this.parsers = parsers;
    }

    public ResumeParser getParser(String fileType) {
        if (fileType == null || fileType.isBlank()) {
            // Default to TxtResumeParser if no type given
            return parsers.stream()
                    .filter(p -> p.supports("txt"))
                    .findFirst()
                    .orElseThrow(() -> new ResumeParsingException("No suitable parser found for empty file type."));
        }

        return parsers.stream()
                .filter(p -> p.supports(fileType))
                .findFirst()
                .orElseThrow(() -> new ResumeParsingException("Unsupported file type: " + fileType + ". Please upload PDF, DOCX, or TXT."));
    }
}
