# ADR-004: Asynchronous Kafka Event Pipeline with Fallback

## Status
Accepted

## Context
Resume analysis requires document parsing, multi-pass keyword extraction, vector cosine calculation, and LLM completions. Synchronous execution on HTTP request threads can lead to gateway timeouts and poor scalability under traffic spikes.

## Decision
We implemented an event-driven architecture using **Apache Kafka**:
1. `POST /api/v1/analyses` persists the initial entity with status `PENDING`.
2. `AnalysisEventPublisher` publishes an `AnalysisJobEvent` to topic `resume-analysis-jobs`.
3. `AnalysisEventConsumer` picks up the job and coordinates `AsyncAnalysisWorker`.
4. Analysis transitions through explicit stages:
   - `PENDING` → `PARSING_RESUME` → `EXTRACTING_REQUIREMENTS` → `SCORING` → `AI_ENRICHMENT` → `COMPLETED`.
5. Frontend polls `GET /api/v1/analyses/{id}` to display live checklist progress.

### In-Process Fallback
When Kafka is disabled (`app.kafka.enabled=false`), the event publisher automatically executes via Spring `@Async` thread pools, allowing single-command developer setup without spinning up Kafka.

## Consequences
- **Positive**: High throughput, decoupled workers, durable queuing, resilient horizontal scaling.
- **Negative**: Adds Kafka message broker infrastructure to full deployment topologies.
