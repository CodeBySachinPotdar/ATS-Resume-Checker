package com.resumematch.kafka;

import com.resumematch.service.AsyncAnalysisWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AnalysisEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(AnalysisEventPublisher.class);
    public static final String TOPIC_RESUME_ANALYSIS = "resume-analysis-jobs";

    @Value("${app.kafka.enabled:false}")
    private boolean kafkaEnabled;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AsyncAnalysisWorker asyncWorker;

    public AnalysisEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            AsyncAnalysisWorker asyncWorker
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.asyncWorker = asyncWorker;
    }

    public void publishAnalysisJob(AnalysisJobEvent event) {
        if (kafkaEnabled) {
            try {
                log.info("Publishing analysis job event to Kafka topic {}: analysisId={}", TOPIC_RESUME_ANALYSIS, event.getAnalysisId());
                kafkaTemplate.send(TOPIC_RESUME_ANALYSIS, String.valueOf(event.getAnalysisId()), event);
                return;
            } catch (Exception e) {
                log.warn("Failed to publish to Kafka ({}); failing over to in-process async worker.", e.getMessage());
            }
        }

        // Direct in-process async processing fallback
        log.info("Processing analysis job asynchronously in-process: analysisId={}", event.getAnalysisId());
        asyncWorker.processJob(event.getAnalysisId(), event.getResumeId(), event.getJobDescriptionId());
    }
}
