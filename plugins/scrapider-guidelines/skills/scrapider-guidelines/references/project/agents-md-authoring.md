# Project AGENTS.md Authoring

Create, update, or rebuild project `AGENTS.md` instructions from project evidence and the author's choices.

- [Scope](#scope)
- [Establish the purpose](#establish-the-purpose)
- [Recommend and let the author choose](#recommend-and-let-the-author-choose)
- [Select content and integrate it](#select-content-and-integrate-it)
- [Check and deliver](#check-and-deliver)
- [Bundled collaboration roles](#bundled-collaboration-roles)

## Scope

Write only the requested `AGENTS.md`. Establish the actual project root rather than assuming the
host's launch directory. Read applicable parent, root, and scoped instructions and the complete
existing target before editing. Inspect relevant project documents, manifests, and capability metadata.

Do not install capabilities, configure hosts, build indexes, register agents, or create agent
definitions, plan files, or companion project documents during authoring. These limits, and
restrictions imposed only on the authoring run, concern this operation; do not turn them into
permanent project policy. An explanation-only request does not authorize a file edit.
Adopt Scrapider's coding rules or bundled agents only from existing project rules or the author's choice.
Write host-specific tool-routing mandates only when the target project's instructions or author
establish them; a requirement of the authoring host alone is not target-project policy.

When creating or restructuring entries for confirmed capabilities, read the matching
[scenario examples](examples/capability-entries.md) and apply
[Capability Entry Format](#capability-entry-format). Reuse confirmed choices without reopening recommendation.

## Establish the Purpose

For creation, identify the project responsibilities and decisions that future agents need. For an
update, address the named changes in their existing sections. For a rebuild, establish what the author
wants to refresh: capability choices, workflows, collaboration, project conventions, or overall brevity.

When defining or revising execution details for an already selected retrieval profile, read
[Retrieval Operations](retrieval-operations.md) for query scope, source completeness, batching, and
handoff. Reuse the confirmed profile choice rather than reopening selection for these changes.

Use the conversation and existing rules first. If the rebuild focus is still unclear, offer a short
choice of relevant directions with a recommendation; do not make the author invent a specification.
Do not repeat a question already answered or refresh the entire tool catalog for every rebuild.

For a skill/tool-area rebuild, reassess every capability category and combination in that area for
current behavior, missing handoffs, and overlap. Limit a single-tool or wording-only request to its
stated scope.

Evaluate existing entries using the same recipe guidance as initialization: offer what to retain,
augment, replace, or retire and why, including the current setup when it remains suitable.
A section-level refresh does not authorize
adopting an unselected alternative or changing unrelated project policies. Apply an in-scope choice
already confirmed in the conversation even when the old file still names only part of that combination.

Inspect only the evidence needed for that purpose. Reuse the target's language, heading hierarchy,
section responsibilities, valid constraints, custom roles, and explicit choices unless the user changes
them. Rebuild from the author's existing organization, using their chosen baseline when supplied.
Apply the requested updates to that source document rather than regenerating it from a summary.
By default, carry unaffected headings and rules forward verbatim. Reword or reorganize them only when
the requested change or a demonstrated problem warrants it. Do not reconstruct unchanged project
instructions from README files during a focused capability refresh. Do not invent frameworks,
commands, business rules, or a new layout merely because a rebuild was requested.
Keep a brief capability summary in its existing area and detailed conditional routes in their original
sections; updating the summary does not authorize absorbing or deleting those sections. A more specific
usage example elsewhere does not narrow an existing general requirement or remove its stated priority.
A user-supplied document is a valid rebuild source; do not require repository history or repeated
backup snapshots when that source suffices for the requested comparison.

## Recommend and Let the Author Choose

Load [Capability Recommendations](capability-recommendations.md) when selecting capability combinations.
Offer two or three named workflows based on the project's tasks. Explain the scenario, division of
work, handoff, and meaningful setup or usage cost; recommend one and say why it
fits. Include a simple existing-tools option when it meets the need. Let the author select a proposal
or provide an alternative. Keep retrieval, coding, planning, and UI responsibilities distinct, and
allow complementary roles in one combination. Offer independent optional additions separately.
When code retrieval is in scope, select one profile for the project. Write only that profile's included
tools and conditional routes into `AGENTS.md`; keep alternative profiles in the recommendation conversation.
Use the profile's documented conditions for each tool; do not require every included tool on each task.

Reuse an explicit selection without asking again. For an unresolved choice that materially changes
the project's workflow or dependencies, wait for the answer before writing it as an adopted rule;
continue independent inspection and organization meanwhile.
After the author chooses, apply all agreed changes and their necessary cross-references. Preserve
entries whose evaluation calls for no change. An answer about one category does not resolve the other
open categories in a section-level refresh; continue their assessment and relay any necessary choices.

When authoring is delegated, the authoring agent assesses the project and formulates the recommendations
and necessary questions. Send each question, its concrete options, and recommendation rationale to the
primary agent for relay to the user; do not have the primary agent invent choices in advance or bypass
the relay with a separate user prompt. The primary agent forwards the user's answer faithfully, and
the authoring agent resumes from that answer. Carry all confirmed preferences within the requested
scope into the handoff, including selected combinations absent from the old file, rather than only the
most recently discussed plugin. Neither agent should repeat answered questions or manufacture a new
choice when the task is already specified.

When refreshing an outdated choice, check its actual identity, relevant current behavior, and host
support before proposing a replacement. Do not declare it obsolete from its name or age alone. Retain
only selected capabilities in the document, including deliberately selected optional additions.
Unselected candidates and setup advice belong in the conversation.

### Capability Entry Format

Describe each selected capability in its own peer entry. Use fields in this order when applicable:
**scenario** (the tasks this capability serves), **boundary** (which tool owns overlapping work),
**skip** (an additional excluded scenario), and **fallback** (unavailability or conflicts).
Use each label at most once; omit empty or redundant fields. Preserve all adopted scenarios,
decision conditions, and fallback paths; do not trim necessary information to meet a sentence or
length target. List distinct tasks under the single scenario field when an umbrella description
would hide when to use the capability.
Write labels in the target document's language and keep one label set throughout. Exclude need-only
scenarios, tool parameters and invocation tutorials, installation snapshots, benchmarks, and savings
claims.
Native search, file reads, and ordinary edits are default operations, not separate tools to adopt.
Mention them within another capability's applicable scenario, boundary, or fallback; a combination
may be named "Codegraph + native retrieval" without giving native retrieval its own field set.

A combined scenario contains separate entries for its tools and skills, with their cooperation expressed through
boundaries; do not collapse the scenario into one large capability entry. For overlapping alternatives,
select one unless the author chose distinct responsibilities for both. Translate and adapt the example
without importing unselected capabilities or the full example catalog.

When rebuilding, convert capability prose to these fields without changing its meaning. Keep cooperating
tools as peer entries in the existing capability list or table. In a list, keep the parent item to the
capability's name and purpose;
put each field in its own subitem. In a table, retain one row with line breaks between fields.
Keep invocation mechanics in the capability's reference; do not remove use scenarios to shorten an entry.
In a new document, organize selected skills, plugins, and MCP/native-tool combinations together in
one capability area under a heading suited to its content. Make each capability a peer
entry there. Do not put a coding skill only under engineering conventions while giving its companion
retrieval tools a separate top-level section. Detailed conventions and scoped workflows may refer back
to these responsibilities without duplicating them.

## Select Content and Integrate It

Check applicable dimensions below by meaning; preserve equivalent existing descriptions, references,
and headings. Do not impose the table's labels or order on the document.

Fill uncovered applicable rules in their existing sections; add a heading only for a subject with no
existing section. Do not invent policies or capabilities to fill a dimension. Ask the author when a
missing decision changes workflow, dependencies, or business behavior.

| Dimension | Useful lasting content |
| --- | --- |
| Project structure | Brief labels for the top-level modules and their responsibilities, plus one general requirement to read the relevant module README and applicable instructions before working there. |
| Capabilities and workflows | Selected Skill, MCP, plugin, or native-tool combinations and the tasks they serve. |
| Collaboration | When delegation helps, role responsibilities, dependency handling, handoffs, and primary-agent acceptance. |
| Business and development conventions | Existing contracts and project-specific code constraints that materially affect decisions; reference established guidance instead of copying it. |
| Execution and delivery | Actual authorization limits, proportionate verification, and the evidence expected in delivery. |
| Language | The author's chosen language for documents, comments, commits, or responses, where specified. |

For creation, assess collaboration even when no custom roles were named. If independent tasks can be
delegated and the choice is open, offer existing or bundled roles and a direct-work option. Record the
chosen responsibilities and acceptance boundary.
If direct work is the confirmed choice, record that choice without adding conditional delegation.
When selected roles are not exposed by the host, retain the responsibilities and have the primary agent
perform them or map them to available agents; do not leave work blocked on a role name.

Keep a statement only if it changes how future work should be done. Use clear, action-oriented rules
and brief module labels. Put capability roles in their section, business rules with business
rules, and multi-agent collaboration in its own established section. Keep development conventions,
execution requirements, and language rules distinct when the source already separates them.
Route document reading by the task's affected areas; do not require every task to read all project
overviews or product documents unless that is an existing project rule or an explicit author choice.
Read the root README when module ownership or entry points remain unclear; do not require it on every task.

When initializing a root document, briefly identify the top-level modules and their responsibilities.
State the module-reading requirement once, in one short sentence: before working in a module, read its
README and applicable `AGENTS.md` instructions. Make it apply to any affected module rather than
enumerating selected module names again. Let each module README provide further reading guidance;
do not expand the root rule into a reading catalog, a module-to-README table, repeated paths, or an
explanation of which kinds of documents to read next. Do not invent missing README files or write them
during this operation.

Keep detailed module structure, history, architecture, and protocols in the corresponding README or
module documentation. Keep selected capability duties, workflows, collaboration, author constraints,
reading requirements, and verification or delivery rules in root `AGENTS.md`.
Do not move those agent instructions into README files to make `AGENTS.md` shorter.

Retain genuine project-wide work constraints, such as checking both sides of a cross-module change,
without summarizing the underlying architecture or message format. Module-specific procedures,
document-maintenance rules, and deeper reading entry points stay in the module README, even when it
states them as requirements. Reference them through the module-reading rule without repeating paths
or navigation in the root file. Do not edit other documents to relocate content during authoring.

For an adopted skill, normally use one short peer entry in the
[capability-entry format](#capability-entry-format). State when it applies, what it contributes, and
any explicit project requirement such as mandatory use. In a new document, keep its mandate and priority
in that entry rather than repeating them in an engineering section. Do not copy the skill's general
conventions, loading procedures, setup, or reference-file paths into the project file. Add only verified
project-specific requirements, explicit overrides, or cross-module coordination rules. For an
adopted role, keep only its responsibility and handoff; leave its internal tool selection, reference
loading, and execution procedure in the role definition. In a new document, put each chosen role in
its own item or row under collaboration, not in one long capability entry.
These creation defaults do not authorize shortening valid existing rules during an unrelated rebuild.

Give independently useful rules their own list items or paragraphs. Use headings where the subject
changes; do not pack unrelated requirements into semicolon chains or oversized bullets. Remove duplicate
wording in place, but keep useful existing sections unless the author explicitly wants them combined.
Update related outdated statements in their locations rather than appending corrective sections.

Keep investigation notes, current installation/index state, missing-file observations, setup steps,
and checks performed out of `AGENTS.md`. Verification command inventories belong in the conversation
unless the author explicitly wants a lasting command reference. A concise verification policy may be
useful in the document. Routine absence needs no warning when work can continue; report only a material
blocker or a decision the author needs to make. Do not copy manuals, research links, or the full catalog.

Honor the selected scope of a plugin across the whole file. If the author limits it to certain skills,
replace conflicting broader assignments elsewhere rather than adding an exception at the end. Keep
model and permission restrictions only when they are the target project's actual choices.
Preserve whether each plugin is optional, conditional, or required; a feature allowlist must not
introduce a mandate or exclude other selected capabilities.

Preserve the existing scope and precedence of project instructions. In new wording, distinguish priority
among coding conventions from authority over the user's task: selecting a skill does not put it above
explicit user instructions. Do not invent a rule making the target file override untouched parent or
child files to resolve a perceived tooling conflict. Distinguish a real conflict from complementary
tools or a limited plugin selection. If a genuine conflict requires an author decision, present the
specific conflicting rules and choices to the author, through the primary agent when delegated,
before deciding; editing one file does not authorize changing the meaning of another file.

## Check and Deliver

Read the complete result against the scope, selection, entry-format, and content rules above. For
every adopted capability or new mandate, identify its project source or explicit author choice.
Check that the entry preserves its adopted scenarios, decision conditions, overlaps, and fallback;
remove duplicate explanations and invocation mechanics, not scenario coverage. Verify role availability
before claiming registration and preserve the selected plugin feature scope. For a capability rebuild,
account for every category in the requested area and every confirmed choice.
For combined scenarios, check that each selected capability retains its own entry, a distinct
responsibility, and a usable fallback; keep unselected alternatives in the recommendation conversation.

Compare meaning, headings, and rule separation against the agreed baseline. Check that useful detail
and distinct responsibilities survived. Account for every changed or deleted heading and rule against
the requested refresh. Preserve who a constraint covers, its conditions, scope, priority, and authorization level;
do not substitute a more familiar generic rule for a project-specific one. Within the requested scope,
remove duplicate rules, stale snapshots, and contradictory routes. Do not target a reduction percentage
or use fewer characters, lines, or headings as proof of improvement. Repeated runs should not accumulate
content. Verify that only the requested target changed, excluding pre-existing user modifications.
Document authoring needs no new unit tests or application build.

Deliver the file, meaningful changes, selection rationale, and checks actually performed. Mention setup
needs only when relevant to an adopted choice, with a pointer to the
[setup flows](capability-setup.md); run the setup only after the author confirms the delivered file
and asks for it. Keep operational observations in this message when they
matter; do not report every absent optional capability. Later coding tasks follow the resulting project
instructions without rerunning this authoring workflow.

## Bundled Collaboration Roles

Prefer existing user roles. When collaboration choices are open, offer only the responsibilities the
project needs from the following definitions; read definitions on demand. Their presence in this skill
does not install or register agents, and selecting them does not mean running all roles on every task.
Apply a no-delegation rule only to roles whose definitions or project instructions impose it; primary-agent
acceptance does not make every other role a leaf agent.

| Role | Responsibility |
| --- | --- |
| [scrapider-explorer](../../../../agents/scrapider-explorer.md) | Read-only investigation and concise evidence. |
| [scrapider-developer](../../../../agents/scrapider-developer.md) | Implementation and verification. |
| [scrapider-code-reviewer](../../../../agents/scrapider-code-reviewer.md) | Read-only functional correctness and regression review. |
| [scrapider-standards-reviewer](../../../../agents/scrapider-standards-reviewer.md) | Read-only engineering standards review. |
| [scrapider-generalist](../../../../agents/scrapider-generalist.md) | Work outside the specialist roles. |

The primary agent checks delegated results and owns integration and acceptance. Keep small tasks direct,
parallelize useful independent work, and serialize dependencies or shared edits. Handoffs carry goals,
scope, constraints, and evidence rather than the entire conversation. Use the existing
[standards-review guidance](../shared/standards-review.md) for its applicability and procedure; do not
invent a second review workflow or require review on every step.
