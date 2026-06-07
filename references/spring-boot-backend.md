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
- `manage`: MyBatis Plus management classes, usually extending `ServiceImpl`. Keep this layer focused on simple database operations.
- `mapper`: MyBatis or MyBatis Plus database access interfaces.
- `service.impl`: Service implementation classes. Put main business logic here.
- `task`: Scheduled jobs or background tasks, such as external data synchronization or periodic maintenance work.
- `<Project>Application`: Spring Boot application entry point, named according to the current project.

## Layering Rules

Standard call chain:

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
