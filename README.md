# Scrapider Guidelines

[简体中文](README_zh-CN.md) | English

Engineering guidelines for coding agents working in Scrapider projects. This repository is a reusable Codex Skill that promotes small, contract-safe, and verifiable changes across supported technology stacks.

## What it provides

- A practical engineering discipline: keep changes minimal, reuse established capabilities, and avoid accidental abstraction.
- Scope-specific guidance for Spring Boot, Python, Android Kotlin, and Jetpack Compose work.
- Clear rules for validation ownership, exception semantics, business-contract preservation, and verification.
- An independent, read-only standards-review agent for checking changes against these guidelines.

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

The main [SKILL.md](SKILL.md) loads only the references relevant to the task. For example, a Spring Boot backend task loads the backend guidance; a multi-module Maven task additionally loads the module guidance. This keeps each task focused while applying the rules that matter to its actual scope.

For a standards-conformance review, use the `scrapider-standards-reviewer` agent. It is intentionally read-only and follows the review procedure in [`references/shared/standards-review.md`](references/shared/standards-review.md).

## Core principles

1. **Simplicity first** — implement the smallest coherent change that solves the request.
2. **Reuse before rebuild** — look for an equivalent project, framework, or standard-library capability before adding one.
3. **No accidental layers** — introduce a layer only when it owns a real domain or technical boundary.
4. **Surgical changes** — avoid unrelated refactors and formatting changes.
5. **Preserve business contracts** — treat defaults, thresholds, enums, and strategy parameters as established behavior.
6. **Verify proportionally** — use the narrowest useful check, and report the evidence.

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
