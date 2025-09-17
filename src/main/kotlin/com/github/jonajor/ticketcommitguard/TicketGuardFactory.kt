package com.github.jonajor.ticketcommitguard

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.changes.CommitContext
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory
import git4idea.repo.GitRepositoryManager

private val LOG = Logger.getInstance("ticket-commit-guard")

class TicketGuardFactory : CheckinHandlerFactory() {
    override fun createHandler(panel: CheckinProjectPanel, commitContext: CommitContext): CheckinHandler {
        return object : CheckinHandler() {
            override fun beforeCheckin(): ReturnResult {
                val project = panel.project
                val repo = GitRepositoryManager.getInstance(project).repositories.firstOrNull()
                val branchName = repo?.currentBranchName ?: return ReturnResult.COMMIT

                // Match tickets like ABC-123
                val ticket = Regex("[A-Z]+-[0-9]+").find(branchName)?.value ?: return ReturnResult.COMMIT

                val current = panel.commitMessage
                if (!current.startsWith("$ticket:")) {
                    panel.setCommitMessage("$ticket: ${current.trimStart()}")

                    LOG.info("ticket-commit-guard: prefixed commit message with $ticket")
                }

                return ReturnResult.COMMIT
            }
        }
    }
}
