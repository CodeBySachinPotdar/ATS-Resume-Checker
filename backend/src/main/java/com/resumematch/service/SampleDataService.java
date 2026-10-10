package com.resumematch.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SampleDataService {

    public static final String SAMPLE_JAVA_RESUME = """
            ALEXANDER CHEN
            San Francisco, CA | (415) 555-0192 | alex.chen.dev@example.com
            linkedin.com/in/alexanderchen-dev | github.com/alexchen-cloud

            PROFESSIONAL SUMMARY
            Dedicated Software Engineer with 6+ years of hands-on experience designing and implementing distributed backend services. Proficient in Java, Spring Boot, Microservices, and REST APIs with strong relational database skills in PostgreSQL. Passionate about system scalability and clean code architecture.

            TECHNICAL SKILLS
            Languages: Java 17/21, SQL, Python, TypeScript
            Frameworks: Spring Boot, Spring MVC, Spring Data JPA, Hibernate, JUnit 5
            Architecture & Cloud: Microservices, RESTful APIs, Docker, AWS (S3, EC2), Git
            Databases: PostgreSQL, MySQL, Redis
            Methodologies: Agile, Scrum, CI/CD, Test-Driven Development

            WORK EXPERIENCE
            Senior Software Developer | Nexus Financial Technologies | 2021 - Present
            • Developed Spring Boot REST APIs for a high-volume payment routing gateway, improving transaction throughput by 25%.
            • Refactored legacy monolithic services into modular microservices using Docker containers and PostgreSQL.
            • Implemented Redis caching layers, reducing database read latency by 45% across peak trading periods.
            • Mentored junior engineers and led bi-weekly sprint planning sessions within an Agile framework.

            Software Engineer | CloudScale Systems | 2018 - 2021
            • Worked on Java application development for enterprise supply chain tracking.
            • Created relational database schemas and optimized complex SQL queries in PostgreSQL.
            • Built automated unit and integration tests using JUnit 5 and Mockito, raising code coverage from 62% to 85%.
            • Collaborated with frontend engineers to integrate REST endpoints with modern web dashboards.

            EDUCATION
            Bachelor of Science in Computer Science | University of California, Berkeley | 2018

            CERTIFICATIONS
            • AWS Certified Solutions Architect - Associate
            • Oracle Certified Professional: Java SE 11 Developer
            """;

    public static final String SAMPLE_JAVA_JD = """
            Senior Java Developer - Cloud Payments Platform
            Company: ABC Global FinTech | Location: San Francisco, CA (Hybrid)

            Job Overview:
            We are looking for an experienced Senior Java Developer to join our Cloud Core Platform team. In this role, you will architect, build, and scale mission-critical financial microservices handling millions of daily transactions.

            Key Responsibilities:
            • Design and implement resilient, distributed microservices using Java 21 and Spring Boot.
            • Build real-time event-streaming pipelines utilizing Apache Kafka for asynchronous transaction processing.
            • Architect containerized deployments on Kubernetes (EKS) and AWS infrastructure.
            • Optimize high-volume relational databases using PostgreSQL and in-memory caches using Redis.
            • Drive system design, high availability, fault tolerance, and CI/CD automation pipelines.
            • Collaborate with cross-functional product and engineering teams in an Agile environment.

            Required Qualifications (Must Have):
            • 5+ years of software engineering experience with Java and Spring Boot.
            • Proven expertise with Microservices architecture and distributed systems.
            • Hands-on experience with Apache Kafka or event-driven messaging brokers.
            • Strong experience with PostgreSQL database optimization and Redis caching.
            • Solid proficiency with Docker containerization and Kubernetes.
            • Experience with AWS cloud infrastructure (EC2, S3, RDS, EKS).

            Nice to Have:
            • Terraform infrastructure-as-code experience.
            • Monitoring with Prometheus and Grafana.
            • Background in financial technology, PCI-DSS compliance, or payment processing.
            """;

    public Map<String, String> getSampleJavaPair() {
        return Map.of(
                "resumeTitle", "Alexander Chen - Senior Java Developer",
                "resumeText", SAMPLE_JAVA_RESUME,
                "jobTitle", "Senior Java Developer - Cloud Payments Platform",
                "company", "ABC Global FinTech",
                "jobDescriptionText", SAMPLE_JAVA_JD
        );
    }
}
