package com.resumematch.ats;

import com.resumematch.ats.scoring.AtsScore;
import com.resumematch.entity.JobDescription;
import com.resumematch.entity.Resume;

public interface AtsScoringEngine {

    AtsScore analyze(
            Resume resume,
            JobDescription jobDescription
    );
}
