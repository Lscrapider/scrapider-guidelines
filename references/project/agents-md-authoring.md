# Project AGENTS.md Authoring

Help the author choose and organize useful project instructions when asked to create, update, or
rebuild `AGENTS.md`. Derive rules from the project and the author's choices; this workflow does not
supply a new engineering policy. Remove unnecessary repetition while keeping rules complete and easy
to scan. Brevity serves comprehension; it is not a compression target.

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

Do not install capabilities, configure hosts, build indexes, or create agent definitions, plan files,
or companion project documents during authoring. These limits, and restrictions imposed only on the
authoring run, concern this operation; do not turn them into permanent project policy. An
explanation-only request does not authorize a file edit.
Using this authoring mode does not select Scrapider's coding rules or bundled agents for future work.
Offer them as options when relevant; adopt them only from existing project rules or the author's choice.

## Establish the Purpose

For creation, identify the project responsibilities and decisions that future agents need. For an
update, address the named changes in their existing sections. For a rebuild, establish what the author
wants to refresh: capability choices, workflows, collaboration, project conventions, or overall brevity.

Use the conversation and existing rules first. If the rebuild focus is still unclear, offer a short
choice of relevant directions with a recommendation; do not make the author invent a specification.
Do not repeat a question already answered or refresh the entire tool catalog for every rebuild.

A request to rebuild the skill/tool area asks for a capability refresh, not merely wording cleanup.
Determine its scope from the document's meaning: an area may contain skills, MCP services, plugins,
native tools, and their combined workflows even when the author calls it the "skill" area. Reassess
the categories present there for current behavior, missing handoffs, overlap, cost, and useful
combinations. Do not narrow the refresh to the first obvious problem or to literal Skill packages.
An explicitly named single-tool edit or wording-only request remains narrower.

Existing entries are the baseline to evaluate, not a reason to skip recommendations. Use the same
relevant recipe guidance as initialization: offer what to retain, augment, replace, or retire and why,
including the current setup when it remains suitable. A section-level refresh does not authorize
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

Load [Capability Recommendations](capability-recommendations.md) for relevant categories. The author
may not know which tools or skills exist: proactively offer two or three useful, named workflow
combinations based on the project's tasks, rather than asking them to supply tool names. Explain the
scenario, division of work, handoff, and meaningful setup or usage cost; recommend one and say why it
fits. Include a simple existing-tools option when it meets the need. Let the author select a proposal
or provide an alternative. Related profiles may share a baseline, but should differ meaningfully.
Keep complementary retrieval, coding, planning, and UI roles clear: they can form a workflow together,
not compete as if they served the same purpose. Separate independent optional additions when useful;
do not make the author choose between a coding standard and a retrieval service just to select both.

Reuse an explicit selection without asking again. For an unresolved choice that materially changes
the project's workflow or dependencies, wait for the answer before writing it as an adopted rule;
continue independent inspection and organization meanwhile. A recommendation is not a selection,
and selecting a capability does not authorize its installation.
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

Describe cooperating MCPs, skills, plugins, and native tools as one grouped capability entry. State the
trigger, each tool's role, when enough evidence has been obtained, and a useful fallback. Place the
combination alongside other entries in the existing capability list or table, without giving it a
separate section or subsection. In a list, keep the parent item to the combination's name and purpose;
put distinct tool responsibilities and shared handoff or fallback rules in short nested items. In a
table, retain one capability row with readable line breaks inside it. Keep the combination grouped
without flattening its distinct responsibilities into a long paragraph. Availability checks inform
selection; an inventory of installed tools or existing indexes is not a project rule.
Apply the same readable grouping when creating a new capability area; if using prose, give distinct
responsibilities separate paragraphs rather than putting the whole combination into one paragraph.
In a new document, organize selected skills, plugins, and MCP/native-tool combinations together in
one capability area under a heading suited to its content. Make each capability or combination a peer
entry there. Do not put a coding skill only under engineering conventions while giving its companion
retrieval tools a separate top-level section. Detailed conventions and scoped workflows may refer back
to these responsibilities without duplicating them.

## Select Content and Integrate It

Check that the following meanings are covered where applicable, regardless of the author's headings,
wording, or order. These are content expectations, not prescribed section names, a fixed template, or
a limit on what the project may include. Equivalent existing descriptions or references already satisfy
the relevant expectation: retain them instead of renaming, duplicating, or replacing them. Semantic
matching recognizes existing coverage; it does not authorize renaming a matching section.

Within the requested scope, fill a genuine content gap in the appropriate existing location; introduce
a heading only when the content needs one. Keep other useful project-specific sections. For a new
document, choose headings that fit its content. For an existing document, retain its useful separation of responsibilities;
do not collapse established sections merely to reduce their count. Do not invent business policies or
unneeded capabilities to fill a category; clarify a material missing decision when needed.

| Dimension | Useful lasting content |
| --- | --- |
| Project structure | Brief labels for the top-level modules and their responsibilities, plus one general requirement to read the relevant module README and applicable instructions before working there. |
| Capabilities and workflows | Selected Skill, MCP, plugin, or native-tool combinations and the tasks they serve. |
| Collaboration | When delegation helps, role responsibilities, dependency handling, handoffs, and primary-agent acceptance. |
| Business and development conventions | Existing contracts and project-specific code constraints that materially affect decisions; reference established guidance instead of copying it. |
| Execution and delivery | Actual authorization limits, proportionate verification, and the evidence expected in delivery. |
| Language | The author's chosen language for documents, comments, commits, or responses, where specified. |

For creation, assess collaboration explicitly rather than silently omitting it because no custom agents
were named. When useful delegation is possible and the choice is open, offer suitable existing or
bundled roles and a direct-work option. Record the chosen responsibilities and acceptance boundary;
the author may choose direct work without adopting named agents. Do not register agents during authoring.
When selected roles are not exposed by the host, retain the responsibilities and have the primary agent
perform them or map them to available agents; do not leave work blocked on a role name.

Keep a statement only if it changes how future work should be done. Use clear, action-oriented rules
and brief module labels. Put capability roles in their section, business rules with business
rules, and multi-agent collaboration in its own established section. Keep development conventions,
execution requirements, and language rules distinct when the source already separates them.
Route document reading by the task's affected areas; do not require every task to read all project
overviews or product documents unless that is an existing project rule or an explicit author choice.
Use the root README for orientation when needed, not as an unconditional reread before every task.

When initializing a root document, briefly identify the top-level modules and their responsibilities.
State the module-reading requirement once, in one short sentence: before working in a module, read its
README and applicable `AGENTS.md` instructions. Make it apply to any affected module rather than
enumerating selected module names again. Let each module README provide further reading guidance;
do not expand the root rule into a reading catalog, a module-to-README table, repeated paths, or an
explanation of which kinds of documents to read next. Do not invent missing README files or write them
during this operation.

Keep documentation ownership clear. Detailed module descriptions, internal structure, subdirectory
contents, historical or prototype material, and architecture or protocol descriptions belong in the corresponding
README or module documentation. Do not copy them into root `AGENTS.md` merely because they are useful
or important. The root file owns agent-facing instructions: selected Skill/MCP/plugin duties and
workflows, collaboration, author constraints, reading requirements, and verification or delivery rules.
Do not move those agent instructions into README files to make `AGENTS.md` shorter.

Retain genuine project-wide work constraints, such as checking both sides of a cross-module change,
without summarizing the underlying architecture or message format. Module-specific procedures,
document-maintenance rules, and deeper reading entry points stay in the module README, even when it
states them as requirements. The general module-reading rule already makes them available; do not
repeat those paths or navigation elsewhere in the root file. Existing README and Skill content remains
authoritative through its reading route. Only the requested `AGENTS.md` is written; do not create or edit
other documents just to relocate content.

For an adopted skill, normally use one short entry stating when it applies, what it contributes, and
any explicit project requirement such as mandatory use. In a new document, keep its mandate and priority
in that entry rather than repeating them in an engineering section. Referencing an adopted engineering
skill also covers the general conventions it owns; do not paraphrase its coding principles into a new
section just to fill a content dimension. Add verified project-specific requirements, explicit overrides,
or necessary cross-module coordination rules instead. Its own instructions govern how to load
`SKILL.md`, resolve source directories, progressively read references, run internal workflows, or
handle setup. Do not restate those mechanics or reference-file paths in the project file. For an
adopted role, keep only its responsibility and handoff, leaving its execution procedure in the role
definition. In a new document, put each chosen role in its own item or row under collaboration, not
in one long capability entry. Tool combinations may need short subitems for their actual division of work.
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
A plugin feature allowlist does not make that plugin mandatory for every matching task or prohibit
other project capabilities. Preserve whether its use is optional, conditional, or required.

Preserve the existing scope and precedence of project instructions. In new wording, distinguish priority
among coding conventions from authority over the user's task: selecting a skill does not put it above
explicit user instructions. Do not invent a rule making the target file override untouched parent or
child files to resolve a perceived tooling conflict. Distinguish a real conflict from complementary
tools or a limited plugin selection. If a genuine conflict requires an author decision, present the
specific conflicting rules and choices to the author, through the primary agent when delegated,
before deciding; editing one file does not authorize changing the meaning of another file.

## Check and Deliver

Read the complete result for consistency, relevance, and readable structure. Confirm that selected combinations
are actionable, original valid constraints survive, and no unselected dependencies or new policies
were introduced. A named role does not prove registration; a plugin name does not select every feature.
For each new mandatory reading, approval, or capability rule, identify its project source or explicit
author choice. Remove rules inferred only from this authoring run's permissions or temporary limits.
Map these content expectations to the actual descriptions in the document. Check practical gaps in
meaning, not whether headings match this reference; do not manufacture missing sections to fill a table.
For a capability rebuild, check that every relevant category in the requested area was assessed and
every confirmed in-scope choice reached the result. Preserving headings and correcting one plugin's
wording is not enough when an accepted retrieval combination or other workflow update was omitted.
Explain meaningful retained or changed choices in delivery; keep the assessment out of `AGENTS.md`.
For initialization, compare the draft with its source documents and selected skills. Keep only a brief
top-level module description and one general module-reading rule. Remove repeated reading routes
regardless of formatting, descriptions of nested directories, historical artifacts,
module internals, protocol or operational details, and internal Skill instructions; their source
documents own that content. Check that reading requirements and agent-specific duties remain explicit.
Keep headings and independent responsibilities readable; do not achieve brevity by packing rules together.

Compare meaning, headings, and rule separation against the agreed baseline. Check that useful detail
and distinct responsibilities survived. Account for every changed or deleted heading and rule against
the requested refresh. Preserve who a constraint covers, its conditions, scope, priority, and authorization level;
do not substitute a more familiar generic rule for a project-specific one. Within the requested scope,
remove duplicate rules, stale snapshots, and contradictory routes. Do not target a reduction percentage
or use fewer characters, lines, or headings as proof of improvement. Repeated runs should not accumulate
content. Verify that only the requested target changed, excluding pre-existing user modifications.
Document authoring needs no new unit tests or application build.

Deliver the file, meaningful changes, selection rationale, and checks actually performed. Mention setup
needs only when relevant to an adopted choice. Keep operational observations in this message when they
matter; do not report every absent optional capability. Later coding tasks follow the resulting project
instructions without rerunning this authoring workflow.

## Bundled Collaboration Roles

Prefer existing user roles. When collaboration choices are open, offer only the responsibilities the
project needs from the following definitions; read definitions on demand. Their presence in this skill
does not install or register agents, and selecting them does not mean running all roles on every task.

| Role | Responsibility |
| --- | --- |
| [scrapider-explorer](../../agents/scrapider-explorer.md) | Read-only investigation and concise evidence. |
| [scrapider-developer](../../agents/scrapider-developer.md) | Implementation and verification. |
| [scrapider-code-reviewer](../../agents/scrapider-code-reviewer.md) | Read-only functional correctness and regression review. |
| [scrapider-standards-reviewer](../../agents/scrapider-standards-reviewer.md) | Read-only engineering standards review. |
| [scrapider-generalist](../../agents/scrapider-generalist.md) | Work outside the specialist roles. |

The primary agent checks delegated results and owns integration and acceptance. Keep small tasks direct,
parallelize useful independent work, and serialize dependencies or shared edits. Handoffs carry goals,
scope, constraints, and evidence rather than the entire conversation. Use the existing
[standards-review guidance](../shared/standards-review.md) for its applicability and procedure; do not
invent a second review workflow or require review on every step.
