# Spring Boot Messaging Reference

## Scope and Shared Rules

Load this reference when a task or affected call path changes message production, consumption,
payloads, routing, or listener/publisher behavior, including indirect changes to those paths. Apply
the same routing during implementation, refactoring, debugging, and review.

Read the [Spring Boot Backend](spring-boot-backend.md) baseline first; reuse it if already read and
unchanged for this task. It supplies repository precedence, core layering, configuration and contract
rules, credentials guidance, and Java conventions. Load architecture,
HTTP/domain, or data-access references only when their own routing triggers also apply.

## Message Queue Boundaries

Use `listener` for inbound messages and `publisher` for outbound messages.

### Listener

- Receive messages, own message annotations or subscription configuration, and deserialize payloads
  in `listener`. Add only necessary checks not already provided by deserialization or an established
  guarantee, following [Validation and Boundaries](../shared/validation-and-boundaries.md).
  Being a message consumer does not require a handwritten validation block.
- Deserialize into or map to the typed input the service consumes. Reuse an existing `domain.dto`
  type when it expresses that contract; do not create a second identical type solely for the listener.
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
