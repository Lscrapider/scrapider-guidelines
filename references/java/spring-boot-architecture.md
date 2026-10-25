# Spring Boot Architecture Reference

## Scope and Shared Rules

Use this reference when adding or moving Java files, choosing package responsibilities, or changing
a component's role, including `handler`, `provider`, and third-party API client behavior. For Maven
multi-module placement, see [Spring Boot Multi-Module](spring-boot-multi-module.md).

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

When a handler or provider path uses `manage` or `mapper`, also load
[Spring Boot Data Access](spring-boot-data-access.md).

## API Clients

- Keep `api` classes focused on third-party data access.
- Keep business orchestration, business decisions, and persistence out of `api`.
- By default, keep integrations with the same third-party data source in one API class.
- If that class becomes too large, split it only by third-party module or resource type.
