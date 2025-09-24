package com.github.jonajor.ticketcommitguard

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.CheckinProjectPanel
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

object CommitPanelHolder {
    private val panels = ConcurrentHashMap<Project, WeakReference<CheckinProjectPanel>>()

    fun setPanel(project: Project, panel: CheckinProjectPanel) {
        panels[project] = WeakReference(panel)
    }

    fun clearMessageIfPresent(project: Project) {
        panels[project]?.get()?.commitMessage = ""
    }

    fun drop(project: Project) {
        panels.remove(project)
    }
}
