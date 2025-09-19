# ticket-commit-guard

![Build](https://github.com/Jonajor/ticket-commit-guard/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)

# Ticket Commit Guard

Automatically prefixes your commit message with the **issue key** found in the current **Git branch** (e.g., `ABC-123: your message`).  
Keep your commits consistent with your tracker (Jira, etc.)—with zero extra clicks.

---

## Features

- Detects the **first** issue key in the branch name via regex: `[A-Z]+-[0-9]+` (e.g., `ABC-123`)
- Prefixes the commit message **only if missing** (no duplicates)
- Trims leading spaces before applying the prefix
- No-op on non-Git projects or branches without a key

---

## How it works

On **Commit**, the plugin:

1. Reads the current Git branch (`git4idea`).
2. Extracts the first token matching `[A-Z]+-[0-9]+`.
3. If your message doesn’t start with `<KEY>:`, it becomes `"<KEY>: <your message>"`.

If no repo or key is found, nothing is changed.

---

## Examples

| Branch name                         | Commit message entered | Final commit message            |
|------------------------------------|------------------------|---------------------------------|
| `feature/ABC-123-fix-video`        | `fix player`           | `ABC-123: fix player`           |
| `bugfix/ABC-7-npe`                | `   handle null`       | `ABC-7: handle null`           |
| `release/v1.2.3`                   | `bump version`         | `bump version` (unchanged)      |
| `main` / `master`                  | `docs`                 | `docs` (unchanged)              |
| `ABC-123`                          | `initial`              | `ABC-123: initial`              |

---

## Requirements

- IntelliJ IDEA **Community or Ultimate 2024.3.x** (`243.*`)
- Git integration enabled (bundled with IntelliJ)

---

## Install (from source)

```bash
# clone
git clone https://github.com/Jonajor/ticket-commit-guard.git
cd ticket-commit-guard

# run in sandbox IDE
./gradlew clean runIde
```
---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
