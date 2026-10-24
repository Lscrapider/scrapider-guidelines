# Inspection Scripts

Mechanically decidable rules of the Scrapider references are scripted as JetBrains
`.inspection.kts` checks — one script per rule plus a batch driver (`BatchAllRules.inspection.kts`)
in `scripts/inspections/`. Load this reference
for a standards review when the JetBrains IDE channel is available for the project, or when
maintaining, adapting, or installing the library. Implementation work does not load it.

Semantic rules — minimal-change fit, responsibility boundaries, exception semantics, business
contracts, converter ownership — are not scripted; they stay with the standards review.
Four checks are convention-driven instead of universal: service-interface javadoc (J3),
file-header templates (J4), param-field comments (J5), and non-empty silent catches (J19
tier 2) apply only where the reviewed repository's own instructions establish them — the
running agent enables their switches per those instructions and leaves them off otherwise.

## Running the Scripts

- **Agent**: execute each applicable script against a changed file with the `run_inspection_kts`
  JetBrains MCP tool — `inspectionKtsCode` is the full script text, `contextPath` the file path
  relative to the project root, `projectPath` the open IDE project. Findings carry the rule id,
  file, and line; treat them as the result for those rules and review what is not scripted.
- **Convention switches**: the switches are set by the running agent, not by configuration
  files — scripts are passed as plain text, so the reviewer reads the target repository's own
  instructions first, edits the switch constants in the script text when the repository
  establishes the convention, and leaves them at their defaults otherwise.
- **Message minimalism** (rule meaning and remediation live in this reference, so finding
  messages carry none of it): per-rule single-file runs anchor findings on the offending
  element — the tool's structured `lineNumber`/`elementText` fields already carry the real
  line and code, so the message is just `[rule]` (plus a `missing @x` token for
  absence-type rules, or a verdict token for file-level Python rules). Batch runs anchor
  everything on the context file, where those structured fields are meaningless — batch
  messages are self-contained `[rule] file:line <code excerpt or missing-x>`.
- **Batch mode (preferred for reviews)**: `scripts/inspections/BatchAllRules.inspection.kts`
  runs the whole library over a list of files in ONE call. Replace its `FILES` list with the
  changed-file paths, optionally set `OPT_IN = false` to skip opt-in rules, and run it once —
  `contextPath` may be any project file. Findings return as messages in the form
  `[rule] File.java:line description`; the structured `lineNumber`/`elementText` fields of the
  result refer to the context file and are not meaningful in batch mode. Per-rule single-file
  calls remain useful to pinpoint one rule on one file.
- **Batch sizing**: keep one batch to roughly 15 files and chunk larger change sets into
  consecutive calls. The script self-limits output regardless: past 5 occurrences of one rule
  in one file it aggregates the rest into a count, and past 60 findings it truncates with an
  overflow breakdown by rule — on truncation, chunk smaller or set `OPT_IN = false`.
- **IDE editor / Qodana / Inspect Code**: copy the needed `.inspection.kts` files into the
  target project's `.idea/inspections/`. Whether the IDE picks them up varies by version;
  `run_inspection_kts` remains the reliable channel. `get_file_problems` runs built-in
  inspections only and does not include these scripts.
- Scripts apply only to the project currently open in the IDE. `targetFileContent` on
  `run_inspection_kts` tests synthetic content without touching disk.

## Rule Library

"Prefilter" is a cheap text check worth doing before spending a call in per-rule single-file
mode; it never replaces the script's own judgment. Batch mode runs every rule internally and
needs no prefilter. Run opt-in scripts like the others — their findings are candidates:
confirm each against the actual code before promoting it to a finding.

| Script | Checks | Rule source | Level | Runs on | Prefilter |
| --- | --- | --- | --- | --- | --- |
| J1ControllerNoMapperOrManage | Controllers must not inject `*Mapper`/`*Manage` (fields and constructor params) | spring-boot-backend.md, Core Layered Architecture | ERROR | classes with `@RestController`/`@Controller` | text contains `RestController` |
| J2ServiceNoDirectMapper | Services must not inject `*Mapper` directly; enable only when the repository has a `manage` layer | spring-boot-backend.md, Core Layered Architecture | WARNING | `*ServiceImpl` / `@Service` classes | text contains `Mapper` |
| J3ServiceInterfaceJavadoc | Service interface methods carry javadoc with `@param` for every parameter and `@return` for non-void | spring-boot-backend.md, File and API Documentation; convention-driven — enable per repository instructions | WARNING | `*Service` interfaces | — |
| J4FileHeaderJavadoc | Class header javadoc carries the repository template tags (`REQUIRED_TAGS` empty = off) | spring-boot-backend.md, File and API Documentation; convention-driven | WARNING | all main-source classes | — |
| J5ParamFieldComment | `*Param` fields carry a comment: `@Schema(description=...)` or a leading comment | this reference; convention-driven — enable per repository instructions | WARNING | `*Param` classes | — |
| J6NoAutowiredFieldInjection | No `@Autowired` field or setter injection; constructor injection with `final` | spring-boot-backend.md, Dependency Injection | WARNING | all main-source `.java` | text contains `@Autowired` |
| J7NoWildcardImport | No `.*` imports, including static | spring-boot-backend.md, Style and Naming | WARNING | any `.java` | import line ends `.*;` |
| J8NoDebugOutput | No `System.out`/`System.err` printing or `printStackTrace()` | spring-boot-backend.md, Logging | WARNING | any `.java` | text contains `System.out`/`System.err`/`printStackTrace` |
| J9ParameterizedLogging | Logger messages use `{}` placeholders, not string concatenation | spring-boot-backend.md, Logging | WEAK_WARNING, opt-in | main-source `.java` | text contains `log.` and `+` |
| J10EnumIdentityComparison | Compare enums with `==`, never `equals` or `ordinal()` | spring-boot-backend.md, Utilities and Value Handling | WARNING | main-source `.java` | text contains `.equals(` |
| J11NoUtilityStaticImport | Invoke utility statics through their class, no single-member static imports of utility classes | spring-boot-backend.md, Utilities and Value Handling | WARNING | any `.java` | text contains `import static` |
| J12ExplicitThisQualifier | Instance-method calls on the current object are qualified with `this.` | spring-boot-backend.md, Style and Naming | WEAK_WARNING, opt-in | main-source `.java` | — |
| J13ControllerNoPOReturn | Controllers must not return `PO`, including inside wrapper generics (`IPage<PO>`, `Response<PO>`) | spring-boot-http-and-domain.md | ERROR | controller classes | text contains `RestController` |
| J14NoLocalExceptionHandler | No local `@ExceptionHandler` in controllers; use `@RestControllerAdvice` | spring-boot-http-and-domain.md | ERROR | controller classes | text contains `ExceptionHandler` |
| J15NoMapRequestBody | No `Map`/`JsonNode` request bodies where a stable typed contract exists — report as candidate, confirm manually | spring-boot-http-and-domain.md | WEAK_WARNING, opt-in | controller classes | text contains `@RequestBody` |
| J16PackageSuffixAlignment | Class names carry the package-role suffix (controller→`Controller`, … impl→`Impl`) | spring-boot-backend.md + spring-boot-architecture.md, Package Map | WARNING | classes in the mapped packages | — |
| J17ModuleDependencyDirection | Module import directions and placement bans for multi-module repositories; ships inert with placeholder topology | spring-boot-multi-module.md | ERROR | all main-source `.java` in a multi-module repository; the agent fills `moduleOf` with the target topology first | — |
| J18MybatisPlusConfigOnly | MyBatis-Plus plugin references only from the unified config | spring-boot-architecture.md, config responsibility | ERROR | main-source `.java` | text contains `mybatisplus.extension.plugins` |
| J19NoSilentCatch | Empty catch blocks always flagged; non-empty catches that swallow silently behind `DETECT_SILENT_SWALLOW` | this reference; tier 2 convention-driven | WARNING | main-source `.java` | text contains `catch (` |
| PY1SnakeCaseModuleName | Module file names are snake_case | python-code-organization.md, Naming Rules | WARNING | any `.py` | — |
| PY2NoSysPathMutation | No `sys.path` mutation | python-code-organization.md, Import Rules | ERROR | any `.py` | text contains `sys.path` |
| PY3NoVagueModuleName | `common`/`utils`/`helper`/`misc` module names carrying business logic — report as candidate, confirm manually | python-code-organization.md, Naming Rules | WEAK_WARNING, opt-in | any `.py` | — |

Result caveats when reporting: name-suffix checks carry an exclusion list for non-database
`*Mapper` types (Jackson `ObjectMapper` is preset) — extend it when a new collision appears.
J1 checks declared fields only; J13 matches `PO`-suffixed types in the return signature, not
objects hidden inside `Map` values. Opt-in findings are candidates, not verdicts.

## Adapting to a Repository

Repository-specific constants sit at the top of each script:

- `J2`: `REQUIRE_MANAGE_LAYER` — set to `false` in repositories without a `manage` layer.
- `J1`/`J2`/`J17`: exclusion lists for `*Mapper`-named non-database types.
- `J11`: `EXPLICIT_UTILITY_CLASSES`.
- Convention-driven switches — the running agent sets these from the reviewed repository's
  own instructions before dispatch, and leaves them off when the repository has no such
  convention: `J3` `REQUIRE_SERVICE_JAVADOC`, `J4` `REQUIRED_TAGS` (empty = off),
  `J5` `REQUIRE_PARAM_FIELD_COMMENTS`, `J19` `DETECT_SILENT_SWALLOW` (the empty-catch
  tier of J19 is structural and always on).
- `J16`: the package-to-suffix map.
- `J17`: `moduleOf`, the package-prefix-to-module topology — it ships with placeholder
  prefixes (inert everywhere); the running agent replaces them with the target repository's
  real prefixes and module names before use.

## Writing New Scripts

- One `InspectionKts` entry per file; multiple entries execute only the first.
- Some PSI symbols do not resolve as explicit type references (`PsiDocComment`,
  `PsiDocParamRef`, `PsiImportStatementBase.qualifiedName`, `PsiCatchSection` members,
  `PsiReference.referenceName`); use implicit property chains, child traversal, or text-prefix
  fallbacks instead.
- A comment directly above a declaration binds into that declaration's own text range — detect
  leading comments by the declaration's text prefix (`//` or `/*`, not `/**`).
- Under `targetFileContent`, `virtualFile.path` is unreliable; derive location from
  `packageName`.
- Static imports are absent from `importList.importStatements`; use `allImportStatements` and
  detect wildcards by the `.*;` text suffix.
- Debug with `generate_psi_tree`, the inspection.kts API/examples generators, and temporary
  `registerProblem` probes.
