package com.resumematch.parser.txt;

import com.resumematch.exception.ResumeParsingException;
import com.resumematch.parser.ResumeParser;
import com.resumematch.parser.ResumeTextParserHelper;
import com.resumematch.parser.model.ResumeContent;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class TxtResumeParser implements ResumeParser {

    @Override
    public boolean supports(String fileType) {
        if (fileType == null) return false;
        String type = fileType.toLowerCase().trim();
        return type.endsWith("txt") || type.contains("text") || type.equals("raw_text");
    }

    @Override
    public ResumeContent parse(InputStream inputStream) {
        try {
            byte[] bytes = inputStream.readAllBytes();
            String text = new String(bytes, StandardCharsets.UTF_8);
            if (text.trim().length() < 20) {
                throw new ResumeParsingException("Resume text is empty or too short for a meaningful analysis.");
            }
            return ResumeTextParserHelper.parseFromText(text);
        } catch (ResumeParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ResumeParsingException("Failed to parse text resume: " + e.getMessage(), e);
        }
    }
}
