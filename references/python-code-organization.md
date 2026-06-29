# Python Code Organization Reference

Use this reference for Python code. Python projects may be agents, RAG systems, training pipelines, inference jobs, data processing jobs, CLIs, workers, experiments, or web services. Do not assume an HTTP backend structure unless the repository clearly uses one.

## Core Rules

- Inspect the existing Python project structure before adding files.
- Do not place many unrelated `.py` files in the same directory.
- Group code by business domain, capability, lifecycle stage, runtime role, or domain responsibility.
- Prefer small packages with clear ownership over large flat directories.
- Do not create Java/Spring-style layers such as `controller`, `service`, `manage`, or `mapper` unless the project already uses that pattern.
- Do not create packages for their own sake; keep simple scripts simple.
- If a one-off script grows shared logic, move reusable logic into packages and keep the script as a thin entry point.

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

## Flat Directory Anti-Patterns

Avoid flat structures that mix unrelated capabilities and business domains:

```text
src/
  agent.py
  train.py
  dataset.py
  model.py
  eval.py
  prompts.py
  tools.py
  vector_store.py
  openai_client.py
  preprocess.py
  inference.py
```

Prefer packages with clear ownership:

```text
src/
  agents/
    research_agent.py
  tools/
    search_tools.py
  prompts/
    research_prompts.py
  retrievers/
    hybrid_retriever.py
  training/
    trainer.py
  datasets/
    qa_dataset.py
  models/
    reranker.py
  evaluation/
    rag_eval.py
  inference/
    generator.py
  clients/
    llm_client.py
    vector_store_client.py
  pipelines/
    document_ingestion.py
    preprocessing.py
```

## Splitting Rules

- Split when one directory mixes agents, tools, training, datasets, clients, evaluation, and scripts.
- Split when one module mixes orchestration, external IO, model logic, data loading, and evaluation.
- Keep related small functions together when splitting would make navigation worse.
- Keep tiny one-off scripts simple; do not create a package hierarchy for a single script.
- Keep entry points thin: `train.py`, `infer.py`, `evaluate.py`, or CLI commands should call package code instead of containing all logic.
- Do not create one-file packages unless the project already uses that pattern or the area is expected to grow immediately.
- Do not move code only for aesthetics; every moved module should clarify ownership or reduce coupling.

## Naming Rules

- Use `snake_case.py` for module names.
- Use clear suffixes when they communicate role, such as `_agent.py`, `_tool.py`, `_client.py`, `_dataset.py`, `_trainer.py`, `_handler.py`, or `_provider.py`.
- Avoid vague names like `common.py`, `helper.py`, `misc.py`, or `utils.py` for business, agent, training, or pipeline logic.
- Prefer business-domain names when the package represents a business area.

## Import Rules

- Prefer absolute imports from the project package when that is the existing style.
- Keep package `__init__.py` minimal. Do not hide large side effects there.
- Do not fix import problems by mutating `sys.path` unless the project already uses that pattern and there is no better local option.
- Avoid circular imports; move shared contracts, constants, and small shared types into a lower-level shared module only when needed.

## Tests

- Mirror the source package structure in tests when practical.
- Put shared test fixtures in the existing fixture or `conftest.py` pattern.
- Do not place tests for unrelated modules in one large test file.
