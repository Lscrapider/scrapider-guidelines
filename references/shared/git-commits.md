# Git Operations and Commits

## Authorization

Do not create commits, push, create branches, stage files, or otherwise mutate Git state unless
the user explicitly asks for that Git action. Read-only Git inspection does not require separate
authorization. Follow project instructions and the scope of the request; permission to commit does
not authorize a push or branch creation. Loading this reference grants no permission.

## Commit Message Format

Use this format only after the user explicitly authorizes creating a commit.

Use one bracketed change key, a colon, and numbered feature points:

```text
[key] : 1. feature point
        2. feature point
```

Choose the key from the actual staged change set:

- `[add]`: Added files or functionality only, with no deletions or modifications to existing behavior.
- `[del]`: Deletions only, with no additions or modifications.
- `[upd]`: Any modification, or additions/deletions mixed with other changes.

Keep the list concise:

- Include at most 10 numbered items.
- Merge related changes instead of listing file-by-file edits.
- Describe user-visible or maintenance-relevant changes, not implementation trivia.

Example:

```text
[upd] : 1. classify shared engineering rules
        2. remove obsolete compatibility guidance
```
