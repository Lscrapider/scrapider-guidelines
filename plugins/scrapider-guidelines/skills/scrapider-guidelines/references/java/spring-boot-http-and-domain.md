# Spring Boot HTTP and Domain Reference

## Scope and Shared Rules

Load this reference for HTTP/controller boundaries, data contracts for internal layer transfer or
third-party responses (including choosing their representation), `Param`/`DTO`/`PO`/`VO`, and any
bean/object conversion, including object-to-Map and Map-to-object conversion. This includes service
return contracts across those boundaries. Apply the same routing during implementation, refactoring,
debugging, and review, including examination of existing contracts without a proposed edit.

## HTTP and Controller Boundary

- Use typed `domain.param` Param objects for complex request bodies or query contracts when that
  matches the repository. Simple identifiers and filters may use path variables or request
  parameters without creating a one-field Param class.
- Do not use weakly typed `Map` or `JsonNode` request bodies when a stable typed contract exists.
- Apply [Validation and Boundaries](../shared/validation-and-boundaries.md) to input handling.
  Typed Param/DTO fields describe consumed data; they do not require rejecting unrelated extra
  JSON fields. Reuse deserialization and necessary Bean Validation without manual field-set gates
  or duplicate checks.
- Put necessary field constraints on the existing Param: use `@NotNull` for required references,
  `@NotBlank` for strings that must contain non-whitespace text, and `@NotEmpty` for required
  nonempty collections or containers. Add only constraints required by the request contract; do
  not stack equivalent annotations or add every available constraint.
- Ensure the controller entry actually triggers validation through the project's `@Valid` or
  `@Validated` setup, including nested validation where needed. Do not repeat accepted Param
  constraints with service/helper `if` branches or manual validator calls downstream.
- For updates, validate newly supplied or changed fields and affected business rules. In a partial
  update, omitted fields retain their stored values; do not apply create-only requiredness to them
  or revalidate unrelated stored fields. Keep checks that need persisted state or business decisions
  at the responsible business boundary rather than replacing them with field annotations.
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
