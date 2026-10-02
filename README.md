# Scrapider Guidelines

[简体中文](README_zh-CN.md) | English

Engineering guidelines for coding agents working in Scrapider projects. This reusable Codex Skill helps agents understand requests, follow project architecture, preserve business contracts within the requested scope, and deliver verified changes with appropriate complexity.

## What it provides

- A coding workflow for understanding the problem, implementing or debugging, verifying behavior, and reporting evidence.
- Shared rules for scope, business contracts, reuse, responsibility boundaries, and refactoring.
- Validation and exception rules based on actual callers, existing guarantees, and required business behavior.
- Scope-specific guidance for Spring Boot, Python, Android Kotlin, and Jetpack Compose work.
- Conditional standards review and separate Git operation guidance.

## Supported scopes

| Scope | Guidance |
| --- | --- |
| Spring Boot backend | Package responsibilities, layered architecture, and object placement. |
| Multi-module Maven projects | Module ownership, dependencies, and Spring configuration placement. |
| Python | Package and module organization. |
| Android Kotlin & Jetpack Compose | UI state, data mapping, error handling, and Compose architecture. |
| Android UI implementation | Additional guidance for design-driven UI and product UI work. |
| Standards review | A complete, read-only conformance-review workflow. |
| Local full-stack deployment | Docker Compose, Jenkins host builds, runtime images, and optional Nginx routing when that profile is in scope. |

## Use with Codex

Install this repository as a Codex Skill using your preferred skill-installation workflow, then invoke it in a task prompt:

```text
Use $scrapider-guidelines to implement this Spring Boot change.
```

The skill can also be selected automatically when an agent is asked to write, edit, refactor, debug, or review code under Scrapider engineering rules.

Examples:

```text
Use $scrapider-guidelines to review this Python diff.

Use $scrapider-guidelines to refactor this Jetpack Compose screen without changing its public behavior.

Use $scrapider-guidelines to implement this Spring Boot endpoint and preserve the existing API contract.
```

## How reference routing works

The main [SKILL.md](SKILL.md) defines the working principles, coding workflow, and loading rules. Detailed guidance is grouped by the decisions it governs:

| Category | Source |
| --- | --- |
| Change scope, business contracts, reuse, responsibilities, and refactoring | [Code Design and Changes](references/shared/code-design-and-changes.md), loaded for all code and build/runtime configuration work. |
| Input guarantees, validation ownership, and exception handling | [Validation and Boundaries](references/shared/validation-and-boundaries.md), loaded when those decisions are in scope. |
| Review applicability, dispatch, read-only procedure, and report | [Standards Review](references/shared/standards-review.md), loaded for explicit reviews or changes that may cross its review boundaries. |
| Git authorization and commit message format | [Git Operations and Commits](references/shared/git-commits.md), loaded before any Git state mutation. |
| Technology-specific responsibilities | The applicable Java, Python, Android, or local deployment references. |

For example, a Spring Boot task loads the general code-design rules and backend guidance; a multi-module Maven task also loads module-placement guidance. Reference examples do not require adding layers the project does not need.

For a standards-conformance review, follow [`references/shared/standards-review.md`](references/shared/standards-review.md), which defines when to delegate to the read-only `scrapider-standards-reviewer` agent or review directly.

## Working principles and origin

The [working principles](SKILL.md#working-principles) retain the emphasis on reasoning before coding, simplicity, focused changes, and verifiable outcomes. Their framing follows the community-maintained [Karpathy-inspired coding guidelines](https://github.com/multica-ai/andrej-karpathy-skills), which derive from Andrej Karpathy's observations about coding agents.

The [coding workflow](SKILL.md#coding-workflow) applies these principles alongside the project's architecture, business contracts, validation boundaries, verification, and review requirements. Load detailed guidance for the actual task and maintain each rule in its relevant category.

## Repository layout

```text
.
├── SKILL.md
├── agents/
│   ├── openai.yaml
│   └── scrapider-standards-reviewer.md
└── references/
    ├── android/
    ├── java/
    ├── python/
    └── shared/
```

## Contributing

Keep contributions narrowly scoped, consistent with the existing guidance, and clear about the problem they solve. When adding or changing a rule, update the relevant routed reference rather than duplicating the same rule across unrelated documents.

## License

Distributed under the [MIT License](LICENSE).
