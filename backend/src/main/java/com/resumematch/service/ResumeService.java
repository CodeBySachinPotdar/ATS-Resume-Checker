package com.resumematch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumematch.dto.ResumeDto;
import com.resumematch.dto.ResumeTextRequest;
import com.resumematch.entity.Resume;
import com.resumematch.entity.User;
import com.resumematch.exception.InvalidRequestException;
import com.resumematch.exception.ResourceNotFoundException;
import com.resumematch.parser.ResumeParser;
import com.resumematch.parser.ResumeParserFactory;
import com.resumematch.parser.ResumeTextParserHelper;
import com.resumematch.parser.model.ResumeContent;
import com.resumematch.repository.ResumeRepository;
import com.resumematch.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ResumeParserFactory parserFactory;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResumeService(
            ResumeRepository resumeRepository,
            UserRepository userRepository,
            ResumeParserFactory parserFactory
    ) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.parserFactory = parserFactory;
    }

    @Transactional
    public ResumeDto uploadResumeFile(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException("Uploaded file cannot be empty.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "uploaded_resume";
        }

        String extension = getFileExtension(originalFilename);
        ResumeParser parser = parserFactory.getParser(extension);

        try (InputStream is = file.getInputStream()) {
            ResumeContent parsed = parser.parse(is);

            User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

            Resume resume = new Resume();
            resume.setUser(user);
            resume.setTitle(originalFilename.replaceFirst("\\.[^.]+$", ""));
            resume.setFileName(originalFilename);
            resume.setFileType(extension.toUpperCase());
            resume.setFileSize(file.getSize());
            resume.setRawText(parsed.getRawText());
            resume.setParsedJson(objectMapper.writeValueAsString(parsed));

            if (parsed.getContactInfo() != null) {
                resume.setCandidateName(parsed.getContactInfo().getName());
                resume.setCandidateEmail(parsed.getContactInfo().getEmail());
                resume.setCandidatePhone(parsed.getContactInfo().getPhone());
            }

            Resume saved = resumeRepository.save(resume);
            return mapToDto(saved);

        } catch (Exception e) {
            throw new InvalidRequestException("Failed to process resume file: " + e.getMessage());
        }
    }

    @Transactional
    public ResumeDto saveResumeText(ResumeTextRequest request, Long userId) {
        if (request.getText() == null || request.getText().trim().length() < 30) {
            throw new InvalidRequestException("Resume text is too brief for ATS parsing.");
        }

        ResumeContent parsed = ResumeTextParserHelper.parseFromText(request.getText());
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

        Resume resume = new Resume();
        resume.setUser(user);
        resume.setTitle(request.getTitle() != null ? request.getTitle() : "Pasted Resume");
        resume.setFileType("RAW_TEXT");
        resume.setRawText(request.getText());

        try {
            resume.setParsedJson(objectMapper.writeValueAsString(parsed));
        } catch (Exception ignored) {}

        if (parsed.getContactInfo() != null) {
            resume.setCandidateName(parsed.getContactInfo().getName());
            resume.setCandidateEmail(parsed.getContactInfo().getEmail());
            resume.setCandidatePhone(parsed.getContactInfo().getPhone());
        }

        Resume saved = resumeRepository.save(resume);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public ResumeDto getResume(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with ID: " + id));
        return mapToDto(resume);
    }

    @Transactional(readOnly = true)
    public List<ResumeDto> getUserResumes(Long userId) {
        return resumeRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional
    public void deleteResume(Long id) {
        if (!resumeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resume not found with ID: " + id);
        }
        resumeRepository.deleteById(id);
    }

    public ResumeDto mapToDto(Resume r) {
        ResumeDto dto = new ResumeDto();
        dto.setId(r.getId());
        dto.setTitle(r.getTitle());
        dto.setFileName(r.getFileName());
        dto.setFileType(r.getFileType());
        dto.setFileSize(r.getFileSize());
        dto.setCandidateName(r.getCandidateName());
        dto.setCandidateEmail(r.getCandidateEmail());
        dto.setCandidatePhone(r.getCandidatePhone());
        dto.setRawText(r.getRawText());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }

    private String getFileExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        return (idx > 0) ? filename.substring(idx + 1).toLowerCase() : "txt";
    }
}
