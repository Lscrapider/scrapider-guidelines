# Spring Boot Backend Reference

## Scope and Repository Precedence

Use this reference as the package-responsibility baseline for a single-module Spring Boot backend.
A Maven multi-module backend normally distributes the same responsibilities across modules; when
multiple modules are present, also load `spring-boot-multi-module.md` before choosing a target file.

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
missing layers merely to make the repository match this package map.

## Contents

- [Package Map](#package-map)
- [Core Layered Architecture](#core-layered-architecture)
- [HTTP and Controller Boundary](#http-and-controller-boundary)
- [Domain Objects and Conversion](#domain-objects-and-conversion)
- [Persistence and External Integration](#persistence-and-external-integration)
- [Optional Service-Supporting Layers](#optional-service-supporting-layers)
- [Message Queue Boundaries](#message-queue-boundaries)
- [Configuration, Contracts, and Credentials](#configuration-contracts-and-credentials)
- [Java and Spring Coding Conventions](#java-and-spring-coding-conventions)

## Package Map

| Package or class | Responsibility |
| --- | --- |
| `api` | External API clients for third-party calls, remote data fetching, and integrations. |
| `config` | Spring configuration for components such as `RestTemplate`, MyBatis Plus, Redis, Swagger, and thread pools. |
| `controller` | HTTP entry points that receive and validate requests, call `service`, and return responses. |
| `converter` | Explicit type conversions that existing framework or project utilities cannot express correctly. |
| `domain` | Business data objects and their specialized subpackages. |
| `domain.constant` | Business constants, including status values, cache key prefixes, and default settings. |
| `domain.dto` | Data transfer objects for internal boundaries or third-party response mapping. |
| `domain.enums` | Business enums for status, type, risk level, and business category. |
| `domain.param` | Request parameter objects for query, create, and update input. |
| `domain.po` | Persistence objects that normally map one-to-one to database tables and are used by `mapper`. |
| `domain.vo` | View objects returned to the frontend. |
| `handler` | Optional strategy, type, event, or rule-branch handlers that support `service`. |
| `listener` | Inbound message queue consumers. |
| `manage` | Concrete MyBatis Plus helpers for simple database operations. |
| `mapper` | MyBatis or MyBatis Plus database access interfaces. |
| `provider` | Optional data providers that support `service` through a shared abstract contract. |
| `publisher` | Outbound message queue publishers. |
| `service` | Business service interfaces and contracts used by controllers, listeners, tasks, and other entry points. |
| `service.impl` | Service implementations that contain the main business logic. |
| `task` | Scheduled jobs and background tasks, such as external data synchronization or periodic maintenance. |
| `<Project>Application` | The Spring Boot application entry point, named for the current project. |

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

## HTTP and Controller Boundary

- Use typed `domain.param` Param objects for complex request bodies or query contracts when that
  matches the repository. Simple identifiers and filters may use path variables or request
  parameters without creating a one-field Param class.
- Do not use weakly typed `Map` or `JsonNode` request bodies when a stable typed contract exists.
- Use `VO` objects for frontend responses. Never return `PO` objects directly to the frontend.
- Reuse the repository's established pagination parameter names and response shape; do not rename
  an existing API contract only to prefer `pageSize` or `pageNum`.
- Choose HTTP methods from the existing API contract and HTTP semantics. Do not change a method
  only for stylistic consistency.
- Do not define local `@ExceptionHandler` methods in controllers. Use module-level or global
  `@RestControllerAdvice`.
- When logging an exception, pass the exception object to the logger so the full stack trace is
  preserved.

## Domain Objects and Conversion

### Object Boundaries

- Use `PO` objects for database persistence.
- Use `DTO` objects for internal layer transfer and third-party response mapping.
- Keep conversion logic free of business orchestration and persistence.

### Creating Persistence Objects

- Follow the repository's established conversion boundary. A static factory on a `PO`, such as
  `OrderPO.fromDto(...)`, is suitable for simple local construction that does not couple the
  persistence object to transport or third-party types.
- Keep external payload, `JsonNode`, and third-party mapping in an existing converter, adapter, or
  integration boundary rather than importing those contracts into the `PO`.
- Do not scatter the same construction logic across `task`, `service`, or `manage`.

### Bean and Object Conversion

- Before implementing bean copying, object-to-Map conversion, or Map-to-object conversion, inspect
  the project's existing dependencies.
- Prefer an existing framework or project utility. For bean copying, use Spring `BeanUtils`,
  Hutool `BeanUtil` when Hutool is available, or another approved utility.
- Do not add a dependency without approval. If no suitable utility exists, implement the narrowest
  explicit conversion that follows project style; ask only when the dependency choice or mapping
  contract cannot be decided from the repository.
- Use a converter class in `converter` only when generic copying cannot represent the required
  business mapping semantics.
- Do not scatter manual conversion logic across `service` or `service.impl`.

## Persistence and External Integration

### Manage

- Apply these rules only when the repository already has a `manage` layer or the user explicitly
  asks to introduce one.
- Keep `manage` focused on MyBatis Plus data access; keep business orchestration, state transitions,
  transaction ownership, parameter normalization, and VO conversion in `service` or `service.impl`.
- Let services use inherited operations such as `getById`, `getOne`, `list`, `page`, `save`,
  `saveBatch`, `updateById`, `removeById`, `lambdaQuery`, and `lambdaUpdate` directly through the
  corresponding manage class.
- Do not add a named manage method that merely forwards one straightforward inherited operation.
- Add a dedicated manage method only for reusable complex access, custom mapper SQL, stable named
  data semantics, locking, batch behavior, or complex shared conditions that should hide ORM or SQL
  details from services.
- Use a concrete `manage` class, normally extending `ServiceImpl`.
- Do not create a `manage.impl` package or a `Manage` interface plus `ManageImpl` pair unless the
  existing repository already follows that convention.

### Mapper

- Keep `mapper` focused on database access.
- Prefer MyBatis Plus functional queries. Write custom SQL only when those queries cannot express
  the operation or are clearly unsuitable.
- For MyBatis Plus query and update construction, prefer lambda wrappers such as
  `LambdaQueryWrapper` and `LambdaUpdateWrapper` over raw string column names.

### API Clients

- Keep `api` classes focused on third-party data access.
- Keep business orchestration, business decisions, and persistence out of `api`.
- By default, keep integrations with the same third-party data source in one API class.
- If that class becomes too large, split it only by third-party module or resource type.

## Optional Service-Supporting Layers

`handler` and `provider` are optional layers that support `service`. Introduce them only when they
create a clear boundary and reduce complexity in `service`.

### Handler

- Use handlers for strategy patterns and for strategy, type, event, or rule branches.
- Organize handlers by business package, such as `handler/rag`, `handler/order`, or
  `handler/message`.
- Make each handler process one clearly defined strategy or branch and support `service`.
- Do not let a handler replace `service` as the business orchestration layer.

### Provider

- Introduce providers when multiple data sources supply the same type of data, or when multiple
  business objects in one processing chain produce the same return type.
- Follow the repository's provider convention. Use an interface for a pure contract; use an
  abstract class only when implementations genuinely share state, a template method, or common
  implementation.
- Place source-specific or object-specific implementations under business packages.
- Use providers to support `service`. Providers may call `api`, `manage`, caches, or other
  infrastructure.
- Prefer `manage` for database access. Call `mapper` directly only when the mapper rules permit it.
- Do not return VO objects or assemble controller-facing responses in providers.

### Shared Boundaries

- Use `domain.dto` DTO objects for stable multi-field or evolving contracts between `service` and
  `handler` or `provider` when that matches the repository.
- Pass a semantic identifier, enum, or small value object directly when a DTO would only wrap
  arguments without owning a distinct contract.
- Do not leak controller-facing `Param` or `VO` objects, or persistence `PO` objects, across these
  boundaries.
- Do not create a handler or provider for simple CRUD, single-branch logic, or a short workflow.
- To reuse a small stateless code block, prefer a private method or ordinary component.

## Message Queue Boundaries

Use `listener` for inbound messages and `publisher` for outbound messages.

### Listener

- Receive messages, own message annotations or subscription configuration, deserialize payloads,
  and perform lightweight validation in `listener`.
- Convert message payloads to `domain.dto` DTO objects before calling `service`.
- Keep business orchestration, persistence logic, and VO assembly out of listeners.

### Publisher

- Encapsulate topic names, routing keys, tags, message payload construction, and MQ client calls in
  `publisher`.
- Keep business decisions in `service`. Call publishers through service orchestration.

### Shared Boundaries

- Keep MQ payload objects separate from internal service DTOs when their contracts differ.
- Do not call `manage`, `mapper`, or database APIs directly from `listener` or `publisher`.
- Carry task context, execution parameters, and object-storage addresses in messages. Do not place
  PDFs, images, large text, complete datasets, or other large binary payloads directly in messages.

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
- If Lombok is already available and its use matches the repository, `@RequiredArgsConstructor`
  is acceptable.
- Do not add Lombok solely for dependency injection. Avoid `@Autowired` field injection.
- Bind related configuration values through typed `@ConfigurationProperties` classes, following
  the repository's existing configuration pattern.
- Reserve `@Value` for isolated values when a dedicated properties class would add no clarity.
- Use Lombok only when it is already a project dependency and follows the established style.
- Prefer focused Lombok annotations such as `@Getter`, `@Setter`, and
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

- Follow the repository's established style for explicit `this`, loops, streams, lambdas, and
  callbacks. Do not rewrite equivalent code only to impose a preferred syntax.
- Remove imports, local variables, private code, configuration, and dependencies made unused by
  the current change. Before removing broader or pre-existing code, check Spring wiring,
  reflection, serialization, generated configuration, and public compatibility; report unrelated
  dead code instead of deleting it opportunistically.
- Keep code simple, clean, and direct.
- Extract a function or class only to remove genuine duplication or when a code block is long
  enough to impair readability.
- Do not add method wrappers that merely forward a call.
- Keep class names aligned with their package roles, such as `UserController`, `UserService`,
  `UserServiceImpl`, `UserManage`, `UserMapper`, `UserParam`, `UserDTO`, `UserVO`, and `UserPO`.
- Do not invent alternate suffixes for the same role.
- Do not use wildcard imports.
