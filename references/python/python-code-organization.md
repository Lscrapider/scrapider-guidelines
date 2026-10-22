# Python Code Organization Reference

Use this reference for Python code. Python projects may be agents, RAG systems, training pipelines, inference jobs, data processing jobs, CLIs, workers, experiments, or web services. Do not assume an HTTP backend structure unless the repository clearly uses one.

## Package Layout (On Demand)

Read [Python Package Layout](python-package-layout.md) when creating, moving, splitting, or merging
files or packages; choosing business or technical directory responsibilities; changing module
boundaries; or reviewing those changes. A change confined to existing functions with no effect on
organizational boundaries does not require the package-layout reference. If implementation requires
a new file or a boundary change, read that reference before proceeding even when reorganization
was not mentioned in the request.

## Contents

- [Core Rules](#core-rules)
- [Classes, Inheritance, and Wrappers](#classes-inheritance-and-wrappers)
- [Package Layout (On Demand)](#package-layout-on-demand)
- [Naming Rules](#naming-rules)
- [Import Rules](#import-rules)
- [Tests](#tests)

## Core Rules

- Inspect the existing Python project structure before adding files.
- Do not place many unrelated `.py` files in the same directory.
- Group code by business domain, capability, lifecycle stage, runtime role, or domain responsibility.
- Prefer cohesive packages with clear ownership over large flat directories. Do not split a cohesive area merely to keep files or packages small.
- Do not create Java/Spring-style layers such as `controller`, `service`, `manage`, or `mapper` unless the project already uses that pattern.
- Do not create packages for their own sake; keep simple scripts simple.
- If a one-off script grows shared logic, move reusable logic into packages and keep the script as a thin entry point.

## Classes, Inheritance, and Wrappers

- Prefer plain functions and direct calls. Introduce a class only when it owns state, implements a protocol, or groups several methods sharing that state; do not create `XManager`, `XService`, `XHelper`, or `XWrapper` classes around logic that works as functions.
- Do not write forwarding wrappers — a class or function whose body only calls another existing class or function. Call the target directly or pass it as a callable. A package may expose its current API through a re-export, but must not retain old names or import paths as compatibility aliases.
- When a new type genuinely specializes an existing class with a stable, project-controlled base, inherit explicitly instead of wrapping and forwarding.
- Prefer composition over inheritance when the base is a third-party or fast-moving class, such as an SDK or model client; do not inherit from those unless the project already does.
- Allow a wrapper only when it owns a real responsibility, following [Code Design and Changes](../shared/code-design-and-changes.md). External SDK or protocol conversion and internal transaction or authorization boundaries can qualify; merely forwarding internal calls does not.
- Do not create an abstract base class or Protocol for a single concrete implementation unless the project already uses that pattern.
- Reuse Pydantic models, dataclasses, or plain dicts for data; do not wrap a single payload in a hand-written class of getters and setters.

## Naming Rules

- Use `snake_case.py` for module names.
- Use clear suffixes when they communicate role, such as `_agent.py`, `_tool.py`, `_client.py`, `_dataset.py`, `_trainer.py`, `_handler.py`, or `_provider.py`.
- Avoid vague names like `common.py`, `helper.py`, `misc.py`, or `utils.py` for business, agent, training, or pipeline logic.
- Prefer business-domain names when the package represents a business area.

## Import Rules

- Prefer absolute imports from the project package when that is the existing style.
- Keep package `__init__.py` minimal. Do not hide large side effects there.
- Fix import problems at the package, installation, or entry-point configuration that owns them. Do not add `sys.path` mutations to keep obsolete module locations or broken package structure working.
- Avoid circular imports; move shared contracts, constants, and small shared types into a lower-level shared module only when needed.

## Tests

- Mirror the source package structure in tests when practical.
- Put shared test fixtures in the existing fixture or `conftest.py` pattern.
- Do not place tests for unrelated modules in one large test file.
