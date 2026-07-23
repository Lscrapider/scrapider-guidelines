# Spring Boot Backend Reference

## Scope and Repository Precedence

Follow this reference by default for Spring Boot backend code. If the current repository has an
unambiguous convention that conflicts with a rule here, follow the repository for that point.

Before changing Java backend code, inspect the project's root package and existing package layout.
Do not copy or hard-code a root package from this reference.

## Contents

- [Package Map](#package-map)
- [Core Layered Architecture](#core-layered-architecture)
- [HTTP and Controller Boundary](#http-and-controller-boundary)
- [Domain Objects and Conversion](#domain-objects-and-conversion)
- [Persistence and External Integration](#persistence-and-external-integration)
- [Optional Service-Supporting Layers](#optional-service-supporting-layers)
- [Message Queue Boundaries](#message-queue-boundaries)
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

Use this standard synchronous package flow:

```text
controller -> service -> (manage / api / mapper) -> domain
```

- Keep `controller` as the request entry layer. Do not put complex business logic or direct
  database access in controllers.
- Put business logic and workflow orchestration in `service` and `service.impl`.
- Make controllers call `service`; never call `mapper` directly from a controller.
- Do not create layers for their own sake. Keep simple CRUD, single-branch logic, and short
  workflows directly in `service`.

## HTTP and Controller Boundary

- Use `Param` objects for request input.
- Use `domain.param` Param objects for controller request bodies. Do not use weakly typed `Map` or
  `JsonNode` request bodies.
- Use `VO` objects for frontend responses. Never return `PO` objects directly to the frontend.
- For paginated query APIs, name the pagination parameters `pageSize` and `pageNum`.
- Use `GET` for query APIs and `POST` for submit APIs.
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

- When building a `PO` from `JsonNode`, a third-party response, a DTO, or other intermediate data,
  prefer a static factory on the target persistence object.
- Use names such as `UserPO.fromApiResponse(...)` or `OrderPO.fromDto(...)`.
- Do not scatter this construction logic across `task`, `service`, or `manage`.

### Bean and Object Conversion

- Before implementing bean copying, object-to-Map conversion, or Map-to-object conversion, inspect
  the project's existing dependencies.
- Prefer an existing framework or project utility. For bean copying, use Spring `BeanUtils`,
  Hutool `BeanUtil` when Hutool is available, or another approved utility.
- If no suitable utility is available, ask the user whether adding a dependency is acceptable
  before writing repetitive field-copying code.
- Use a converter class in `converter` only when generic copying cannot represent the required
  business mapping semantics.
- Do not scatter manual conversion logic across `service` or `service.impl`.

## Persistence and External Integration

### Manage

- Keep `manage` focused on minimal database operation wrappers based on MyBatis Plus.
- Keep business orchestration, parameter normalization, and VO conversion out of `manage`.
- Prefer query, insert, update, and delete operations through `manage`. Keep the related business
  logic in `service` or `service.impl`.
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

- Use a Java abstract class as the provider contract.
- Introduce providers when multiple data sources supply the same type of data, or when multiple
  business objects in one processing chain produce the same return type.
- Define shared behavior, template methods, and return contracts in the abstract provider class.
- Place source-specific or object-specific implementations under business packages.
- Use providers to support `service`. Providers may call `api`, `manage`, caches, or other
  infrastructure.
- Prefer `manage` for database access. Call `mapper` directly only when the mapper rules permit it.
- Do not return VO objects or assemble controller-facing responses in providers.

### Shared Boundaries

- Pass `domain.dto` DTO objects between `service` and `handler` or `provider`.
- Do not pass `Param`, `VO`, or `PO` objects across these boundaries.
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
- Use JDK `java.util.Objects` for null checks and object equality.
- For string operations, use Hutool `StrUtil` when Hutool is already available. Otherwise, use an
  existing project-approved utility.
- Follow the same dependency-first approach for common collection, number, and date operations.
  Hutool examples include `CollUtil`, `NumberUtil`, and `DateUtil`.
- If no suitable utility exists, ask the user before adding a dependency.
- Do not reimplement functionality that an available utility already provides.
- Invoke static utility methods through their declaring class, such as `Objects.nonNull(...)` or
  `StrUtil.isBlank(...)`, instead of static-importing them.
- Return empty collections instead of `null`. Choose an empty collection whose mutability matches
  the method contract.
- For nullable boxed booleans, use `Boolean.TRUE.equals(value)` or
  `Boolean.FALSE.equals(value)`.
- Compare enum constants with `==`. Do not compare enums by ordinal or ad hoc string values.
- Use `java.time` for new date and time code. Reuse the project's existing date-time utilities and
  serialization conventions.
- Introduce `Date` or `Calendar` only when required by a legacy API, and convert at that boundary.

### Style and Naming

- When a Java class calls its own method, use explicit `this`.
- Prefer lambda style for collection processing, callbacks, and functional interface logic.
- Remove unused classes, methods, fields, local variables, imports, configuration, and
  dependencies. Do not keep dead code for possible future use.
- Keep code simple, clean, and direct.
- Extract a function or class only to remove genuine duplication or when a code block is long
  enough to impair readability.
- Do not add method wrappers that merely forward a call.
- Keep class names aligned with their package roles, such as `UserController`, `UserService`,
  `UserServiceImpl`, `UserManage`, `UserMapper`, `UserParam`, `UserDTO`, `UserVO`, and `UserPO`.
- Do not invent alternate suffixes for the same role.
- Do not use wildcard imports.
