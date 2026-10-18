# XERJ incremental retrieval validation

Date: 2026-10-18. Status: measured pilot; 18 formal runs completed.

## Current assessment

At the tested version and configuration, this pilot did not demonstrate a repeatable Token-saving benefit from adding XERJ. Keep it outside the Skill recommendations for now and retain this report as a dated research record. Compared with the ordinary existing workflow, the XERJ arm used **32.3% more total tokens**, **32.5% more output tokens**, and **36.8% more elapsed time per task**. Its uncached input was only **1.7% lower**. Both arms passed all six of their answers under the frozen rubric.

Keep XERJ as a candidate for reevaluation after relevant release updates, integration changes or new retrieval needs. Continue the existing task-dependent native / Codegraph workflow, with Serena reserved for an actual symbol or reference gap. This assessment applies to the observed setup and tasks; later versions, corpora or integrations may produce different results.

These are results for a specific local CLI integration and lexical index. Seven failed loopback connections and their retries are included. The percentages must not be presented as an intrinsic cost penalty of the XERJ backend or as an MCP-to-MCP ranking.

## Design

The comparison used the same read-only snapshot of an anonymized Java/Python repository for every arm: 2,126 text files, 12,155,961 bytes. A private file manifest was used to verify the snapshot during execution. The public archive omits the project identity, internal business and symbol names, source paths, private commit identifiers, local paths and private-artifact fingerprints. Aggregate setup information is recorded in [SETUP.json](SETUP.json).

| Arm | Retrieval workflow |
| --- | --- |
| A — existing | Native search and targeted reads, Codegraph as appropriate, optional Serena for an unresolved exact-symbol need. |
| B — reference first | The same tools, with an explicit instruction to locate existing implementation and project guidance before answering. |
| C — reference first + XERJ | The same reference-first instruction, starting with a real XERJ query; native reads, Codegraph and optional Serena can fill gaps. |

Three cases were each run twice in every arm, giving **18 fresh-context Desktop collaboration runs**, all **gpt-6-luna / max**, serial, with no model switches or reused worker contexts. Arm order was balanced in advance. Workers saw their task and routing instructions, never pre-retrieved answer passages, private rubrics or previous answers. All scheduled results are retained in the table; no weak answer was replaced.

The cases were:

1. **Remote document processing:** distinguish validation scripts from the registered Worker path; trace routing, submission, remote status, parsing and persistence.
2. **Synchronous engine extension:** reconcile the extension guide with current routing keys, operation selection, Executor/Lifecycle, Factory/Registry and blocking-I/O/result boundaries.
3. **Known-file MinIO URI parsing:** explain a small, explicitly named parser. This is a negative control for extra discovery work.

The negative control deliberately required C to issue a query even though the file was known. It measures the overhead of invoking XERJ in that situation. An optional deployment could skip that invocation; these results do not mean that merely installing XERJ slows every local task.

The [publication-safe rendering of the frozen protocol](PROTOCOL.md) describes routing, measurement, scoring and order. Case identifiers and descriptions have been generalized for publication; measurements, scoring and execution order are unchanged. These are source-analysis tasks, not code-generation or runtime regression tests.

## Results

### Mean per completed run

Each row averages six runs: two repetitions of each of the three cases. Total tokens include cached input. Output already includes reasoning output.

| Arm | Total tokens | Uncached input | Output | Model responses | Seconds | Mean score | Passed |
| --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| A — existing | 633,026.0 | 50,785.5 | 7,776.5 | 10.00 | 67.61 | 92.67 | 6/6 |
| B — reference first | 757,578.3 | 50,470.0 | 8,036.3 | 11.33 | 68.95 | 89.67 | 5/6 |
| C — plus XERJ | 837,324.7 | 49,930.3 | 10,306.3 | 14.00 | 92.46 | 93.83 | 6/6 |

Relative to A, C changed total tokens by **+32.273%**, uncached input by **−1.684%**, output by **+32.532%**, and elapsed time by **+36.755%**. Relative to B, C changed total tokens by **+10.526%**, uncached input by **−1.069%**, output by **+28.247%**, and elapsed time by **+34.096%**.

The slightly lower uncached input does not establish an overall saving: output and repeated cached context increased. No monetary cost is inferred from these counts.

### Per-case mean total tokens

| Case | A | B | C | C versus A |
| --- | ---: | ---: | ---: | ---: |
| Remote document processing | 859,499.0 | 1,442,914.5 | 1,248,356.0 | +45.24% |
| Synchronous engine extension | 807,128.5 | 622,017.5 | 923,007.5 | +14.36% |
| Known-file URI parser | 232,450.5 | 207,803.0 | 340,610.5 | +46.53% |

The paired C-versus-A changes by repetition were:

| Case | Repetition 1 | Repetition 2 |
| --- | ---: | ---: |
| Remote document processing | +14.2% | +81.2% |
| Synchronous engine extension | +44.0% | −12.7% |
| Known-file URI parser | +13.4% | +88.0% |

Only one of the six same-case/repetition comparisons reduced total tokens versus A. The remote-document comparison with B changed direction between repetitions: a reduction in the first and an increase in the second. There is no repeatable overall win in this sample.

### Answer quality and primary review

Three independent Luna/max judges each evaluated six shuffled answers for one case, without arm labels, token usage or tool traces. The primary agent checked the deductions against answers and source evidence and retained the blind scores. The pass rule was frozen before formal runs: at least 85/100 with no listed critical factual error. Equivalent wording and unique file-level citations were accepted.

| Case | A scores | B scores | C scores |
| --- | --- | --- | --- |
| Remote document processing | 93, 93 | 80, 88 | 92, 90 |
| Synchronous engine extension | 85, 85 | 85, 85 | 85, 96 |
| Known-file URI parser | 100, 100 | 100, 100 | 100, 100 |

No answer made a rubric-listed critical false claim. The single failing B answer omitted the intended remote API probe relationship and several interface/component details; it still identified the main production route, remote status handling and persistence boundary correctly. A failed completeness gate is not evidence that its entire explanation was wrong.

Remote-document deductions primarily concerned the probe distinction, Factory/executor details, exact submission/download parameters and explicit success-summary fields. The synchronous case's score difference came entirely from the explicit checklist of new capability components: every answer correctly explained the current routing split, synchronous execution, registration and result contract. This checklist difference should not be interpreted as a broad capability improvement. All six small-parser answers were complete.

Criterion-level points are preserved in [scores.json](scores.json); measured usage and final verdicts are joined in [metrics.csv](metrics.csv). These small differences and six passes per arm cannot demonstrate general capability equivalence or non-inferiority across coding tasks.

## What the retrieval traces show

- All six C workers actually queried the clean XERJ index. There were **16 CLI invocations**: **7 loopback-endpoint failures** and **9 calls returning passages**. Every failed connection was followed by a successful authorized local call. All scheduled task runs completed; these retries remain in the measured usage.
- Wide natural-language queries sometimes ranked UI prototype material, unrelated Java controllers or shared constants ahead of the target Python implementation. Narrowing the query to the target engine's exact symbol name returned its implementation; narrowing to the URI implementation returned the requested parser. XERJ can find relevant code, but additional query refinement and source reads can offset the benefit.
- The lower-token second synchronous C run still had an unrelated UI prototype as its first result, with the relevant extension guide later in the result list. A whole-run difference alone cannot identify how much improvement came from a useful passage, ordinary model variation or the subsequent Codegraph/native work.
- Workers used Codegraph in 6/6 A runs, 5/6 B runs and 4/6 C runs. **No formal worker used Serena.** Serena was available and its Python LSP helper passed a preparation smoke check; this study provides no estimate of Serena's incremental benefit.
- The known-file control supports going directly to a small named source file. An additional discovery query is unnecessary when the location and required evidence are already known.

The connection failures are an integration confound: ordinary sandboxed CLI calls could not reach the loopback node, while escalated local calls could. The frozen prompt allowed that retry. A production MCP connection or a direct permitted CLI invocation could remove this overhead. We did not subtract hypothetical saved model turns, because the downstream context and behavior would also change; that requires a new measured comparison.

## Environment and measurement integrity

XERJ was pinned to **1.0.0-rc.96**, macOS ARM64, with the release asset SHA-256 verified. It ran as an isolated loopback node in lexical mode, with feedback disabled and no external embedding/rerank API. The formal workflow used actual `xerj search` CLI text output against `snapshot-*`, initially at five passages and 1,000 characters per passage. Raw `--json` was avoided because the release returns untruncated records in that mode.

The actual MCP server was also checked during preparation: it exposed 16 tools. Its `max_tokens` uses the engine's StandardTokenizer/UAX #29 word count, not the model tokenizer. Neither that limit nor `xerj gain` was used to estimate measured model usage. Formal comparisons used CLI retrieval; global MCP schemas were not isolated by arm.

The clean XERJ build took **160.173 seconds** and ended with exit code **3**, `completed-with-junk`: 2,055 unique files indexed, 12,931 records, 30 file-level junk entries and 87 record-level junk entries. All 1,589 recognized code files were indexed. Coverage checks found 262 indexed Python paths, one byte-identical canonical duplicate Python path, and all 38 Markdown paths indexed. SQL/XML/JSON and other ingestion limitations, including field-limit warnings and 12 no-text-searchable structured datasets, are recorded in setup evidence; they were not silently removed from the result.

During preparation, private case files were briefly placed under the snapshot by the case-design agent. Before any formal worker ran, they were moved outside it, the entire XERJ node data and indexing journals were discarded, and both XERJ and Codegraph were rebuilt. The clean catalog had no private case/truth/probe records. All 2,126 original source hashes and frozen prompts/rubrics/configurations remained unchanged through the formal runs.

A Java+Python Serena probe also started unnecessary Java initialization. It was stopped before measurements; the frozen helper used Python only. Its experiment-specific Java workspace was identified and removed while preserving the existing Serena runtime. Neither preparation incident is a formal case result.

Actual usage came from the host's `token_usage_record` entries, deduplicated by `response_id` and reconciled with the completed-turn cumulative snapshot. **All 18 measurements passed validation.** There was no character-based estimate, substituted model, missing usage, hidden replacement run or duplicated text/structured-content rendering in the formal XERJ CLI output. A fresh context does not imply a cold model cache; order was balanced and cached/uncached input is reported separately.

Formal case-run totals were:

| Metric | Actual total |
| --- | ---: |
| Input | 13,210,859 |
| Cached input, included in input | 12,303,744 |
| Uncached input | 907,115 |
| Output, including reasoning | 156,715 |
| Reasoning output, included in output | 98,751 |
| Total | 13,367,574 |
| Model responses | 212 |
| Host tool calls | 194 |
| Sum of measured worker time | 1,374.118 seconds |

These totals cover the 18 workers only. Preparation, grading and the live coordinator have substantial separate usage; their actual records and the coordinator's explicit partial cutoff are included in `overhead` within [SETUP.json](SETUP.json). They must not be mistaken for a complete end-to-end cost reduction or folded into one arm's retrieval result.

## Limits and follow-up threshold

This is a small pilot: one project, three read-only tasks, two repeats per cell, one model/effort and one pinned XERJ version. It did not test unfamiliar external-library implementation, multi-repository reference libraries, trained semantic retrieval, neural reranking, code modification, build success, isolated MCP schema exposure or a persistent production MCP integration. The private source snapshot is not published, so the public archive is a developer-facing record rather than an independently runnable public benchmark.

A relevant XERJ release update, a stable production connection or a new cross-repository/documentation use case would justify reevaluation. A follow-up should use a stable permitted connection, define a purposeful reference corpus that excludes generated/demo noise where appropriate, and retain a matched existing-tools baseline and quality gate. These are directions for a future test; this report records the current observations without predicting later results.

For the present Skill, retain task-based routing: known path or exact text → native lookup; relationships or impact → scoped Codegraph; unresolved exact-symbol operations → Serena when applicable. Avoid requiring a reference-first ritual or a query to every installed tool for every task. No Skill recommendation was changed by this pilot.

## Archive and cleanup

This directory is a developer research archive and is not linked from the Skill's runtime loading route. It contains the anonymized report, protocol, row-level metrics, criterion-level scores and aggregate setup/verification metadata. Source-bearing worker answers, complete model logs, private truth files, private-artifact fingerprints and temporary helper scripts are excluded from publication.

Cleanup status and preserved resource categories are recorded in [SETUP.json](SETUP.json). Existing Codegraph, Serena, Codex and uv installations, the original private project and its indexes are protected. The host's original conversation logs remain managed by the application; removing temporary copies does not delete that history.

Cleanup is complete: the temporary XERJ node was stopped, no owned runtime process remained, and the experiment tree was removed, including its snapshot, indexes, downloaded binary, private answer/log copies and helper scripts. The separate Java preflight workspace was also removed. Existing tool launchers and the real project were checked after stopping the experiment.

## Primary sources consulted

The candidate and its reference-coding positioning came from the [XERJ repository README](https://github.com/xerj-org/xerj/blob/main/README.zh-CN.md) and the [published reference-coding case study](https://github.com/xerj-org/xerj/blob/main/docs/case-studies/reference-coding/CASE_STUDY.md). The tested binary came from the [1.0.0-rc.96 release](https://github.com/xerj-org/xerj/releases/tag/v1.0.0-rc.96). Published project results were not reused as local measurements; this comparison required the worker to issue its own real retrieval requests.
