package com.github.jonajor.ticketcommitguard

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.util.messages.MessageBusConnection
import git4idea.push.GitPushListener
import git4idea.push.GitPushRepoResult
import git4idea.repo.GitRepository

class TicketPushActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        val log = Logger.getInstance("Ticket Commit Guard")
        // Auto-disconnect when project is disposed
        val connection: MessageBusConnection = project.messageBus.connect(project)

        connection.subscribe(GitPushListener.TOPIC, object : GitPushListener {
            override fun onCompleted(repository: GitRepository, pushResult: GitPushRepoResult) {
                val type = pushResult.type

                // Treat these as successful enough to clear:
                // SUCCESS (normal), UP_TO_DATE (nothing to push), NEW_BRANCH (created), FORCED (force push)
                val shouldClear = when (type) {
                    GitPushRepoResult.Type.SUCCESS,
                    GitPushRepoResult.Type.UP_TO_DATE,
                    GitPushRepoResult.Type.NEW_BRANCH,
                    GitPushRepoResult.Type.FORCED -> true

                    // Leave message for rework/retry on these:
                    GitPushRepoResult.Type.REJECTED_OTHER,
                    GitPushRepoResult.Type.REJECTED_NO_FF,
                    GitPushRepoResult.Type.REJECTED_STALE_INFO,
                    GitPushRepoResult.Type.ERROR,
                    GitPushRepoResult.Type.NOT_PUSHED -> false
                }

                if (shouldClear) {
                    try {
                        CommitPanelHolder.clearMessageIfPresent(repository.project)
                        log.info("ticket-commit-guard: cleared commit message after push (${repository.presentableUrl}) [$type]")
                    } catch (t: Throwable) {
                        log.warn("ticket-commit-guard: failed to clear commit message after push", t)
                    }
                } else {
                    log.info("ticket-commit-guard: push not successful/complete ($type) — leaving commit message intact")
                }
            }
        })
    }
}