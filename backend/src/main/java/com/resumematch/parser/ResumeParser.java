package com.resumematch.parser;

import com.resumematch.parser.model.ResumeContent;
import java.io.InputStream;

public interface ResumeParser {

    boolean supports(String fileType);

    ResumeContent parse(InputStream inputStream);
}
