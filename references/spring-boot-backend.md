# Spring Boot Backend Reference

Use this reference for Spring Boot backend code. Follow these rules unless the current repository has a clearly conflicting convention.

Before changing Java backend code, infer the project's root package and existing package layout from the repository. Do not hard-code a root package from this reference.

## Package Responsibilities

- `api`: External API clients, such as third-party service calls, remote data fetching, and integration clients.
- `config`: Spring configuration, such as `RestTemplate`, MyBatis Plus, Redis, Swagger, and thread pools.
- `controller`: HTTP entry points. Receive requests, validate parameters, call `service`, and return results. Do not put complex business logic or direct database access here.
- `domain`: Business data objects.
- `domain.constant`: Business constants, such as status values, cache key prefixes, and default settings.
- `domain.dto`: Data transfer objects for internal layer transfer or third-party response mapping.
- `domain.enums`: Business enums, such as status, type, risk level, and business category.
- `domain.param`: Request parameter objects for frontend query, create, and update input.
- `domain.po`: Persistence objects that usually map one-to-one to database tables and are used by `mapper`.
- `domain.vo`: View objects returned to the frontend. Do not return PO objects directly.
- `handler`: Optional service-supporting strategy handlers for strategy, type, event, or rule branches. See Optional Service-Supporting Layers.
- `listener`: Message queue consumers for inbound messages. See Message Queue Layers.
- `manage`: MyBatis Plus management classes, usually extending `ServiceImpl`. Keep this layer focused on simple database operations.
- `mapper`: MyBatis or MyBatis Plus database access interfaces.
- `provider`: Optional service-supporting data providers based on Java abstract classes. See Optional Service-Supporting Layers.
- `publisher`: Message queue publishers for outbound messages. See Message Queue Layers.
- `service.impl`: Service implementation classes. Put main business logic here.
- `task`: Scheduled jobs or background tasks, such as external data synchronization or periodic maintenance work.
- `<Project>Application`: Spring Boot application entry point, named according to the current project.

## Layering Rules

Standard synchronous request call chain:

```text
Controller -> Service -> Manage / API / Mapper -> Domain
```

Follow these rules when adding or changing backend features:

1. Keep `Controller` as the request entry layer only; do not write complex business logic there.
2. Put business logic in `Service` and `service.impl`.
3. Keep `Manage` limited to minimal database operation wrappers based on MyBatis Plus. Do not put business orchestration, parameter normalization, or VO conversion there.
4. Keep `Mapper` focused on database access. Write custom SQL only when MyBatis Plus functional queries cannot express the query or are clearly unsuitable.
5. Keep `api` classes focused on third-party data access. Do not put business orchestration, business decisions, or persistence there. Keep the same third-party data source in one API class when practical; split only by third-party module or resource type if the class grows too large.
6. Use `Param` objects for request input.
7. Use `VO` objects for frontend responses.
8. Use `PO` objects for database persistence.
9. Do not return `PO` objects directly to the frontend.
10. Do not call `Mapper` directly from `Controller`; controllers should call `Service`.
11. Prefer database query, insert, update, and delete operations through `manage` with MyBatis Plus. Keep business logic in `service` or `service.impl`.
12. When a Java class calls its own method, use explicit `this`.
13. Prefer lambda style for collection processing, callbacks, and functional interface logic.
14. When building a `PO` from `JsonNode`, third-party responses, DTOs, or intermediate data, prefer a static factory on the target entity, such as `UserPO.fromApiResponse(...)` or `OrderPO.fromDto(...)`. Do not scatter this logic across `task`, `service`, or `manage`.
15. Prefer Hutool utilities for common null checks, string handling, collection checks, number conversion, and date conversion, such as `Objects`, `StrUtil`, `CollUtil`, `NumberUtil`, and `DateUtil`. Do not rewrite common utility logic unless Hutool does not fit the scenario.
16. Remove unused code promptly, including unused classes, methods, fields, local variables, imports, configuration, and dependencies. Do not keep dead code for possible future use.
17. Use `pageSize` and `pageNum` for paginated query APIs. Use GET for query APIs and POST for submit APIs.
18. Do not write local `@ExceptionHandler` methods in controllers. Use module-level or global `@RestControllerAdvice`, and log exception objects to preserve full stack traces.
19. Controller request bodies must use `domain.param` Param objects. Do not use weakly typed `Map` or `JsonNode` request bodies.
20. Keep code simple, clean, and direct. Add functions or classes only when there is real duplication, related repeated code, or a block is long enough to hurt readability. Avoid meaningless method wrappers.
21. For object copying, object-to-Map, or Map-to-object conversions, prefer existing project dependencies or framework utilities. If existing tools do not match the business semantics, create a clear converter class in a `converter` package. Do not scatter manual conversion logic through `service` or `serviceImpl`.
22. `handler` and `provider` are optional `service`-supporting layers, not default required layers. Introduce them only when they clearly reduce strategy-branch complexity or data-provider complexity in `service`.
23. Do not create layers for their own sake. Simple CRUD, single-branch logic, and short workflows should stay directly in `service`.
24. For message queues, use `listener` for inbound messages and `publisher` for outbound messages. `listener` should call `service`; business workflows should call `publisher` through `service` orchestration.

## Optional Service-Supporting Layers

`handler` and `provider` are not required layers in the standard call chain. Use them only when they create a clear boundary and reduce `service` complexity.

- `handler`: Use for strategy patterns, strategy branches, type branches, event branches, or rule branches. Organize handlers by business package, such as `handler/rag`, `handler/order`, or `handler/message`. Each handler should process one clear strategy or branch, serve `service`, and must not replace `service` as the business orchestration layer.
- `provider`: Use Java abstract classes for provider contracts. Use providers when different data sources provide the same type of data, or when different business objects in one processing chain provide the same return type. Define shared behavior, template methods, and return contracts in an abstract provider class; implement source-specific or object-specific providers under business packages. Providers serve `service`; they may call `api`, `manage`, cache, or other infrastructure. Prefer `manage` for database access; call `mapper` directly only when the mapper rules allow it. Providers must not return VO objects or assemble controller-facing responses.
- Pass `domain.dto` DTO objects between `service` and `handler` / `provider`. Do not pass `Param`, `VO`, or `PO` across these boundaries.
- Do not create `handler` or `provider` for simple CRUD, single-branch logic, or short workflows.
- If the goal is only to reuse a small stateless code block, prefer a private method or ordinary component instead of introducing `handler` or `provider`.

## Message Queue Layers

Use `listener` and `publisher` only for message queue boundaries.

- `listener`: Receive MQ messages, handle message annotations or subscription configuration, deserialize payloads, perform lightweight validation, convert payloads to `domain.dto` DTO objects, and call `service`. Do not put business orchestration, persistence logic, or VO assembly in listeners.
- `publisher`: Send MQ messages, encapsulate topic names, routing keys, tags, message payload construction, and MQ client calls. Keep business decisions in `service`; publishers should only execute the outbound message send.
- Keep MQ payload objects and internal service DTOs separated when their contracts differ. Convert message payloads to DTOs before entering `service`.
- Do not call `manage`, `mapper`, or database APIs directly from `listener` or `publisher`.
