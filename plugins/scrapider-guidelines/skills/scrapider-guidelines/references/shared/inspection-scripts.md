# Inspection Scripts

Mechanically decidable Scrapider rules are implemented as JetBrains `.inspection.kts` checks:
one script per rule plus `BatchAllRules.inspection.kts` in `scripts/inspections/`. Load this
reference as the assigned standards reviewer when the JetBrains inspection channel is available,
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

- Run applicable rules within the assigned review scope. Single-file findings anchor on the offending
  element; use the structured `lineNumber` and `elementText` for its location and code.
- Use `BatchAllRules.inspection.kts` for multi-file reviews; use a per-rule script to isolate one
  rule on one file. In batch results, use `file:line` from each message and ignore structured
  location fields.
- Single-file messages contain `[rule]`, plus `missing @x` for absence-type rules or a verdict
  token for file-level Python rules. Batch messages contain `[rule] file:line <code excerpt or
  missing-x>`.
- Keep a batch to roughly 15 files. The script aggregates occurrences after 5 of the same rule
  in one file and truncates after 60 findings with an overflow breakdown; when truncated, split
  the files into smaller batches and continue until all applicable checks have results. If one
  file still truncates when run alone, use per-rule checks for it and manually verify any remaining
  omitted results.
- These scripts run against the currently open IDE project. `get_file_problems` runs built-in
  inspections only and does not include these scripts.

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
| J17ModuleDependencyDirection | Enforce module import directions and placement bans | spring-boot-multi-module.md | ERROR | main-source `.java` in a multi-module repository |
| J18MybatisPlusConfigOnly | MyBatis-Plus plugin imports are limited to `.config` packages or `*Config` classes | spring-boot-architecture.md, config responsibility | ERROR | main-source `.java` |
| J19NoSilentCatch | Empty catches and configured non-empty silent catches are flagged | this reference | WARNING | main-source `.java` |
| PY1SnakeCaseModuleName | Python module filenames use ASCII `snake_case` | python-code-organization.md, Naming Rules | WARNING | any `.py` |
| PY2NoSysPathMutation | Text matching `sys.path.append/insert/extend/remove` is flagged; verify the matched line is executable code | python-code-organization.md, Import Rules | ERROR | any `.py` |
| PY3NoVagueModuleName | Vague module filenames such as `common`, `util`, `utils`, `helper`, `helpers`, and `misc` | python-code-organization.md, Naming Rules | WEAK_WARNING | any `.py` |

Result boundaries: J1 checks fields and constructor parameters only. J13 checks `PO`-suffixed
types in the declared return signature, not runtime objects hidden inside `Map` values.

## Adapting to a Repository

- Update the J1/J2/J17 exclusion sets for non-database name collisions (`ObjectMapper` is preset;
  J1 and J17 also match `*Manage`).
- `J11`: add utility classes to `EXPLICIT_UTILITY_CLASSES`.
- `J16`: update the package-to-suffix map.
- `J17`: update the `moduleOf` mappings and banned-import prefixes together to match the target
  topology; unmatched packages remain unchecked.
