# Spring Boot Maven Multi-Module Placement Reference

## Scope

Use this reference for Spring Boot Maven multi-module repositories that distribute application
assembly, shared data or persistence responsibilities, and business runtime behavior across modules.
It covers horizontal technical-layer modules and hybrid structures that combine foundation modules
with business-domain modules. Module names are not fixed; infer their actual responsibilities from
the repository.

Do not apply this reference to repositories organized primarily around Hexagonal or Clean
Architecture, `domain` / `application` / `adapter` boundaries, separate API contract and
implementation modules, or another topology whose ownership model does not match these layered or
hybrid structures. Follow those repositories' own architecture instead.

Within its applicable architecture, use this reference only to decide which Maven module owns a new
or changed Java file, resource, or configuration.

This reference determines Maven module placement only; it does not change the responsibility of
Controller, Service, Manage, Mapper, PO, Param, DTO, VO, configuration, listener, or publisher code.

## Contents

- [Repository Structure Takes Precedence](#repository-structure-takes-precedence)
- [Map Single-Module Responsibilities to Modules](#map-single-module-responsibilities-to-modules)
- [Common Module Responsibilities](#common-module-responsibilities)
- [Dependency Direction](#dependency-direction)
- [Placement Decision Table](#placement-decision-table)
- [Adding a Module](#adding-a-module)

## Repository Structure Takes Precedence

Do not infer module ownership from a module name alone. Before creating or moving a file, inspect:

- Repository instructions and architecture documents.
- The root POM and relevant child POMs.
- `<modules>`, `<parent>`, `<packaging>`, `<dependencyManagement>`, and project dependencies.
- The module containing the Spring Boot application class.
- Existing files with the same responsibility or business domain.
- Component scanning, configuration imports, and test layout.

Apply this precedence:

```text
explicit user instruction
-> repository instructions and architecture documents
-> established module responsibilities and dependencies
-> this reference
-> examples in this reference
```

Examples illustrate placement decisions. Never copy their module names, dependency versions,
package names, or topology into another repository as defaults.

## Map Single-Module Responsibilities to Modules

Use the package map in [Spring Boot Architecture](spring-boot-architecture.md) to place each file
with the module that owns its existing backend responsibility, following its package conventions
and the repository's dependency direction.

Do not place a file in the executable module merely because that module packages the final JAR,
scans the component, or can access every dependency.

## Common Module Responsibilities

Module names vary. Determine their roles from POMs, code, and documentation before applying these
categories.

### Application or Bootstrap Module

An application or bootstrap module normally owns:

- The Spring Boot application class.
- `application*.yml` and profile loading.
- Top-level application assembly and component scanning.

It normally does not own:

- Domain-specific Controllers.
- Business Service interfaces or implementations.
- Authentication or governance workflows.
- Mapper, Manage, Repository, or persistence objects.
- Business Param, DTO, or VO types.
- Domain-specific listeners, publishers, or configuration.

### Business Modules

A business module normally owns runtime behavior for its domain:

- Domain-specific Controllers.
- Service interfaces and Service implementations.
- Business workflow and transaction orchestration.
- Domain-specific security, integration, listener, publisher, task, or configuration code when the
  repository assigns those responsibilities to the business module.

Place a Controller with the module that owns the API's business meaning. For example, an
authentication Controller belongs to the existing authentication business module, not to the
bootstrap module.

Do not assume Param, DTO, VO, PO, enum, or exception types belong beside the Service. If the
repository has a dedicated model or entity module, place those types according to that module's
established responsibility.

### Model, Entity, or Contract Modules

A model module may own only persistence entities, or it may own every non-runtime data type. Inspect
the project before deciding.

When the repository defines one shared entity/model module for all entity types, it may contain:

- PO and database entities.
- Param request types.
- DTO transfer types.
- VO response types.
- Shared models and base types.
- Enums.
- Custom exception classes.

Keep runtime behavior out of such a module: no Controller, Service implementation, Mapper, Manage,
listener, publisher, Spring Security workflow, or business orchestration.

A custom exception class may belong to the shared model module while a global
`@RestControllerAdvice` remains in the application or adapter module defined by the repository.

### Persistence Modules

A dedicated persistence module normally owns the repository's established database access types,
such as Mapper, Manage, Repository, custom SQL resources, and persistence dependencies.

Do not put business workflow, Controller code, authentication decisions, response assembly, or
message consumption in a persistence module. When persistence access is affected, apply the Manage
and Mapper rules from [Spring Boot Data Access](spring-boot-data-access.md); this reference only
determines their module placement.

### Shared or Foundation Modules

Place code in a shared module only when its responsibility is stable and genuinely shared by the
modules that depend on it. Do not move domain-specific behavior into a lower-level shared module
merely to make it reachable without correcting an inappropriate dependency.

## Dependency Direction

Derive the allowed dependency direction from the repository. Preserve it when adding files or POM
dependencies.

- A lower-level model, foundation, or persistence module must not depend on an upper-level business
  or bootstrap module unless the repository explicitly defines a different architecture.
- A business module must not depend on the bootstrap module merely to reuse application code.
- The bootstrap module normally depends on business modules to assemble the executable application;
  business modules do not normally depend back on bootstrap.
- Do not introduce circular dependencies or a reverse dependency for implementation convenience.
- Reuse existing API, SPI, event, or shared-contract boundaries when the repository already uses
  them.

If the required placement cannot follow the existing dependency direction, explain the affected
modules and alternatives before changing the architecture.

## Placement Decision Table

Use this table only after confirming the repository has corresponding module roles.

| Responsibility from `spring-boot-architecture.md` | Typical module owner | Common placement error |
| --- | --- | --- |
| Application class, profile loading, top-level assembly | Application/bootstrap module | Treating bootstrap as the owner of every Spring Bean |
| Domain Controller, Service, ServiceImpl | Matching business-domain module | Putting a business Controller in bootstrap |
| PO, Param, DTO, VO, shared model, enum, custom exception | Existing model/entity/contract module when that is its documented role | Assuming an entity module can contain only database PO types |
| Mapper, Manage, Repository, custom SQL | Existing persistence module | Putting persistence access in bootstrap or an unrelated business module |
| Domain listener and publisher | Business module that owns the workflow | Putting message-driven business entry in persistence |
| Domain integration or configuration | Module that owns and uses the capability | Centralizing every configuration class in bootstrap |
| Global exception handler or response adapter | Repository-defined application/adapter module | Confusing exception classes with exception handling runtime code |

## Adding a Module

Prefer an existing module whose responsibility matches the change. Do not create a module for one
class, a small utility, an empty package hierarchy, or cosmetic symmetry.

Consider a new module only for a stable current boundary with independent build or release value,
dependency or permission isolation, long-term ownership, or substantial reuse. Ask only when the
change introduces an unapproved architecture decision or materially different ownership choices
remain unresolved by the request and repository evidence.

Before adding one, establish its responsibility and incoming/outgoing dependencies, confirm existing
modules cannot own it, and identify build, scanning, packaging, configuration, and migration impact.
Use existing authorization when the module and ownership were already requested or agreed. For module
changes, define or run the narrowest Maven build, test, or compile check that covers the changed
module and its required dependencies.
