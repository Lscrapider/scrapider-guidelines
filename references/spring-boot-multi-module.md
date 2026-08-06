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

This reference does not replace `spring-boot-backend.md` and does not redefine Java coding,
layering, data-access, API, configuration, or security rules. For every applicable multi-module
backend task:

1. Load `spring-boot-backend.md` to determine the file's responsibility and implementation rules.
2. Load this reference to place that responsibility in the correct Maven module.
3. Apply both references throughout implementation and review.

A layered or hybrid multi-module project distributes responsibilities from the single-module
backend reference across Maven modules. Module boundaries change physical placement, not the
responsibility of Controller, Service, Manage, Mapper, PO, Param, DTO, VO, configuration, listener,
or publisher code.

## Contents

- [Repository Structure Takes Precedence](#repository-structure-takes-precedence)
- [Map Single-Module Responsibilities to Modules](#map-single-module-responsibilities-to-modules)
- [Common Module Responsibilities](#common-module-responsibilities)
- [Dependency Direction](#dependency-direction)
- [Placement Decision Table](#placement-decision-table)
- [Example: Bootstrap, Foundation, and Business Modules](#example-bootstrap-foundation-and-business-modules)
- [Adding a Module](#adding-a-module)
- [Review Checklist](#review-checklist)

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

Use the package map in `spring-boot-backend.md` as the responsibility baseline. For each file:

1. Classify its responsibility, such as application assembly, business entry, business workflow,
   data model, persistence, external integration, or messaging.
2. Identify the module that already owns that responsibility or business domain.
3. Confirm that placing the file there follows the established dependency direction.
4. Check equivalent files in that module for package and naming conventions.
5. Apply all implementation rules from `spring-boot-backend.md`.

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

A component being discovered by the bootstrap module does not make the bootstrap module its owner.
The executable module assembles the application; business modules own business capabilities.

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
message consumption in a persistence module. Apply the Manage and Mapper rules from
`spring-boot-backend.md`; this reference only determines their module placement.

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

| Responsibility from `spring-boot-backend.md` | Typical module owner | Common placement error |
| --- | --- | --- |
| Application class, profile loading, top-level assembly | Application/bootstrap module | Treating bootstrap as the owner of every Spring Bean |
| Domain Controller, Service, ServiceImpl | Matching business-domain module | Putting a business Controller in bootstrap |
| PO, Param, DTO, VO, shared model, enum, custom exception | Existing model/entity/contract module when that is its documented role | Assuming an entity module can contain only database PO types |
| Mapper, Manage, Repository, custom SQL | Existing persistence module | Putting persistence access in bootstrap or an unrelated business module |
| Domain listener and publisher | Business module that owns the workflow | Putting message-driven business entry in persistence |
| Domain integration or configuration | Module that owns and uses the capability | Centralizing every configuration class in bootstrap |
| Global exception handler or response adapter | Repository-defined application/adapter module | Confusing exception classes with exception handling runtime code |

## Example: Bootstrap, Foundation, and Business Modules

The following is one project-specific mapping, not a universal module template:

```text
bootstrap
foundation-entity
foundation-db
business-auth
business-governance
```

For a repository that explicitly defines those responsibilities:

| Module | Owns | Does not own |
| --- | --- | --- |
| `bootstrap` | Application class, configuration loading, component scanning, and application assembly | Authentication or governance Controller and Service code; Mapper; Manage; model types |
| `foundation-entity` | All PO, Param, DTO, VO, shared models, enums, and custom exception classes | Controller, Service implementation, Mapper, Manage, listener, publisher, business workflow |
| `foundation-db` | Mapper, Manage, custom SQL, and database access dependencies | Controller, business workflow, authentication decisions, response assembly |
| `business-auth` | Authentication Controller, Service, ServiceImpl, Spring Security, JWT, and authentication workflow | Application entry point; authentication Param, DTO, or VO when those belong to `foundation-entity` |
| `business-governance` | Governance Controller, Service, ServiceImpl, domain configuration, listener, publisher, and governance workflow | Application entry point; governance Param, DTO, or VO when those belong to `foundation-entity` |

Correct authentication placement in this example:

```text
foundation-entity/.../param/LoginParam.java
foundation-entity/.../vo/LoginVO.java
foundation-entity/.../exception/AuthenticationException.java
business-auth/.../controller/AuthController.java
business-auth/.../service/AuthService.java
business-auth/.../service/impl/AuthServiceImpl.java
business-auth/.../config/SecurityConfig.java
```

Incorrect placement:

```text
bootstrap/.../controller/AuthController.java
business-auth/.../param/LoginParam.java
```

The first file confuses application assembly with domain ownership. The second ignores this
example repository's rule that all entity and transfer types belong to `foundation-entity`.

## Adding a Module

Prefer an existing module whose responsibility matches the change. Do not create a module for one
class, a small utility, an empty package hierarchy, or cosmetic symmetry.

Consider a new module only when the repository needs a stable boundary with independent build or
release value, dependency or permission isolation, long-term ownership, or substantial reuse. Before
adding it, explain:

- Its responsibility and public capability.
- Its incoming and outgoing dependencies.
- Why existing modules cannot own the code.
- Build, scanning, packaging, configuration, and migration impact.

Wait for confirmation when adding the module changes the architecture or when multiple ownership
choices remain reasonable.

## Review Checklist

Before creating, moving, or reviewing a file in a multi-module Spring Boot project, verify:

- [ ] `spring-boot-backend.md` is loaded for implementation and layering rules.
- [ ] Relevant parent, aggregator, and child POMs have been inspected.
- [ ] The file's single-module package responsibility is clear.
- [ ] The target module already owns that responsibility or business domain.
- [ ] A business Controller or Service has not been placed in bootstrap.
- [ ] Model types and custom exceptions follow the repository's actual model-module contract.
- [ ] Mapper and Manage types follow the repository's persistence-module contract.
- [ ] Existing dependency direction is preserved without a cycle or reverse dependency.
- [ ] No example module name, version, parameter, or infrastructure policy was copied as a default.
- [ ] The narrowest relevant Maven build, test, or compile check is defined or executed.
