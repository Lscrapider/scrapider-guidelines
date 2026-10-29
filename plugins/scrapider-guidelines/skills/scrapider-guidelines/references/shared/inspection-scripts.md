# Inspection Scripts

Mechanically decidable Scrapider rules are implemented as JetBrains `.inspection.kts` checks:
one script per rule plus `BatchAllRules.inspection.kts` in `scripts/inspections/`. Load this
reference before scripted Java standards checks, including when IDE/MCP availability is unknown,
or when explicitly maintaining, adapting, or installing the library.

Semantic rules — minimal-change fit, responsibility boundaries, exception semantics, business
contracts, and converter ownership — remain part of the standards review.

## Applicability and Findings

- Apply J3 only when repository instructions require service-interface javadoc, J4 only with a
  required header template, J5 only when Param-field comments are an established convention, and
  J19's non-empty tier only when instructions require logging swallowed exceptions. These switches
  default off; J19's empty-catch tier always runs.
- J9, J12, J15, and PY3 are opt-in checks; the batch includes them by default. J15 applies only
  where a stable typed request contract can be used; PY3 applies only to business-logic modules
  and excludes infrastructure-only modules. Treat opt-in results as candidates and confirm them
  against the code before reporting violations.

## Running the Scripts

- Run applicable rules within the assigned review scope. Use `BatchAllRules` for multiple files and
  a per-rule script to isolate one rule on one file. Single-file results use `lineNumber` and
  `elementText`; batch results use `file:line` in the message, not structured anchor fields.
- First load this reference from the assigned skill root, then run the local
  [inspection runner](../../scripts/run_inspection.mjs) with Node.js 18 or newer. It loads source,
  batches about 15 files, handles MCP HTTP, and emits JSON only. Do not read or attach `.inspection.kts`
  source; attempt the runner even if no native inspection tool appears in the host catalog.

  ```sh
  node /path/to/skill/scripts/run_inspection.mjs /path/to/project
  node /path/to/skill/scripts/run_inspection.mjs /path/to/project '["src/main/java/A.java"]'
  node /path/to/skill/scripts/run_inspection.mjs /path/to/project '["src/main/java/A.java"]' '["J7","J13"]'
  ```

- Call: `node /path/to/skill/scripts/run_inspection.mjs <projectPath> [filesJson] [inspectionsJson]`.
  The project path must be open in the IDE. Omitted or `null` `filesJson` selects existing staged,
  unstaged, and untracked `.java` files with read-only Git commands; deleted files and ignored
  untracked files are excluded. A JSON array selects paths; `[]` is empty. Omitted `inspectionsJson` runs the batch;
  pass IDs such as `J7` or `J13` for a named subset. Set `SCRAPIDER_INSPECTION_SETTINGS` to a JSON
  object only when a repository convention requires a script setting.
- By default, the runner reads the connection with `codex mcp get jetbrains-mcp --json`. Other hosts
  can set `JETBRAINS_MCP_URL` and JSON `JETBRAINS_MCP_HEADERS`; `JETBRAINS_MCP_SERVER` selects an
  alias. No Python or third-party package is required.
- `status: "ok"` means execution completed; findings still require review. Missing PSI files, missing
  targets, and truncation produce `incomplete`; compilation or call failures produce `error`. Both exit nonzero.
  Report the attempted command, concrete error, and uncovered scope, then manually review it; never
  report the scripted inspection as passed. No Java changes or `[]` returns `skipped` with a reason and needs no IDE connection.
- Single-file messages contain `[rule]`, plus `missing @x` for absence checks or a verdict token for
  file-level Python checks. Batch messages contain `[rule] file:line <excerpt or missing-x>`; use this
  embedded location. The batch aggregates after 5 occurrences per rule per file and truncates at 60;
  narrow and rerun if truncated. `get_file_problems` reports built-in inspections only.

## Rule Library

| Script | Checks | Rule source | Level | Runs on |
| --- | --- | --- | --- | --- |
| J1ControllerNoMapperOrManage | Controllers must not inject `*Mapper`/`*Manage` fields or constructor parameters | spring-boot-backend.md, Core Layered Architecture | ERROR | `@RestController`/`@Controller` classes |
| J2ServiceNoDirectMapper | Services must not inject `*Mapper` fields directly; enable only where a `manage` layer exists | spring-boot-backend.md, Core Layered Architecture | WARNING | `@Service` classes, `*ServiceImpl` classes, and `*Service` interfaces |
| J3ServiceInterfaceJavadoc | Service interface methods require javadoc, `@param` for each parameter, and `@return` for non-void results | spring-boot-backend.md, File and API Documentation | WARNING | `*Service` interfaces |
| J4FileHeaderJavadoc | Class header javadoc carries configured repository template tags | spring-boot-backend.md, File and API Documentation | WARNING | top-level classes in main-source `.java` |
| J5ParamFieldComment | Non-static `*Param` fields carry `@Schema(description=...)`, javadoc, or a leading comment | this reference | WARNING | `*Param` classes |
| J6NoAutowiredFieldInjection | Flag fields and methods annotated with `@Autowired` | spring-boot-backend.md, Dependency Injection | WARNING | all main-source `.java` |
| J7NoWildcardImport | No wildcard imports, including static imports | spring-boot-backend.md, Style and Naming | WARNING | any `.java` |
| J8NoDebugOutput | Resolved `PrintStream.print/println/printf` and `Throwable.printStackTrace()` calls are flagged | spring-boot-backend.md, Logging | WARNING | any `.java` |
| J9ParameterizedLogging | Logger messages use `{}` placeholders instead of string concatenation | spring-boot-backend.md, Logging | WEAK_WARNING | main-source `.java` |
| J10EnumIdentityComparison | Compare enums with `==`, not `equals()` or `ordinal()` comparisons | spring-boot-backend.md, Utilities and Value Handling | WARNING | main-source `.java` |
| J11NoUtilityStaticImport | Invoke utility statics through their class; single-member imports from `*Util` or configured utility classes are flagged | spring-boot-backend.md, Utilities and Value Handling | WARNING | any `.java` |
| J12ExplicitThisQualifier | Qualify unqualified instance-method calls on the current object with `this.` | spring-boot-backend.md, Style and Naming | WEAK_WARNING | main-source `.java` |
| J13ControllerNoPOReturn | Controllers must not return `PO`-suffixed types, including in wrapper generics | spring-boot-http-and-domain.md | ERROR | controller classes |
| J14NoLocalExceptionHandler | No local `@ExceptionHandler` methods in controllers; `@ControllerAdvice` and `@RestControllerAdvice` classes are exempt | spring-boot-http-and-domain.md | ERROR | controller classes |
| J15NoMapRequestBody | Controller `@RequestBody` parameters typed as `Map` or `JsonNode` | spring-boot-http-and-domain.md | WEAK_WARNING | controller classes |
| J16PackageSuffixAlignment | Class names carry package-role suffixes; the `service` package applies the suffix to interfaces only | spring-boot-backend.md + spring-boot-architecture.md, Package Map | WARNING | classes in mapped packages |
| J17ModuleDependencyDirection | Enforce module import directions, placement bans, and tier order in the declared module dependency graph | spring-boot-multi-module.md | ERROR | main-source `.java` in a multi-module repository |
| J18MybatisPlusConfigOnly | MyBatis-Plus plugin imports are limited to `.config` packages or `*Config` classes | spring-boot-architecture.md, config responsibility | ERROR | main-source `.java` |
| J19NoSilentCatch | Empty catches and configured non-empty silent catches are flagged | this reference | WARNING | main-source `.java` |
| PY1SnakeCaseModuleName | Python module filenames use ASCII `snake_case` | python-code-organization.md, Naming Rules | WARNING | any `.py` |
| PY2NoSysPathMutation | Text matching `sys.path.append/insert/extend/remove` is flagged; verify the matched line is executable code | python-code-organization.md, Import Rules | ERROR | any `.py` |
| PY3NoVagueModuleName | Vague module filenames such as `common`, `util`, `utils`, `helper`, `helpers`, and `misc` | python-code-organization.md, Naming Rules | WEAK_WARNING | any `.py` |

Result boundaries: J1 checks fields and constructor parameters only. J13 checks `PO`-suffixed
types in the declared return signature, not runtime objects hidden inside `Map` values. J17 reports
one `[J17][SKIP]` notice per unresolved module and one dependency-graph finding per violating module
pair per run; its import bans additionally require the tier token in the file's own package so the
root package can be derived.

## Adapting to a Repository

- Update the J1/J2/J17 exclusion sets for non-database name collisions (`ObjectMapper` is preset;
  J1 and J17 also match `*Manage`).
- `J11`: add utility classes to `EXPLICIT_UTILITY_CLASSES`.
- `J16`: update the package-to-suffix map.
- `J17`: tiers resolve from `MODULE_MAP` package-prefix overrides, then module and package naming
  tokens (`foundation`/`shared`/`common`/`core`, `entity`/`model`, `db`/`persistence`/`mapper`,
  `business`/`biz`, `bootstrap`/`app`); `business`/`bootstrap` naming wins over later tokens while
  `foundation-*` compounds take the most specific segment. Add `MODULE_MAP` entries only for
  non-conventional naming; files whose tier stays unresolved emit a `[J17][SKIP]` notice instead of
  silently passing.
