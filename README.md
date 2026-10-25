# Scrapider Guidelines

[简体中文](README_zh-CN.md) | English

Engineering guidelines for coding agents working in Scrapider projects. This reusable Agent Skill helps agents understand requests, follow project architecture, preserve business contracts within the requested scope, and deliver verified changes with appropriate complexity.

## What it provides

- A coding workflow for understanding the problem, implementing or debugging, verifying behavior, and reporting evidence.
- Shared rules for scope, business contracts, reuse, responsibility boundaries, and refactoring.
- Validation and exception rules based on actual callers, existing guarantees, and required business behavior.
- Scope-specific guidance for Spring Boot, Python, Android Kotlin, and Jetpack Compose work.
- Conditional standards review and separate Git operation guidance.
- On-demand authoring of the target `AGENTS.md`, with optional MCP, skill, plugin, and collaboration recommendations.

## Supported scopes

| Scope | Guidance |
| --- | --- |
| Spring Boot backend | Package responsibilities, layered architecture, and object placement. |
| Multi-module Maven projects | Module ownership, dependencies, and Spring configuration placement. |
| Python | Package and module organization. |
| Android Kotlin & Jetpack Compose | UI state, data mapping, error handling, and Compose architecture. |
| Android UI implementation | Additional guidance for design-driven UI and product UI work. |
| Standards review | A complete, read-only conformance-review workflow, with an optional JetBrains inspection-script channel for mechanical checks. |
| Project AGENTS.md | Help authors select clear, structured project instructions and refresh existing sections with relevant capability combinations and collaboration guidance. |
| Local full-stack deployment | Docker Compose, Jenkins host builds, runtime images, and optional Nginx routing when that profile is in scope. |

## Usage

Use your coding agent's skill installation and invocation mechanisms. The instructions and references target coding agents generally; discovery, invocation, and available capabilities depend on the host. These examples use Codex invocation syntax:

```text
Use $scrapider-guidelines to implement this Spring Boot change.
```

The skill can also be selected automatically when an agent is asked to write, edit, refactor, debug, or review code under Scrapider engineering rules.

Examples:

```text
Use $scrapider-guidelines to review this Python diff.

Use $scrapider-guidelines to refactor this Jetpack Compose screen without changing its public behavior.

Use $scrapider-guidelines to implement this Spring Boot endpoint and preserve the existing API contract.

Use $scrapider-guidelines to create, update, or rebuild this project's AGENTS.md with suitable workflows and optional tool combinations.
```

AGENTS.md authoring writes only the requested instruction file. It preserves project conventions and does not install tools or create configuration or separate agent-definition files.

## How reference routing works

The main [SKILL.md](SKILL.md) defines the working principles, coding workflow, and loading rules. Detailed guidance is grouped by the decisions it governs:

| Category | Source |
| --- | --- |
| Change scope, business contracts, reuse, responsibilities, and refactoring | [Code Design and Changes](references/shared/code-design-and-changes.md), loaded for all code and build/runtime configuration work. |
| Input guarantees, validation ownership, and exception handling | [Validation and Boundaries](references/shared/validation-and-boundaries.md), loaded when those decisions are in scope. |
| Review applicability, dispatch, read-only procedure, and report | [Standards Review](references/shared/standards-review.md), loaded for explicit reviews or changes that may cross its review boundaries. |
| Git authorization and commit message format | [Git Operations and Commits](references/shared/git-commits.md), loaded before any Git state mutation. |
| Inspection script library | The assigned standards reviewer loads this when the JetBrains inspection channel is available; also load for explicit library maintenance, adaptation, or installation. | [Inspection Scripts](references/shared/inspection-scripts.md) |
| Project instructions and optional capability combinations | [AGENTS.md Authoring](references/project/agents-md-authoring.md), loaded only for authoring requests; [Capability Recommendations](references/project/capability-recommendations.md) when selecting workflows; [Capability Setup](references/project/capability-setup.md) when installing or initializing the selected tools. |
| Technology-specific responsibilities | The applicable Java, Python, Android, or local deployment references. |

For example, a Spring Boot task loads the general code-design rules and backend baseline, then follows
its routes for architecture, HTTP/domain objects, data access, and messaging only where applicable.
A multi-module Maven task also loads module-placement guidance. Python tasks load the Python baseline;
package-layout rules and examples are added when creating, moving, or reorganizing code. Mixed tasks
load every applicable route, including behavior affected through callers, and revisit routing when
the scope expands. Implementation and review use the same routes. Reference examples do not require
adding layers the project does not need.

The shared code-design rules distinguish the reported location from the cause's actual scope:
inspect related paths when evidence connects them, repair shared causes within the authorized scope,
and keep scenario-specific behavior local. Determine that scope before introducing an abstraction.

For a standards-conformance review, follow [`references/shared/standards-review.md`](references/shared/standards-review.md), which defines when to delegate to the read-only `scrapider-standards-reviewer` agent or review directly.

## Working principles and origin

The [working principles](SKILL.md#working-principles) retain the emphasis on reasoning before coding, simplicity, focused changes, and verifiable outcomes. Their framing follows the community-maintained [Karpathy-inspired coding guidelines](https://github.com/multica-ai/andrej-karpathy-skills), which derive from Andrej Karpathy's observations about coding agents.

The [coding workflow](SKILL.md#coding-workflow) applies these principles alongside the project's architecture, business contracts, validation boundaries, verification, and review requirements. Load detailed guidance for the actual task and maintain each rule in its relevant category.

## Repository layout

```text
.
├── .research/
├── SKILL.md
├── agents/
│   ├── openai.yaml
│   ├── scrapider-code-reviewer.md
│   ├── scrapider-developer.md
│   ├── scrapider-explorer.md
│   ├── scrapider-generalist.md
│   └── scrapider-standards-reviewer.md
├── references/
│   ├── android/
│   ├── java/
│   ├── project/
│   ├── python/
│   └── shared/
└── scripts/
    └── inspections/
```

## Research and evaluation

The [code-retrieval Token experiment](.research/2026-10-18-retrieval-token-benchmark/REPORT.md)
records the tested scenarios, measured usage, quality review, limitations, and resulting workflow
choices. Its [per-run metrics](.research/2026-10-18-retrieval-token-benchmark/metrics.csv) are
available for inspection. The [12-run Serena follow-up](.research/2026-10-18-serena-incremental-benefit/REPORT.md)
records the repaired response rendering, fixed scoring, repeated comparisons, and limits on attributing
benefits to an optional tool that workers did not invoke. Later archives record the evaluations behind
recent features: the [inspection-script rule library](.research/2026-10-24-inspection-kts-rule-library/REPORT.md)
behind the mechanical-check channel, the [JetBrains MCP and Context7 capability pilot](.research/2026-10-24-jetbrains-context7-capability-pilot/REPORT.md)
behind the external library docs service and IDE tool surface recommendations, and the
[capability scenario and authoring runs](.research/2026-10-25-capability-scenarios-and-authoring/REPORT.md)
behind the capability entry examples.
The `.research/` directory is for developers browsing this repository;
omit it when installing the skill. It is not referenced by the skill's loading routes.

## Contributing

Keep contributions narrowly scoped, consistent with the existing guidance, and clear about the problem they solve. When adding or changing a rule, update the relevant routed reference rather than duplicating the same rule across unrelated documents.

## License

Distributed under the [MIT License](LICENSE).
