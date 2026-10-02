# Code Design and Changes

Apply these rules to code and configuration changes across technology stacks, together with the
working principles and workflow in `SKILL.md`. They cover scope, business contracts, reuse,
responsibility boundaries, and refactoring.

## Change Scope

- Touch only files and lines that serve the request. Match established naming, style, structure,
  and dependency choices where they remain appropriate for the requested result.
- Refactor when requested or needed to complete the change cleanly. Preserve behavior, public APIs,
  request/response shapes, and database semantics the task does not authorize changing. Maintaining
  unchanged behavior does not require retaining a replaced implementation or adding compatibility code.
- Remove what the current change makes unused. Report unrelated dead code or design issues instead
  of fixing them opportunistically; do not reformat unrelated code.

## Business Contracts

- Treat default values, thresholds, enums, and strategy parameters as business contracts. Reuse
  existing constants and configuration; do not rename equivalent parameters or duplicate them as
  magic values.
- Before consolidating a duplicated value, search the system components that share that protocol,
  including other languages and available related repositories. Migrate only covered copies, keep
  remaining values byte-identical, and name the cross-language linkage and synchronization owner in
  delivery. State material search limits instead of claiming unseen components were checked.
- Change a default or introduce a different contract value only when authorized. The current request
  can supply that authorization; do not ask for it again. If a new requirement leaves its scope
  ambiguous, clarify whether it changes the global default or one scenario before changing values.

## Current Requirements and Reuse

- Implement only the current request. Do not add speculative options, extension points, generic
  frameworks, or configuration for requirements or callers that do not exist.
- Before adding a helper, wrapper, validator, or query, search the repository, standard library,
  framework, and approved dependencies for an equivalent capability.
- Reuse only when behavior and contract are equivalent. A generic utility must not weaken domain
  validation or otherwise change required semantics.
- Prefer a precise query over loading a full collection and filtering in memory when ordering and
  conditions are equivalent. Keep the full read when the caller needs it or equivalence is unproven.
- If the change grows large, check whether a smaller direct implementation solves the same problem.

## Abstraction and Responsibility

- Add a layer, module, class, or cross-file abstraction only for a distinct current responsibility:
  a domain invariant, transaction, authorization, state transition, conversion, external integration,
  or stable multi-step operation. A name or architectural pattern alone is not a responsibility.
- Do not add a layer merely to forward or rename a call, perform a trivial null/empty check, or make
  a short method look organized. Remove intermediate calls that own no behavior or contract.
- A private function in an existing file is not an architectural layer. Extract one when it removes
  verified duplication or materially improves readability. Do not create a cross-file abstraction
  solely for hypothetical reuse or split cohesive code for theoretical extensibility. A required
  responsibility boundary can have one caller; caller count alone does not justify or forbid it.
- Keep distinct handlers, strategies, or stage-specific classes when they represent actual dispatch
  identities, business semantics, state keys, or lifecycle stages, even if their bodies match today.
  Identical text alone does not prove interchangeable responsibilities.
- Decide keep-versus-merge from code, documentation, tests, callers, and the request. Ask only if an
  unresolved choice changes registered identities, lifecycle stages, public or business contracts,
  or an established extension boundary. Explain the concrete tradeoff once.
- Extract shared behavior only when it is stable and reduces the work needed to understand or
  change it. Weigh removed duplication against added indirection, inheritance, and dependencies;
  repeated trivial delegation or result construction alone does not justify a shared layer.
  Preserve needed semantic entry points without adding another dispatch or forwarding layer.

## Refactoring and Replacement

- Trace callers before replacing code, including registrations, configuration, reflection, and
  serialization where relevant. Update the affected callers to the chosen implementation directly.
  Update active contract documentation and examples affected by the replacement as well.
- Delete the superseded implementation and its unused imports, exports, files, configuration,
  and dependencies within the change's scope. Do not leave renamed copies or dead entry points.
  Update affected tests to exercise the replacement and preserve coverage of required behavior;
  remove assertions only when the authorized change makes their expectation obsolete.
  A constructor or responsibility move does not itself authorize changing expected business
  outcomes. When code, tests, and documented behavior disagree, establish the intended contract
  from the request and concrete change evidence, or report the unresolved conflict. Making tests
  match the current implementation does not by itself validate the migration.
- Do not preserve an old signature through a forwarding wrapper, re-export an old name as an alias,
  accept both renamed fields, keep dual old/new reads or writes, or fall back to the old code to make
  the migration appear complete. Complete the in-scope migration and keep one active path.
- If a known caller or data migration outside the authorized scope prevents removal, identify the
  concrete dependency and resolve the scope with the user before that breaking change. Do not invent
  hypothetical external callers or silently introduce a compatibility layer.
- Fix bugs at the responsible implementation. Do not add a catch-all, default value, special-case
  branch, mode switch, or adapter merely to hide a broken invariant or keep obsolete code working.
  Required external SDK/protocol conversion remains a valid boundary, not permission for a bridge
  between superseded internal versions.

Example: when replacing `loadOld` with `load`, update its callers and remove `loadOld`. Keeping
`loadOld(...) -> load(...)`, trying `load` then falling back to `loadOld`, or accepting both signatures
leaves the obsolete contract alive and violates the replacement rule.

## Existing Deployment Boundaries

- For CI, Docker, or deployment work, preserve the runtime model, routing, output mode, service names,
  credentials, and topology outside the requested change. Introduce a different architecture, proxy,
  output format, or packaging model only when explicitly requested or the current runtime cannot
  consume the required artifact. Prune replaced build/runtime paths within the authorized migration.
