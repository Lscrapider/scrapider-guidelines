---
description: Standards review of current changes under Scrapider engineering rules
argument-hint: "[scope or paths] defaults to the current working-tree changes"
skills: scrapider-guidelines
---

Review the requested changes against Scrapider engineering standards. Do not modify code during the review.

Load the `scrapider-guidelines` skill if it is not already loaded, then load its
`references/shared/standards-review.md` and follow that reference exactly: apply its applicability
rules to determine the review scope, follow its dispatch rules (delegate to the
`scrapider-standards-reviewer` agent where they require it), use its read-only procedure, and produce
the report in the format it defines. Distinguish verified findings from unverified observations.

Review target: $ARGUMENTS (empty means the current working-tree changes)
