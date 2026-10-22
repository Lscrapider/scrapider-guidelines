# Serena Incremental-Benefit Follow-up (2026-10-18)

Developer research archive. Omit `.research/` when installing the Skill; the Skill does not load this report. This follows the [initial six-profile experiment](../2026-10-18-retrieval-token-benchmark/REPORT.md) using the same frozen private Java/Python project. Private source, prompts, answers, and traces are excluded from this archive; their experiment-owned temporary copies have been removed after the final results were saved.

## Result and recommendation

All 12 formal answers passed the frozen rubric (8/8 each). None of the six workers allowed to use Serena invoked it. Those workers used native retrieval only. The other arm made three Codegraph calls across two symbol-case runs; its remaining work used native retrieval. Therefore the study provides **no estimate of the benefit of actually invoking Serena**. Lower usage in an arm where Serena was available cannot be credited to Serena.

The bounded reference question and known-method control were answerable without Serena. This supports a lighter default for these tasks, not a conclusion that Serena is generally ineffective. Keep precise-symbol enhancement conditional on a concrete unresolved gap and verified language-server support. Do not recommend it just because a repository is large or assume an additional MCP saves Tokens.

## Prespecified design

- Two arms: `graph_native` (Codegraph + native retrieval) and `graph_serena` (the same options plus optional Serena LSP for unresolved symbol/reference evidence).
- Two cases, three repetitions per case/arm, 12 fresh-context Desktop collaboration workers. Every worker was confirmed `gpt-6-luna` / `max`, `fork_turns=none`, one completed turn. Runs were serial, with no model fallback or nested delegation.
- Case `symbol`: identify one interface operation's implementation and complete direct-call set inside a specified production module, including receiver binding. A same-name graph association was found during source preparation; the original task was retained, and workers were not told about it.
- Case `local`: inspect a known method and explain a specific category-to-ID filter path, including its empty-result fallback before pagination. Other filters and downstream behavior were outside scope.
- Source questions, rubric, profiles, scripts, and schedule were hashed before the first formal worker. The rubric did not change after execution began.
- Order by repeat: symbol N→S and local S→N; local N→S and symbol S→N; symbol N→S and local S→N. N denotes `graph_native`, S `graph_serena`. There are three N-first and three S-first pairs overall.
- This authorized experiment overrode ordinary project retrieval preferences. Known-target native reads were allowed; neither arm had to call every available tool. This tests optional tool availability under conditional routing, not forced tool use.

Codegraph remained version 1.3.0, Serena 2.0.0 classic LSP, Java via vscode-java/JDTLS 1.54.0, with the same previously prepared indexes and runtime. Each cell started a new bridge/server process. Both backends had an unmeasured readiness warm-up. LSP/OS/model caches were not cleared. Worker elapsed time excludes bridge startup; this is a warm-state comparison. Startup is separately recorded in the CSV: across six cells per arm, its median was 0.260 seconds for N and 0.864 seconds for S. Persistent-server deployments would amortize this differently.

## Duplicate-return repair

The model-facing helper used in this follow-up removed a redundant structured representation only when a single text block parsed to the same JSON value, or exactly equalled `structuredContent.result`. Equality preserved JSON types, so booleans and numbers were not conflated. Residual structured fields, text-block metadata, error flags, non-text blocks, and continuation information remained visible. Original packets and per-render audit records were retained for assessment, then their experiment-owned copies and the helper were removed during final cleanup. Tool schemas were requested individually on demand.

Offline replay checked all 292 original response records against their raw JSONL packets: 91 whole-JSON duplicates and 154 duplicate `result` fields were deduplicated; two non-equivalent structured responses and 45 text-only responses were retained. Four in-memory boundary checks covered equality, non-equality, additional cursor fields, and errors/non-text/metadata. No host Token savings were inferred from character counts.

The formal follow-up made only three MCP calls, all to Codegraph, with no duplicate structured payloads. Consequently this matrix does not quantify the repair's model-usage savings. The global Desktop tool catalog remained exposed in both arms; this is not an isolated schema-installation experiment.

## Fixed scoring

Each case had eight necessary fact points; passing required at least six points, the core requested outcome, and no critical error. Facts needed supporting source locations; one range could support multiple facts. Equivalent wording and uniquely locatable scoped filenames were assessed by meaning rather than exact strings. Unrequested surrounding detail did not earn or lose task-completion points.

- Symbol points covered interface identity, implementation class and method, exact caller-set size, caller class/method, receiver expression, and declared receiver type. Core outcomes were the implementation and complete scoped caller set with receiver binding. Confusing a same-named method or a declaration with a call was a critical error.
- Local points covered the non-null trigger/type mapping, source IDs, matching link rows, collected target IDs, the ID filter, empty-set sentinel, its intended no-result behavior, and application before the page query. Core outcomes were the ID transformation and empty-set behavior before pagination.
- A fresh Luna/max reviewer received anonymous shuffled answers, the task, and frozen evidence, but no profiles, usage, or traces. One tool-selection-only sentence was removed from a blind copy; source facts were unchanged. The primary agent checked the scores and accepted equivalent phrasing. Every answer scored 8/8 with no critical errors.

## Observed measurements

The [12-row CSV](metrics.csv) preserves each formal run. The following values are medians with observed ranges where shown; n=3 in every row. Total Tokens include cached input and output. They are not monetary cost.

| Case | Arm | Pass | Total Tokens: median [min–max] | Uncached input: median [min–max] | Output median | Worker seconds: median [min–max] |
| --- | --- | --- | ---: | ---: | ---: | ---: |
| symbol | graph_native | 3/3 | 318,476 [208,160–414,205] | 16,611 [9,299–25,179] | 2,722 | 33.077 [23.536–34.599] |
| symbol | graph_serena | 3/3 | 209,932 [174,334–259,049] | 11,856 [8,422–12,765] | 1,966 | 23.588 [18.696–26.831] |
| local | graph_native | 3/3 | 306,130 [164,390–389,413] | 12,202 [7,985–13,702] | 2,555 | 28.805 [17.471–29.323] |
| local | graph_serena | 3/3 | 220,428 [167,963–258,722] | 11,556 [8,968–11,859] | 1,977 | 20.806 [16.729–24.242] |

| Case | Arm | Model responses: median [min–max] | Codegraph calls, all three runs | Serena calls, all three runs |
| --- | --- | ---: | ---: | ---: |
| symbol | graph_native | 7 [5–9] | 3 | 0 |
| symbol | graph_serena | 5 [4–6] | 0 | 0 |
| local | graph_native | 7 [4–9] | 0 | 0 |
| local | graph_serena | 5 [4–6] | 0 | 0 |

### Matched-repeat differences

Values below are S minus N, paired by case and repetition. Negative values mean lower observed use or time in S; they do not establish a Serena effect because S never invoked Serena.

| Case | Repeat | Total Token difference | Uncached-input difference | Output difference | Seconds difference |
| --- | ---: | ---: | ---: | ---: | ---: |
| symbol | 1 | -108,544 | -8,189 | -2,051 | -11.011 |
| symbol | 2 | -155,156 | -12,414 | -150 | -6.246 |
| symbol | 3 | -33,826 | +2,557 | +481 | -4.840 |
| local | 1 | -168,985 | -343 | -578 | -7.999 |
| local | 2 | +94,332 | +3,571 | +1,033 | +6.771 |
| local | 3 | -138,167 | -4,734 | -1,721 | -12.594 |

## Accounting and integrity

The 12 independent worker sessions produced 71 unique response IDs. Unique-response usage sums matched the final per-turn cumulative usage; no repeated cumulative snapshots were added. Frozen protocol hashes and all 2,199 source-manifest files remained unchanged. There were no overlapping measured workers. All formal bridges stopped normally.

| Account | Input | Cached input | Uncached input | Output | Total |
| --- | ---: | ---: | ---: | ---: | ---: |
| 12 formal cases | 3,064,068 | 2,913,664 | 150,404 | 27,134 | 3,091,202 |
| Preparation, coordination and assessment — partial cutoff | 18,488,183 | 17,830,912 | 657,271 | 104,420 | 18,592,603 |

The separate overhead snapshot ends at 2026-10-18T07:51:30.629075+00:00. It includes the two preparation agents, the blind reviewer, and completed parent-session responses since the follow-up request. The parent account is explicitly partial; later reporting and delivery are outside this cutoff. Formal worker usage is excluded from the overhead row. This accounting does not claim a final bill or that running the experiment itself saved cost.

## Post-run backend check (outside the matrix)

After all 12 formal workers finished, a separate direct Serena `find_referencing_symbols` call returned the single correct caller and omitted the unrelated same-name call. No additional measured model worker was launched. This confirms that the scoped reference operation was usable; it does not supply a model-level accuracy, latency, or Token comparison. Returned LSP locations used zero-based positions and were checked against source line numbers. The helper rendered the duplicate result only once.

## Interpretation and limits

1. **No actual Serena treatment was observed.** All six enabled-arm runs used native tools. The difference between arm labels is not evidence that Serena improves performance. The symbol case proved answerable by scoped native search, so it did not create the intended unresolved semantic gap.
2. **Both arms reached the quality ceiling.** There is no observed completeness improvement in these two tasks. This does not establish what happens for harder inheritance, overload, or cross-module binding questions.
3. **Retrieval choices and repeat variability matter.** Two N symbol runs used Codegraph and then native verification; the other ten runs used native retrieval only. Even the all-native local case varied substantially. Extra helper discovery and model rounds contribute to the observed workflow totals.
4. **n=3 is descriptive evidence.** The task order was counterbalanced, but model randomness and caches were not controlled. No statistical significance, universal ranking, or billing-based conclusion is claimed.
5. **Presentation was repaired, not isolated as a treatment.** The original 18-run matrix is not a matched before/after control: tasks, routing, and repetition design changed. Its numbers must not be used to calculate the repair's savings.
6. **The public archive is an audit summary, not a standalone reproducer.** The source project is private. Experiment-owned temporary copies of the original tasks, source citations, frozen ground truth, full requests, rendered/raw responses, model logs, and detailed scoring were removed after archiving the final measurements and assessment conclusions. Provenance hashes identify the historical inputs but do not restore them. Host-managed conversation history is outside this cleanup and is not promised as a reproduction store.

## Skill decision

Keep one selected retrieval profile per project. Prefer native retrieval when it covers the actual question, and Codegraph plus native retrieval for recurring relationship work. Retain the third profile as a conditional capability option only after an actual symbol/reference gap is identified and Serena is verified to resolve it. Preserve confirmed user choices, but do not require unused tools on every task or present the third profile as a validated Token-saving upgrade.

The guidance also now calls for checking ambiguous same-name edges against receiver and call-site source, and distinguishes available tools from tools actually used when interpreting benchmarks. No research paths or numerical benchmark claims were added to the Skill loading routes. During the formal experiment, no new global MCP installation, live-project edit, or index rebuild was performed. Serena 2.0.0 LSP was subsequently retained in a persistent installation with its Java/Python dependencies and local Codex integration. Temporary experiment runtimes, project bindings, harnesses, raw-trace copies, and previews have been removed. The old temporary environment is absent; the retained installation is not a launcher into that environment. See the shared [cleanup verification record](../CLEANUP.md). This result does not justify uninstalling Serena as generally ineffective.

## Provenance

- Frozen source manifest SHA-256: `3e1c6d71ce1fe277a91fb185dcc6e9249c7db60b5677aadb0805b0caae623304`
- Frozen protocol manifest SHA-256: `fae6848b722337bf8666de70f6aec6300558f059d6ae8ac3de1f137c4303f094`
- Pre-run scoring rubric SHA-256: `24bffba94799278edff33f1206a8aab29f0f1cca034e9092a3dd0055a3278a1f`
- Render-helper SHA-256 used in every cell: `30c4b838d72ed954e3b0e9739909542a329a5c733599eddc489435c5fc202908`
- Exported metrics SHA-256: `060088f9effa19aab53febce698c21ac0b389f1f39bfd51ac5a3bbedbf88508f`
