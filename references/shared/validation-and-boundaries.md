# Validation and Boundaries

## Use During Implementation and Review

Decide whether a check belongs before writing it. Implement the business operation directly using
existing guarantees; do not start with exhaustive defenses and rely on review to remove them.
Apply the same standard during review. These are coding decisions, not a requirement to expose
internal reasoning, write per-field explanations, or produce another checklist for the user.

A check needs a current business requirement or concrete failure consequence, an actual gap in
existing guarantees, and a reason to handle that gap here. “Just in case”, “external input”,
“contract consistency”, “safer”, and hypothetical future callers are not sufficient on their own.
Use the smallest existing mechanism that provides the required behavior. This applies equally to
`if`, assertions, annotations, schemas, utility calls, validators, and wrapper functions.

## Field Contracts Do Not Imply Exact Payloads

- A consumer defines the fields it uses, not an exclusive inventory of everything a producer may
  send. Do not reject an HTTP body, MQ message, or third-party response solely because it contains
  unrelated extra fields, including in nested objects.
- A DTO, documented field list, example JSON, or producer's current output does not establish that
  unknown fields are forbidden. Optional fields remain optional; do not require placeholders or
  invent ranges, formats, cross-field dependencies, or requiredness for completeness.
- Do not hand-write exact-field or unknown-field gates using `TSet`, `Set`, `keySet`, set equality,
  differences, membership scans, field counts, or equivalent loops and repeated `if` statements.
  Changing the syntax does not make the restriction legitimate. This does not ban sets used for
  business membership, deduplication, or other actual business operations.
- Do not enable a framework's unknown-field rejection as an alternative way to impose the same
  unsupported restriction. A genuinely closed protocol requires an explicit requirement to reject
  unknown fields, not an inference from its field definitions. When required, use the established
  schema or decoder rather than hand-maintaining a second field inventory.
- Use typed input or explicit consumption of intended fields. Ignoring extras does not mean
  forwarding arbitrary input into persistence, authorization fields, or downstream operations.
  If the actual path permits an unintended write, address that mapping or permission boundary;
  do not use a hypothetical risk to justify an exact-field gate on every payload.

Example: an operation consumes `orderId` and `quantity`. Adding an unused `displayLabel` must not
make the request fail. Whether `quantity` satisfies the actual ordering rule is a separate business
question; it does not justify inspecting all incoming keys.

## Trust Guarantees on the Actual Call Path

- Once an entry point or caller establishes a condition, downstream functions consume it directly
  while the guarantee remains intact. In particular, a helper with only that caller must not
  repeat the same null, empty, type, or range checks for independent robustness.
- Locally constructed arguments, exhaustive branches, enforced types, deserialization, and
  immutable validated values can establish guarantees without an explicit preceding `if`.
  Inspect the actual construction and use; do not invent ways the current code cannot call itself.
- A frontend convention alone does not enforce a server boundary. For an actual external entry,
  establish only the conditions the operation needs and existing mechanisms do not already enforce.
- A function, layer, process, or storage boundary does not automatically invalidate a guarantee.
  Repeat a check only for a concrete new gap, such as an actual independent untrusted caller,
  mutation that invalidates the earlier result, or a state change relevant to authorization or
  concurrency. Address that gap rather than rechecking the entire payload.
- Preserve atomic conditional updates, uniqueness constraints, and state-transition guards that
  protect concurrent operations. An earlier read is not a substitute for protection at the write.

Example: `submit` validates a value and passes it unchanged to its only helper `save`. The helper
uses it directly. Add a new boundary check if a later change introduces a real caller that does not
establish the condition, not in anticipation of that hypothetical change.

## Use Existing Type, Framework, and Storage Guarantees

- Do not manually repeat required deserialization, schema, or Bean Validation guarantees. A type
  declaration counts only for what it actually enforces in the current runtime; for example,
  Python type hints alone do not validate external data.
- A complete persisted row from a table with three enforced `NOT NULL` columns already guarantees
  those columns are non-null. If row existence needs handling, handle that once rather than checking
  all three columns again. The implication comes from the schema and query, not from one field
  being more important than the others.
- Do not assume the same guarantee for an outer join, partial projection, nullable expression,
  incomplete DTO, or unsaved object. Non-null also does not imply nonblank or domain-valid. Check
  such properties only when the operation actually requires them and no existing guarantee covers them.
- Do not implement format or type pre-checks that merely duplicate an existing parser with suitable
  failure behavior. Use the parser or framework directly.
- Do not query for existence before an operation solely to predict a failure or result it already
  handles correctly. Use its result or the existing exception mapping when that satisfies the
  business contract. Preserve a prior read when it supplies needed data or a distinct business decision.
- If an empty collection already produces the correct result through zero iterations or the called
  API's normal behavior, omit an empty-check branch that adds nothing. Preserve distinctions the
  business actually makes between absent input, empty input, and a failed operation.

## Let Exceptions Reach Their Responsible Boundary

- An exception being possible does not itself justify a pre-check. If direct use or parsing already
  fails appropriately and the existing exception boundary provides the required handling, do not
  add a branch that throws another exception with the same effective result.
- Do not add null checks solely to replace an internal null dereference or missing-key error with
  a different generic exception. Check first when it must produce a meaningful business outcome,
  support different recovery, or prevent a concrete harmful effect before failure.
- Use specific validation or business exceptions when callers must distinguish, correct, retry, or
  branch on the condition. Merely changing error wording is not enough unless that distinction is
  part of the required error contract.
- Unexpected internal failures without local recovery belong to the existing global exception and
  logging policy. Do not catch, log, wrap, and rethrow at each layer without actionable context,
  recovery, or required boundary translation.
- Do not substitute empty strings, zero, empty objects, or success responses for broken internal
  guarantees just to avoid an exception. Defaults are appropriate only when the actual contract
  defines that behavior.
- A global exception handler does not replace authorization, required business decisions, atomic
  integrity guarantees, or protection against partial side effects. Nor does it automatically make
  every exception equivalent: respect established HTTP errors, transaction behavior, and MQ
  retry/acknowledgement semantics where they affect the operation.

## Apply the Same Standard in Review

Assess the actual guarantees and consequences in the scoped code. Report unnecessary restrictions,
repeated checks, and exception plumbing when their redundancy is supported by the current path.
Do not propose missing checks without identifying a concrete required behavior that existing
mechanisms fail to provide. “More defensive” and “covers every case” are not review findings.

Neither the number of `if` statements nor replacing them with a validator determines quality.
Business branches for status, routing, permissions, and eligibility remain ordinary necessary code.
An already-correct implementation needs no extra checks or abstractions to satisfy this reference.

When editing existing code, establish what a check protects before removing it. This reference does
not authorize broad deletion of unrelated checks or silent changes to established error, retry,
or protocol behavior. A request to remove overvalidation authorizes the scoped simplification;
do not preserve a demonstrated redundant check merely because it already exists.
