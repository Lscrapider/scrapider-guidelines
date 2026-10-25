# Spring Boot Backend Reference

## Scope and Repository Precedence

Use this as the mandatory baseline for any Spring Boot backend implementation, refactoring,
debugging, or review. It defines repository precedence, core layering, shared configuration and
contract rules, and Java conventions. Load scenario references below according to the actual
behavior and call paths affected by the task.

For a Maven multi-module backend, also load [Spring Boot Multi-Module](spring-boot-multi-module.md)
before choosing a target module or file location.

Apply rules in this order:

1. The user's explicit instruction.
2. Repository instructions such as `AGENTS.md` or `CLAUDE.md`.
3. Architecture documents and established business contracts.
4. Existing POMs, package layout, configuration, code, and tests.
5. This reference.
6. Examples in this reference.

Before changing Java backend code, inspect the project's root package and existing package layout.
Do not copy or hard-code a root package from this reference. If the repository has an unambiguous
convention that conflicts with this reference, follow the repository for that point. Do not create
missing layers merely to make the repository match [this package map](spring-boot-architecture.md#package-map).

## Contents

- [Core Layered Architecture](#core-layered-architecture)
- [Configuration, Contracts, and Credentials](#configuration-contracts-and-credentials)
- [Java and Spring Coding Conventions](#java-and-spring-coding-conventions)
- [Scenario Reference Routing](#scenario-reference-routing)

## Core Layered Architecture

Use the repository's established synchronous flow. When the project has a `manage` layer, use:

```text
controller -> service (implemented by service.impl) -> manage -> mapper -> domain.po
```

When the project does not have a `manage` layer, keep its existing `service -> repository` or
`service -> mapper` boundary instead of introducing `manage` solely to match this reference.
External API clients support service orchestration and do not replace the persistence flow.

- Keep `controller` as the request entry layer. Do not put complex business logic or direct
  database access in controllers.
- Put business logic, workflow orchestration, and transaction boundaries in `service` and
  `service.impl`.
- Make controllers call `service`; never call `mapper` directly from a controller.
- In a project with an established `manage` layer, make business services use `manage` rather than
  injecting or calling `mapper` directly.
- Do not create layers for their own sake. Keep simple CRUD, single-branch logic, and short
  workflows directly in `service`.

## Configuration, Contracts, and Credentials

Before introducing a value or policy, search existing constants, configuration properties, enums,
POMs, environment templates, documentation, and code. Treat established defaults, thresholds,
enums, and strategy parameters as business contracts.

Do not invent or change any of these without an existing contract or explicit user confirmation:

- Default or maximum page sizes and overflow behavior.
- Logical deletion, automatic field filling, or audit-field semantics.
- Exchange, queue, routing key, TTL, dead-letter, retry, acknowledgement, concurrency, or prefetch
  policies.
- Object-storage bucket categories, retention, cleanup, or access policies.
- Token lifetime, refresh, concurrent-login, password, or authorization policies.
- Database schemas, indexes, status enums, or state-transition rules.

Keep credentials in controlled environment variables or ignored local configuration. Do not put
real credentials in committed configuration, Java constants, documentation, logs, or scripts. Never
expose object-storage access keys or secret keys to a frontend; generate authorized presigned URLs
at the backend boundary when temporary direct access is required.

## Java and Spring Coding Conventions

### Dependency Injection, Configuration, and Lombok

- Use constructor injection for required Spring dependencies, and declare injected fields `final`.
- Do not add Lombok solely for dependency injection. Avoid `@Autowired` field injection.
- Bind related configuration values through typed `@ConfigurationProperties` classes, following
  the repository's existing configuration pattern.
- Reserve `@Value` for isolated values when a dedicated properties class would add no clarity.
- Use Lombok only when it is already a project dependency and follows the established style;
  prefer focused annotations such as `@Getter`, `@Setter`, and
  `@RequiredArgsConstructor`.
- Avoid broad `@Data` when generated setters, `equals`, `hashCode`, or `toString` are not all part
  of the intended contract.

### Logging

- Use the project's existing logging framework.
- If Lombok is already available, `@Slf4j` is acceptable.
- Use parameterized log messages instead of string concatenation.
- Do not use `System.out`, `System.err`, or `printStackTrace`.
- Pass exception objects to the logger to preserve full stack traces.

### Utilities and Value Handling

- Inspect existing dependencies and project utility packages before choosing a utility.
- Use JDK `java.util.Objects` for null checks and object equality when a check is warranted at all; values already guaranteed non-null by the type system, deserialization, or an upstream validated boundary are used directly.
- For string operations, use Hutool `StrUtil` when Hutool is already available. Otherwise, use an
  existing project-approved utility.
- Follow the same dependency-first approach for common collection, number, and date operations.
  Hutool examples include `CollUtil`, `NumberUtil`, and `DateUtil`.
- If no suitable utility exists, ask the user before adding a dependency.
- Do not reimplement functionality that an available utility already provides.
- Invoke static utility methods through their declaring class, such as `StrUtil.isBlank(...)`,
  instead of static-importing them.
- For new APIs where absence means “no elements,” return an empty collection whose mutability
  matches the method contract. Preserve `null` when it is an established distinct state; change an
  existing nullable contract only after proving caller and serialization equivalence.
- For nullable boxed booleans, use `Boolean.TRUE.equals(value)` or
  `Boolean.FALSE.equals(value)`.
- Compare enum constants with `==`. Do not compare enums by ordinal or ad hoc string values.
- Use `java.time` for new date and time code. Reuse the project's existing date-time utilities and
  serialization conventions.
- Introduce `Date` or `Calendar` only when required by a legacy API, and convert at that boundary.

### File and API Documentation

- Before creating a Java file, inspect equivalent files and repository instructions for a required
  file-level JavaDoc template, author, date, language, and description format.
- Follow an established template exactly. Do not infer an author name or impose a template from an
  example when the repository has no such rule.
- For service interfaces, follow the repository's method-level JavaDoc convention, including its
  parameter and return tags. Do not add duplicate interface comments to implementations unless the
  implementation has behavior that needs additional explanation.
- Do not rewrite file headers in existing Java files solely to make them match a preferred format.

### Style and Naming

- Explicitly qualify instance method calls on the current object with `this.`, such as
  `this.validateOrder(order)` instead of `validateOrder(order)`. Call static methods through their
  declaring class.
- Follow the repository's established style for loops, streams, lambdas, and callbacks. Do not
  rewrite equivalent code only to impose a preferred syntax.
- Remove imports, local variables, private code, configuration, and dependencies made unused by
  the current change. Before removing broader or pre-existing code, check Spring wiring,
  reflection, serialization, generated configuration, and actual consumers. Follow
  [Code Design and Changes](../shared/code-design-and-changes.md) to migrate affected callers and
  remove superseded paths without compatibility wrappers; report unrelated dead code instead of
  deleting it opportunistically.
- Keep code simple, clean, and direct.
- Do not add method wrappers that merely forward a call.
- Keep class names aligned with their package roles, such as `UserController`, `UserService`,
  `UserServiceImpl`, `UserManage`, `UserMapper`, `UserParam`, `UserDTO`, `UserVO`, and `UserPO`.
- Do not invent alternate suffixes for the same role.
- Do not use wildcard imports.

## Scenario Reference Routing

Load every scenario reference whose trigger matches the behavior or call paths affected:

| Reference | Load when the task or affected code path involves |
| --- | --- |
| [Spring Boot Architecture](spring-boot-architecture.md) | Adding or moving a Java file, choosing its package or responsibility, or changing an existing role or responsibility. This includes changes to `handler` or `provider` behavior/responsibility and third-party `api` client behavior/responsibility. |
| [Spring Boot HTTP and Domain](spring-boot-http-and-domain.md) | HTTP/controller boundaries; data contracts for internal layer transfer or third-party responses, including choosing their representation; `Param`/`DTO`/`PO`/`VO`; and any bean/object conversion, including object-to-Map and Map-to-object conversion. This includes service return contracts across those boundaries. |
| [Spring Boot Data Access](spring-boot-data-access.md) | Adding or changing database access, including query/ORM calls, persistence behavior, or conditions that affect persistence even when changed indirectly outside `manage` or `mapper`. |
| [Spring Boot Messaging](spring-boot-messaging.md) | Adding or changing message production, consumption, payloads, routing, or listener/publisher behavior, including indirect changes to those paths. |
