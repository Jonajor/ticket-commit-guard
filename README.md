# Ticket Commit Guard

<!-- Plugin description -->
**Ticket Commit Guard** keeps your commit messages compliant by automatically prefixing them with the issue key parsed from the current Git branch name.

## Features

- Extracts the first issue key in the branch name (e.g. `ABC-123`)
- Prefixes the commit message only when missing (no duplicates)
- Preserves your text and trims leading spaces
- Automatically clears commit message after successful pushes

## Supported Branch Patterns

- Jira-style keys: `ABC-123` anywhere in the branch (e.g., `feature/ABC-123-title`)
- Branches without a key are left untouched

## Examples

| Branch Name | Resulting Commit Prefix | Example Final Message |
|-------------|------------------------|----------------------|
| `feature/ABC-123-fix-video` | `ABC-123:` | `ABC-123: fix video page` |
| `bugfix/ABC-123` | `ABC-123:` | `ABC-123: handle null IDs` |
| `main` or `master` | *no action* | `update docs` |

**Tip:** Use branch names that include an issue key (e.g., `ABC-123`). If the current branch has no key, the plugin won't change the message.
<!-- Plugin description end -->

## Installation

1. Download the plugin from the [releases page](../../releases)
2. In IntelliJ IDEA: **File → Settings → Plugins → Install Plugin from Disk**
3. Select the downloaded `.zip` file
4. Restart IntelliJ IDEA

## Development

```bash
# Run in development mode
./gradlew runIde

# Build plugin
./gradlew buildPlugin
```

## License

MIT License - see [LICENSE](LICENSE) file for details.