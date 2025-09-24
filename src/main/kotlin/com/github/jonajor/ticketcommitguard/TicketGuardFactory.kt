package com.github.jonajor.ticketcommitguard

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.changes.CommitContext
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory
import git4idea.repo.GitRepositoryManager

private val LOG = Logger.getInstance("Ticket Commit Guard")

class TicketGuardFactory : CheckinHandlerFactory() {
    override fun createHandler(panel: CheckinProjectPanel, commitContext: CommitContext): CheckinHandler {
        return object : CheckinHandler() {

            // Matches ABC-123 anywhere in the branch name
            private val ticketRegex = Regex("[A-Z]+-[0-9]+")

            override fun beforeCheckin(): ReturnResult {
                // Remember the panel so we can clear it after PUSH (not after commit-only)
                CommitPanelHolder.setPanel(panel.project, panel)

                val project = panel.project
                val repo = GitRepositoryManager.getInstance(project).repositories.firstOrNull()
                    ?: return ReturnResult.COMMIT

                val branchName = repo.currentBranchName ?: return ReturnResult.COMMIT
                val ticket = ticketRegex.find(branchName)?.value ?: return ReturnResult.COMMIT

                val current = panel.commitMessage.orEmpty()
                val trimmed = current.trimStart()

                // Already prefixed? Accept ABC-123:, ABC-123 -, ABC-123–, ABC-123 |
                val alreadyPrefixed = Regex(
                    pattern = "^\\s*${Regex.escape(ticket)}\\s*[:\\-–|]\\s*",
                    options = setOf(RegexOption.IGNORE_CASE)
                ).containsMatchIn(trimmed)

                if (!alreadyPrefixed) {
                    panel.commitMessage = "$ticket: $trimmed"
                    LOG.info("ticket-commit-guard: prefixed commit message with $ticket")
                } else {
                    LOG.info("ticket-commit-guard: message already prefixed with $ticket — no change")
                }

                return ReturnResult.COMMIT
            }

            // NOTE: We intentionally do NOT clear here.
            // We clear only after PUSH via GitPushListener (see TicketPushActivity).
            // override fun checkinSuccessful() {}
        }
    }
}
