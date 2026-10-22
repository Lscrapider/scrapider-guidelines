# Spring Boot Data Access Reference

## Scope and Shared Rules

Load this reference when a task or affected call path adds or changes database access, including
query/ORM calls, persistence behavior, or conditions that affect persistence even when changed
indirectly outside `manage` or `mapper`. Apply the same routing during implementation, refactoring,
debugging, and review.

Read the [Spring Boot Backend](spring-boot-backend.md) baseline first; reuse it if already read and
unchanged for this task. It supplies repository precedence, core layering, configuration and contract
rules, credentials guidance, and Java conventions. Load the architecture,
HTTP/domain, or messaging references only when their own routing triggers also apply.

## Manage

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

## Mapper

- Keep `mapper` focused on database access.
- Prefer MyBatis Plus functional queries. Write custom SQL only when those queries cannot express
  the operation or are clearly unsuitable.
- For MyBatis Plus query and update construction, prefer lambda wrappers such as
  `LambdaQueryWrapper` and `LambdaUpdateWrapper` over raw string column names.
