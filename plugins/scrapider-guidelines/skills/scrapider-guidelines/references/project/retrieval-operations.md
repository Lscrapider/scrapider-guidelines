# Retrieval Operations

Use this reference when defining or revising execution details for a selected retrieval profile,
or when setup verification needs query-scoping guidance. Keep profile selection in
[Capability Recommendations](capability-recommendations.md#select-one-retrieval-profile).
Follow the applicable host and tool routing contract; this reference grants no permission to
install tools, build indexes, edit code, or change configuration.

## Retrieve Enough Evidence with Fewer Repeated Calls

Call supporting tools only for unresolved evidence; do not impose one fixed sequence. Expand to a
complete file when the selected body omits declarations or initialization that affect the answer.

Skip discovery for a known target. For an unknown target, narrow by path or symbol before retrieving
its body. Expand to callers, types, or configuration only for a concrete evidence gap; complete
truncated bodies before drawing conclusions about their behavior.

Reuse existing indexes and complete returned source; do not retrieve the same body through another
tool without a freshness or completeness gap.

Choose retrieval granularity for the question. Retrieve several needed methods together when they
sit in a small, cohesive class or file, rather than through separate discovery and body requests. Batch
independent reads whose targets are already known; keep queries that depend on earlier results
sequential.
Before stopping, check that the answer addresses the requested facts with adequate evidence; do not
expand a bounded question into an exhaustive account of every surrounding implementation detail.

For a handoff, identify the unresolved question, known path or symbol, and source already obtained.
Across message or language boundaries, verify the relevant producer, routing and consumer evidence;
do not report a path as absent solely from a missing graph edge.

## Codegraph Query Scope

Confirm the Codegraph package and exposed operations before use. Scope queries to the affected
symbols and paths, verify returned targets, and query only missing evidence.
Resolve ambiguous same-name relationships against receiver declarations and call-site source; use a
precise-symbol tool when that evidence still leaves the binding unresolved.
Use native retrieval for an unavailable index, uncovered content, or stale results, within the exposed
contract. Respond to actual coverage gaps rather than rebuilding or rechecking the index on every task.
