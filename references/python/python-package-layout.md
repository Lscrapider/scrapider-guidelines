# Python Package Layout Reference

Use this reference for decisions about Python business and technical package structure, module
boundaries, and file splitting. Read the [Python Code Organization](python-code-organization.md)
baseline first; reuse it if already read and unchanged for this task.

## Contents

- [Business Module Boundaries](#business-module-boundaries)
- [Common Python Package Roles](#common-python-package-roles)
- [Splitting Rules](#splitting-rules)

## Business Module Boundaries

- Separate different business domains into different packages or subpackages.
- Do not place code for unrelated business domains in the same module just because the technical role is similar.
- When both business domain and technical role matter, choose one clear structure and keep it consistent across the project.
- For domain-heavy projects, group by business domain first, then by capability inside each domain.
- For platform-heavy projects, group by capability first, then split by business domain inside each capability.

Domain-first example:

```text
src/
  finance/
    agents/
      analyst_agent.py
    tools/
      market_tools.py
    pipelines/
      report_ingestion.py
  medical/
    agents/
      triage_agent.py
    tools/
      clinical_tools.py
```

Capability-first example:

```text
src/
  agents/
    finance/
      analyst_agent.py
    medical/
      triage_agent.py
  tools/
    finance/
      market_tools.py
    medical/
      clinical_tools.py
```

## Common Python Package Roles

Use only the roles that fit the current project.

### Agent / Workflow

- `agents`: Agent definitions, agent composition, agent runtime setup.
- `workflows` or `flows`: Multi-step orchestration, graph nodes, pipeline stages, state transitions.
- `tools`: Tool functions exposed to agents, tool schemas, tool adapters.
- `prompts`: Prompt templates, prompt builders, prompt versioning.
- `memory`: Conversation memory, checkpoints, long-term memory adapters.
- `retrievers`: Retrieval logic, vector search, hybrid search, reranking, document recall.
- `embeddings`: Embedding clients, embedding wrappers, vectorization logic.

### Training / ML

- `datasets`: Dataset loading, dataset wrappers, splits, sampling, preprocessing entry points.
- `data`: Raw data adapters or domain data access when the project already uses this name.
- `preprocessing`: Cleaning, normalization, tokenization, feature construction.
- `training` or `train`: Training loops, trainer classes, optimization setup, checkpoint saving.
- `models`: Model definitions, architecture modules, model wrappers.
- `losses`: Loss functions and objective components.
- `metrics`: Metrics used during training or evaluation.
- `evaluation` or `evals`: Evaluation runners, benchmark logic, scoring, reports.
- `inference`: Prediction, generation, serving adapters, inference-time pipelines.
- `experiments`: Experiment configs, experiment entry points, ablation wiring.
- `configs`: YAML, JSON, or Python config schemas and defaults.

### Infrastructure / Integration

- `clients`: External API, SDK, model, database, storage, or service clients.
- `adapters`: Translation between external payloads and internal objects.
- `schemas`: Pydantic models, validation schemas, structured output contracts, tool input/output contracts.
- `repositories`: Persistence access only when the project clearly uses repository-style storage.
- `tasks` or `workers`: Background jobs, queue consumers, scheduled tasks, async workers.
- `pipelines`: ETL, ingestion, chunking, parsing, indexing, or data processing pipelines.
- `config`: Runtime settings, environment parsing, feature flags.
- `utils`: Small generic helpers only. Do not dump business, agent, training, or pipeline logic here.

## Splitting Rules

- Split when one directory mixes agents, tools, training, datasets, clients, evaluation, and scripts.
- Split when one module mixes orchestration, external IO, model logic, data loading, and evaluation.
- Keep related small functions together when splitting would make navigation worse.
- Keep tiny one-off scripts simple; do not create a package hierarchy for a single script.
- Where a package already owns the implementation, keep `train.py`, `infer.py`, `evaluate.py`, or CLI entry points thin. A small self-contained script may keep its logic in one file; do not create a package merely to thin its entry point.
- Create a one-file package only for a current package responsibility or an established repository convention, never because the area may grow later.
- Do not move code only for aesthetics; every moved module should clarify ownership or reduce coupling.
