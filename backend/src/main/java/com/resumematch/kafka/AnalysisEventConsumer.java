package com.resumematch.kafka;

import com.resumematch.service.AsyncAnalysisWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class AnalysisEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(AnalysisEventConsumer.class);
    private final AsyncAnalysisWorker asyncWorker;

    public AnalysisEventConsumer(AsyncAnalysisWorker asyncWorker) {
        this.asyncWorker = asyncWorker;
    }

    @KafkaListener(topics = AnalysisEventPublisher.TOPIC_RESUME_ANALYSIS, groupId = "${spring.kafka.consumer.group-id:resumematch-workers}")
    public void consumeAnalysisJob(AnalysisJobEvent event) {
        log.info("Received Kafka analysis job event: analysisId={}, resumeId={}", event.getAnalysisId(), event.getResumeId());
        asyncWorker.processJob(event.getAnalysisId(), event.getResumeId(), event.getJobDescriptionId());
    }
}
