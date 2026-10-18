# XERJ incremental retrieval validation

This is a publication-safe rendering of the protocol frozen before execution. Project and case identifiers have been generalized; the design, execution order and scoring are unchanged.

## Question and scope

Does adding XERJ to an existing native / Codegraph / optional Serena workflow reduce actual model usage without losing answer quality? A workflow-only arm separates an instruction effect from the extra retrieval engine. These are read-only project analysis tasks; they do not establish generated-code quality or broad coding capability.

## Frozen design

- Corpus: a text-only snapshot of an anonymized Java/Python repository. A private source manifest records every copied file and SHA-256 for internal integrity checks; source paths and private fingerprints are withheld from publication. All profiles see the same snapshot. No private task, rubric, answer, experiment log or synthetic probe belongs to the indexed corpus.
- Model: gpt-6-luna, reasoning effort max, fresh Desktop collaboration context per run, one completed turn, no child delegation or model changes.
- Cases: Remote document processing path; documented versus current synchronous engine extension; a known-file object-storage URI parser as a negative control.
- Arms: A = ordinary existing workflow; B = existing tools with explicit reference-first instructions; C = B plus actual local XERJ passage retrieval.
- Two repetitions of every case / arm cell, 18 runs, executed serially. Order: first repetition C1 A/B/C, C2 B/C/A, C3 C/A/B; second repetition C1 C/B/A, C2 A/C/B, C3 B/A/C. Every arm occupies each order position twice.
- All workers receive only their task and routing instructions, never retrieved answer passages or scoring criteria. C must issue a task-relevant XERJ query; native reads and structural tools remain available for verification and missing context. A/B cannot use XERJ. A known exact file can be read directly in A/B.
- The routing instruction overrides only conflicting retrieval-order preferences in the copied AGENTS.md. Other applicable project constraints remain. Source and project settings are read-only.

## Tools and controls

Use real local CLI calls rather than registering additional global MCP servers. Native bounded searches/reads and Codegraph CLI are common; a read-only Serena MCP helper renders equivalent text / structured output once. XERJ CLI text output is bounded by result count and passage characters; raw JSON is not used. This tests retrieval workflow benefit, not MCP schema overhead or transport equivalence.

Codegraph 1.3.0 indexes only the snapshot. XERJ 1.0.0-rc.96 runs on a loopback-only isolated node, in lexical mode with no external embedding/rerank API. XERJ searches only snapshot-* indices. The optional Serena helper uses the existing runtime with isolated settings and Python LSP for the Python cases. It starts and stops a server per call; this possible latency overhead is common to all arms and actual use is reported.

Indexing and preparation are excluded from case-run usage and reported separately. The XERJ index is fixed before run 1. Cache state is not forcibly reset between runs; balanced order and cached/uncached reporting address, but cannot eliminate, caching/order effects. The globally exposed Desktop catalog is common and is not isolated by arm.

## Measurement

Read actual token_usage_record usage from each child session, deduplicate by response_id, and reconcile with the completed turn snapshot. Record input, cached input, input minus cached input, output, reasoning output (already included in output), total, response count, host tool-call count and elapsed time. Missing usage remains missing; no character-based token estimate or XERJ gain counter is substituted. Coordinator, setup and grading usage are separately labeled, with an explicit cutoff for a live coordinator snapshot.

Retain every scheduled result, including quality failures, tool errors and protocol deviations. Do not rerun a weak answer or change a rubric after seeing a treatment result. An infrastructure failure can only be described separately, not silently replaced. Inspect actual calls to verify treatment use and corpus isolation.

## Quality evaluation

Each task has a private, pre-frozen 100-point rubric. Pass = at least 85 points and no critical factual error. Equivalent wording and unique file references are accepted; exact line format is not required. Only requested facts are graded. A blind judge receives shuffled answers, task and rubric, without arm labels, usage or traces. The primary agent checks findings against sources and resolves specific scoring errors with written reasons.

Cost savings are interpreted alongside quality, including continuous score, critical errors and pass counts. Descriptive paired comparisons use the same case/repetition. With two repeats, no significance or universal winner claim is justified. The negative control tests the overhead of invoking XERJ when the exact file is already known.

## Preparation incidents and integrity

During preparation, private case files were briefly placed under the copy by the case-design agent. Before any formal worker ran, those files were moved outside the corpus, all XERJ node/index/journal data were discarded and rebuilt, and Codegraph was rebuilt. Formal use requires zero private-case/truth/probe index records and an unchanged source manifest. The discarded attempt is preparation only.

A Java+Python Serena smoke probe started unnecessary Java dependency resolution; it was stopped, and the frozen case configuration enables only Python. Original source file hashes remained unchanged. The experiment-specific language-server workspace and temporary dependencies are cleanup targets, while existing runtime installations are protected.

## Reporting and cleanup

Save an English developer-facing report, row-level measurements, protocol and reproducibility metadata under .research/2026-10-18-xerj-incremental-benefit/. Do not add this archive to installed Skill loading paths. Keep source-bearing answers, full model logs and private rubrics out of the repository. Summarize evidence before removing owned temporary environments, indexes and scripts. Preserve the original private project, its indexes, existing Serena/Codegraph/Codex/uv and unrelated repository changes. The validation step did not include global installation, a Skill recommendation change or Git publication. Publication of this anonymized archive was authorized separately afterward.
