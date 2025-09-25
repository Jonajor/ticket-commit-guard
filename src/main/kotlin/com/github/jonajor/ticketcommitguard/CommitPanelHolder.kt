package com.github.jonajor.ticketcommitguard

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.CheckinProjectPanel
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

object CommitPanelHolder {
    private val log = Logger.getInstance(CommitPanelHolder::class.java)
    private val panels = ConcurrentHashMap<Project, WeakReference<CheckinProjectPanel>>()

    fun setPanel(project: Project, panel: CheckinProjectPanel) {
        panels[project] = WeakReference(panel)
    }

    fun clearMessageIfPresent(project: Project) {
        if (project.isDisposed) return
        ApplicationManager.getApplication().invokeLater({
            if (project.isDisposed) return@invokeLater
            val panel = panels[project]?.get() ?: return@invokeLater
            try {
                // EditorTextField/CommitMessage handles write-intent internally
                panel.commitMessage = ""
            } catch (t: Throwable) {
                log.warn("ticket-commit-guard: failed to clear commit message on EDT", t)
            }
        }, project.disposed)
    }
}
