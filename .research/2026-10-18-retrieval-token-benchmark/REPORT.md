# Code Retrieval Token Benchmark (2026-10-18)

Developer research archive for human browsing on GitHub. Omit `.research/` when installing the skill; the skill does not load this archive. This is an exploratory, single-sample comparison of six code-retrieval workflows across three repository-question types. Task descriptions below are generalized; this archive contains no private source excerpts, answers, raw prompts, traces, user names, absolute paths, deployment identifiers, sensitive configuration, or credentials. Tool and operating-system versions are retained as experiment parameters.

## Findings

Eighteen formal Desktop runs completed: one run for each of six profiles in each of three cases. Two earlier CLI attempts failed before returning usage and are listed separately in the data. Every formal run used a fresh independent context, one completed turn, serial execution, and the confirmed model and effort gpt-6-luna / max.

The original strict quality gate passed all six case 1 answers and none of the twelve case 2 or case 3 answers. A separate review of the original task scope found all six case 1 answers sufficient; case 2 native, Codegraph default, Codegraph + Serena, and Codebase Memory sufficient, with focused Codegraph and Serena partially sufficient; and all six case 3 answers partially sufficient. The case 3 answers got the main queue and recovery logic right, with completeness gaps. No core factual error was identified in the reviewed answers. The review does not overwrite the original scoring.

Three candidate workflows are retained for project-level choice: native retrieval; Codegraph plus native retrieval; or Codegraph plus Serena free LSP plus native retrieval. Select one workflow per project. This sample does not establish a universal winner or intrinsic backend cost ranking. CBM is not a default recommendation.

## Study design

### Cases

| Case | Generalized task |
| --- | --- |
| case1_symbol | Find an identified Java helper's exact tenant and user checks, then verify its login and refresh callers. |
| case2_cross_language | Follow a request from Java orchestration through a Python worker and asynchronous completion event back to Java handling. |
| case3_conceptual | Explain what happens when a local queue is full, including persisted state, in-memory context, retries, and restart recovery. |

### Profiles

| Profile | Retrieval available to the worker |
| --- | --- |
| native | Host-native file and text search |
| codegraph_default | Codegraph with its default query strategy |
| codegraph_focused | Codegraph with a focused file-selection strategy |
| serena_lsp | Serena classic LSP |
| codegraph_serena | Codegraph and Serena LSP |
| codebase_memory | Codebase Memory (CBM) |

Each case used all six profiles once. Runs were serial and used independent contexts; the case order was rotated. Workers could not inspect other runs, answer keys, or the experiment plan, and could not delegate, run the application, install dependencies, or rebuild indexes. The integrity review found 18 single-turn completions, exact model/effort matches, no measured overlaps, no unexpected nested tools, and a match between logged usage and reported per-run usage.

### Environment and tool versions

| Component | Recorded version or configuration |
| --- | --- |
| Model and effort | gpt-6-luna / max, confirmed in all 18 formal runs |
| Formal execution host | Codex Desktop 0.163.0-alpha.10.2 |
| Failed initial CLI entry | Codex CLI 0.144.4; two attempts, neither yielded usage |
| Codegraph | @colbymchenry/codegraph 1.3.0; codegraph_explore tool |
| Serena | serena-agent 2.0.0, classic LSP; Java JDTLS via vscode-java 1.54.0 and Pyright 1.1.403 |
| Codebase Memory | codebase-memory-mcp 0.12.1 |
| Runtime environment | macOS 26.6.2 arm64; harness Python 3.9.6; isolated Serena Python 3.13.15 |

The frozen source manifest covered 2,199 files (13,502,743 bytes). The run-integrity audit found no changes to those manifest files; generated language-server artifacts were inventoried separately. The indexes and language-server state were reused, and operating-system and model caches were not cleared.

## Measurement and accounting

Per-run usage comes from the host's reported usage records. Across the 18 formal logs, 397 unique response IDs were counted with no cross-run duplicates. Repeated cumulative snapshots were not added again.

Uncached input = input tokens − cached input tokens. Total tokens = input tokens + output tokens. Cached input is a subset of input, and reasoning output is a subset of output; neither is added a second time. Missing usage remains blank in metrics.csv and is not treated as zero. Token totals are not prices or monetary costs; no price schedule or billing record was available.

| Formal-case account | Runs | Input | Cached input | Uncached input | Output | Total tokens |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| Formal cases | 18 | 35,423,428 | 34,293,120 | 1,130,308 | 216,575 | 35,640,003 |

Coordination, preparation, and analysis usage is kept separate from the formal cases. At the recorded cutoff, the non-case snapshot was partial: 9 coordination/analysis sessions and 441 unique response IDs summed to 56,554,899 input tokens (55,090,432 cached; 1,464,467 uncached), 462,496 output tokens, and 57,017,395 total tokens. The snapshot included 27 selected sessions overall, of which the 18 formal-case sessions are accounted for above. This partial snapshot is not the complete experiment cost and does not include the formal-case total in its coordination sum.

## Results

The original strict assessment and the later scope review are shown separately. “Gate” reports the original per-run quality-pass value; “review” reports the independent answer-sufficiency judgment. The detailed CSV also retains the original correctness and evidence-coverage fields.

### Aggregate observations

| Profile | Total tokens across three cases | Uncached input | Model responses |
| --- | ---: | ---: | ---: |
| native | 2,651,974 | 135,738 | 41 |
| codegraph_default | 4,591,595 | 166,834 | 55 |
| codegraph_focused | 3,184,125 | 164,819 | 43 |
| serena_lsp | 14,647,187 | 222,801 | 148 |
| codegraph_serena | 3,054,067 | 168,705 | 41 |
| codebase_memory | 7,511,055 | 271,411 | 69 |

Native retrieval had the lowest aggregate total and uncached input in this sample. The combined profile's aggregate total was about 15.2% higher; its retention is a capability choice, not a claim that it won on Token cost. These totals include partially sufficient answers and the presentation differences below, so they are observations rather than a quality-controlled ranking.

### case1_symbol

| Profile | Total tokens | Uncached input | Original gate | Review |
| --- | ---: | ---: | --- | --- |
| native | 394,041 | 11,979 | pass | sufficient |
| codegraph_default | 356,249 | 24,268 | pass | sufficient |
| codegraph_focused | 347,302 | 25,719 | pass | sufficient |
| serena_lsp | 433,826 | 21,917 | pass | sufficient |
| codegraph_serena | 238,678 | 21,529 | pass | sufficient |
| codebase_memory | 888,763 | 51,686 | pass | sufficient |

All six answers correctly covered the helper's exact gate conditions and both callers. In the codegraph_serena profile, the worker made one Codegraph call and no Serena call. Its lower observed total therefore cannot be attributed to two-tool collaboration; native had the lowest uncached-input count.

### case2_cross_language

| Profile | Total tokens | Uncached input | Original gate | Review |
| --- | ---: | ---: | --- | --- |
| native | 1,380,633 | 85,358 | not passed | sufficient |
| codegraph_default | 3,505,331 | 99,083 | not passed | sufficient |
| codegraph_focused | 2,212,969 | 100,934 | not passed | partial |
| serena_lsp | 12,662,730 | 147,526 | not passed | partial |
| codegraph_serena | 1,707,166 | 88,181 | not passed | sufficient |
| codebase_memory | 5,670,667 | 179,473 | not passed | sufficient |

The separate review applied the original task scope: four answers were sufficient and two partially sufficient. This review changes no historic gate values. There is no common original-gate pass from which to select an efficiency winner. The combined profile used Codegraph only for this case; Serena was not called.

### case3_conceptual

| Profile | Total tokens | Uncached input | Original gate | Review |
| --- | ---: | ---: | --- | --- |
| native | 877,300 | 38,401 | not passed | partial |
| codegraph_default | 730,015 | 43,483 | not passed | partial |
| codegraph_focused | 623,854 | 38,166 | not passed | partial |
| serena_lsp | 1,550,631 | 53,358 | not passed | partial |
| codegraph_serena | 1,108,223 | 58,995 | not passed | partial |
| codebase_memory | 951,625 | 40,252 | not passed | partial |

All six answers explained the main queue-full and recovery behavior, but left parts of the proof or lifecycle conditions underdeveloped. The reviewed judgment is partial for all six, not a finding that the main state logic was wrong. Cost differences here are diagnostic only.

The full 20-row metrics.csv includes every completed run plus both failed CLI attempts. The two failures are outer initialization failure (0.237 seconds) and model rejection (5.018 seconds). Both have unavailable usage, with token fields left blank, and are excluded from profile comparisons.

## Interpretation and limits

The measured unit is the complete host, bridge, prompt, and retrieval workflow. It is not an isolated backend benchmark. Of 292 MCP returns, 247 included structuredContent, and 245 presented text and structuredContent forms were semantically equivalent. The bridge displayed both forms when present. This likely added repeated input in some runs, but no one-form control was run, so no token savings can be estimated or subtracted after the fact.

The Desktop global tool catalog could not be hidden by profile. Schema exposure was therefore not isolated, and the token cost of a tool schema cannot be separated from system instructions, skill text, task text, bridge output, or other context. The experiment does not measure the savings from native MCP rendering, schema trimming, on-demand loading, or a REPL.

There was one sample per profile/case. Queries, retrieved scope, answer length, model turns, cache hits, and index or language-server state varied. The case order rotated, but there are no repeated samples, confidence intervals, or significance tests. File-count limits such as Codegraph maxFiles are not token budgets, and the profile differences do not isolate a single configuration parameter. Total tokens and uncached input can rank the same runs differently; neither represents billable cost by itself.

The original case 2 strict gate and the subsequent review answer different questions. Retrieved source, factual correctness, and citation or explanation sufficiency are distinct. A full body found in a trace does not prove the final answer used it; an omitted citation is not automatically a false statement. Keep both judgments visible when using this archive.

The CBM readiness record noted partial and unusable parse results, so absent graph results must not be treated as proof that code is absent. Serena requires its language-server setup and state. These setup costs were not folded into per-run token totals. Other models, repeated runs, larger tasks, coding tasks, controlled schema presentation, and billing-based cost were not tested.

## Selection and cleanup record

For clarity, native, graph, and enhancement are project-guidance usage options, not three additional measured profiles. The benchmark matrix remains the six profiles listed above.

The evidence supports retaining three mutually exclusive project-level choices for review:

1. Native retrieval for projects that prefer a dependency-light workflow.
2. Codegraph plus native retrieval, with complete returned source reused and native search available for configuration, literals, and uncovered evidence.
3. Codegraph plus Serena free LSP plus native retrieval, with Serena used only for a specific unresolved symbol, reference, or implementation gap.

Choose one workflow per project. Do not require every project or every task to invoke the whole combination. The default and focused Codegraph profiles use the same backend; this sample does not establish that either strategy should be installed as a separate product. Codebase Memory remains an experiment candidate, not the default stack.

An earlier partial cleanup removed the unselected CBM executable, runtime databases and lock files, plus inactive TypeScript/Vue Serena language servers. Its historical audit reported 495,373,423 logical bytes removed while the snapshot and experiment evidence were still retained. That record describes the earlier stage, not the current retention state.

Final retention state: the temporary experiment environment, private project copy, experiment indexes, harnesses, raw-trace copies, and preview files have since been removed. The old experiment directory was checked and is absent. The final public deliverables are this report and metrics.csv; they retain the measured results, assessment conclusions, versions, parameters, and provenance hashes. Persistent Serena 2.0.0 LSP and its Java/Python dependencies, the preexisting Codegraph, Codex and uv installations, other existing MCP configuration, and the real project's original indexes are protected. No permanent tool depends on a retained copy of the deleted experiment. See the shared [cleanup verification record](../CLEANUP.md) for the final checks. Cleanup is an operational choice, not a ranking of backend quality or cost.

## Authoring behavior checks after the update

Three small generation probes used a Luna/max subagent and primary-agent review. They exercised the updated Skill, not the retrieval backends, and are outside the measured Token matrix. No application or new unit test was run.

| Probe | Input choice and scope | Observed result |
| --- | --- | --- |
| Initialize | Small Python project; lightweight retrieval; direct work | One native-retrieval entry and brief module responsibilities. The first output added conditional delegation despite the direct-work choice. The authoring rule was clarified and the repeated probe retained direct work. |
| Rebuild capabilities | Existing structured file; relationship-aware profile; change Tools only | One Codegraph + native entry with triggers, reuse and fallback; Collaboration and Delivery preserved. |
| Rebuild capabilities | Java/Python modules; precise-symbol enhancement; change Tools only | One grouped Codegraph + Serena LSP + native entry; Serena limited to unresolved symbol questions; unrelated sections preserved. |

These probes support the targeted authoring change; they are not a general success-rate estimate or evidence of Token savings from the updated guidance.

## Further opportunities (not benchmarked)

The following upstream projects were checked on 2026-10-18. They optimize different layers and do not form a fourth mandatory retrieval stack. Their advertised savings are not results of this study.

| Layer and candidate | Suitable opportunity | Minimal next check and adoption boundary |
| --- | --- | --- |
| Shell output: [RTK](https://github.com/rtk-ai/rtk) | Repetitive Git, build, test, or log output. The OSS edition supplies command filters under Apache-2.0; the full edition also includes differently licensed components. | Compare a successful command and a failing command with and without filtering; preserve exit status and diagnostic evidence. Then measure actual host usage. RTK's own byte-based estimates are not host Token measurements. Use this CLI layer only where output is noisy. |
| Tool definitions: [MCP Slim](https://github.com/dopatools/mcp-slim) | A host eagerly exposes a large tool catalog; the MIT-licensed proxy offers tool search, schema retrieval, and downstream calls. | First establish whether the target host already defers schemas. A useful control must count discovery calls, cache effects, and correct execution. Treat this as a candidate until that host comparison is available; it does not establish savings from deduplicating returned content. |
| Large result processing: [Context Mode](https://github.com/mksglu/context-mode) | Frequent large logs or payloads can be processed locally and retrieved selectively through MCP tools. | Compare with native local processing on one bounded task, including setup and query overhead. Verify routing in the actual host; a subprocess is not full OS isolation. Its Elastic License 2.0 terms differ from permissive open-source licenses. Keep it a research candidate, not a default dependency. |

Prefer existing host capabilities when they already cover a layer. No new packages, hooks, or global MCP entries were installed for this follow-up. Additional backend runs were deferred: a meaningful comparison should first control the duplicate rendering and schema differences already observed, rather than repeat the same confounded matrix.

## Provenance

- Original source report SHA-256: fe97ff5295b5e16e9ee7839c877258a99015928acf4abe3c4bbe7c5c83dcc9f2
- Original source metrics SHA-256: 1c5f6dc698f52d19cc33f259dc0fcb43df307ddca0cda7c32d3592ad7268f1c5
- Frozen source manifest SHA-256: 3e1c6d71ce1fe277a91fb185dcc6e9249c7db60b5677aadb0805b0caae623304
- Formal usage basis: 397 unique response IDs across 18 completed runs, cross-checked against per-run usage.

The hashes identify the reviewed inputs without exposing their local paths. Experiment-owned copies of raw requests, answers, traces, and source snapshots were removed during final cleanup and are not included in this archive. The archive supports checking aggregate arithmetic and the recorded assessment, but no longer supports reopening those original packets or independently reproducing the private-source investigation. Host-managed conversation history is outside this experiment cleanup and is not promised as a reproduction store. A future backend-cost study should first control the rendered representation and schema exposure, then repeat matched-quality cases and compare host-reported usage; this archive does not claim those effects have been measured.
