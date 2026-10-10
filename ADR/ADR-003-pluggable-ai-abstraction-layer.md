# ADR-003: Pluggable AI Abstraction Layer

## Status
Accepted

## Context
Tying an enterprise application directly to a single LLM vendor (e.g. OpenAI SDK) creates vendor lock-in, exposes the system to API outages, and prevents running offline or cost-free development environments.

## Decision
We established a strict `AIProvider` abstraction:
```text
AIProvider (interface)
  ├── MockAIProvider (deterministic, zero external API cost, instant tests)
  ├── OpenAIProvider (HTTP chat completions with JSON schema validation)
  └── GeminiProvider (Google Gemini API)
```
Managed by `AIProviderFactory`, controlled via `app.ai.provider` configuration.

### Anti-Fraud Safeguard
The AI layer explicitly implements anti-fraud prompts:
- Never advise candidates to add skills they do not possess.
- Never fabricate numbers or metrics; provide bracketed placeholders `[X%]` prompting genuine candidate metrics.

## Consequences
- **Positive**: App runs out of the box without requiring API keys; seamless switching between OpenAI, Gemini, and local mock engines; resilient automated testing.
- **Negative**: Prompt formatting must target portable JSON outputs across diverse LLMs.
