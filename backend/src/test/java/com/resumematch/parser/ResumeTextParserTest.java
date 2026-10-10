package com.resumematch.parser;

import com.resumematch.parser.model.ResumeContent;
import com.resumematch.service.SampleDataService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResumeTextParserTest {

    @Test
    @DisplayName("Should parse contact info, sections, and bullets from text accurately")
    void testParseFromText() {
        ResumeContent content = ResumeTextParserHelper.parseFromText(SampleDataService.SAMPLE_JAVA_RESUME);

        assertNotNull(content);
        assertNotNull(content.getContactInfo().getEmail());
        assertTrue(content.getContactInfo().getEmail().contains("alex.chen"));
        assertTrue(content.getContactInfo().getPhone().contains("555"));
        assertTrue(content.getContactInfo().getLinkedIn().contains("linkedin.com"));
        assertFalse(content.getSkills().isEmpty());
        assertFalse(content.getExperiences().isEmpty());
        assertEquals("PROFESSIONAL SUMMARY", content.getSummary().isEmpty() ? "" : "PROFESSIONAL SUMMARY");
        assertNotNull(content.getFormattingInspection());
        assertTrue(content.getFormattingInspection().isHasStandardHeadings());
    }
}
