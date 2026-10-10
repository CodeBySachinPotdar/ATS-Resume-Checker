package com.resumematch.service;

import com.resumematch.dto.JobDescriptionDto;
import com.resumematch.dto.JobDescriptionRequest;
import com.resumematch.entity.JobDescription;
import com.resumematch.entity.User;
import com.resumematch.exception.InvalidRequestException;
import com.resumematch.exception.ResourceNotFoundException;
import com.resumematch.repository.JobDescriptionRepository;
import com.resumematch.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JobDescriptionService {

    private final JobDescriptionRepository jobDescriptionRepository;
    private final UserRepository userRepository;

    public JobDescriptionService(JobDescriptionRepository jobDescriptionRepository, UserRepository userRepository) {
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public JobDescriptionDto createJobDescription(JobDescriptionRequest request, Long userId) {
        if (request.getText() == null || request.getText().trim().length() < 30) {
            throw new InvalidRequestException("Job description text is too short to analyze.");
        }

        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

        JobDescription jd = new JobDescription();
        jd.setUser(user);
        jd.setTitle(request.getTitle() != null && !request.getTitle().isBlank() ? request.getTitle() : extractJobTitle(request.getText()));
        jd.setCompany(request.getCompany() != null && !request.getCompany().isBlank() ? request.getCompany() : extractCompany(request.getText()));
        jd.setLocation(request.getLocation());
        jd.setRawText(request.getText());
        jd.setExperienceRequirement(extractExperienceReq(request.getText()));

        JobDescription saved = jobDescriptionRepository.save(jd);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public JobDescriptionDto getJobDescription(Long id) {
        JobDescription jd = jobDescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job description not found with ID: " + id));
        return mapToDto(jd);
    }

    public JobDescriptionDto mapToDto(JobDescription jd) {
        JobDescriptionDto dto = new JobDescriptionDto();
        dto.setId(jd.getId());
        dto.setTitle(jd.getTitle());
        dto.setCompany(jd.getCompany());
        dto.setLocation(jd.getLocation());
        dto.setEmploymentType(jd.getEmploymentType());
        dto.setExperienceRequirement(jd.getExperienceRequirement());
        dto.setRawText(jd.getRawText());
        dto.setCreatedAt(jd.getCreatedAt());
        return dto;
    }

    private String extractJobTitle(String text) {
        String firstLine = text.lines().findFirst().orElse("Software Engineer").trim();
        return firstLine.length() < 60 ? firstLine : "Software Engineer";
    }

    private String extractCompany(String text) {
        Pattern p = Pattern.compile("(?:at|company:?)\\s+([A-Z][a-zA-Z0-9&\\s]{2,30})", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        if (m.find()) {
            return m.group(1).trim();
        }
        return "Target Company";
    }

    private String extractExperienceReq(String text) {
        Pattern p = Pattern.compile("(\\d+\\+?\\s*(?:years|yrs))", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        if (m.find()) {
            return m.group(1);
        }
        return "3+ years";
    }
}
