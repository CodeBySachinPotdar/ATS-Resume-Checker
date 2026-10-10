package com.resumematch.repository;

import com.resumematch.entity.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, Long> {

    @Query("SELECT ra FROM ResumeAnalysis ra JOIN ra.resume r WHERE r.user.id = :userId ORDER BY ra.createdAt DESC")
    List<ResumeAnalysis> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT ra FROM ResumeAnalysis ra LEFT JOIN FETCH ra.scoreBreakdown LEFT JOIN FETCH ra.keywordMatches LEFT JOIN FETCH ra.recommendations WHERE ra.id = :id")
    Optional<ResumeAnalysis> findByIdWithDetails(@Param("id") Long id);
}
