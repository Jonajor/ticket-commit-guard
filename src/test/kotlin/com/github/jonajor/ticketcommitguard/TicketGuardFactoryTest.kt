package com.github.jonajor.ticketcommitguard

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.changes.CommitContext
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TicketGuardFactoryTest {

 private lateinit var project: Project
 private lateinit var panel: CheckinProjectPanel
 private lateinit var commitContext: CommitContext
 private lateinit var repoManager: GitRepositoryManager
 private lateinit var repo: GitRepository

 @BeforeEach
 fun setUp() {
  project = mockk(relaxed = true)
  panel = mockk(relaxed = true)
  commitContext = mockk(relaxed = true)
  repoManager = mockk(relaxed = true)
  repo = mockk(relaxed = true)

  every { panel.project } returns project

  // Mock static: GitRepositoryManager.getInstance(project)
  mockkStatic(GitRepositoryManager::class)
  every { GitRepositoryManager.getInstance(project) } returns repoManager
 }

 @AfterEach
 fun tearDown() {
  unmockkAll()
 }

 private fun makeHandler(): CheckinHandler {
  val factory = TicketGuardFactory()
  return factory.createHandler(panel, commitContext)
 }

 @Test
 fun `prefixes message when branch contains ticket and message missing prefix`() {
  // Branch contains ticket like ABC-123-foo
  every { repo.currentBranchName } returns "DARK-123-fix-bug"
  every { repoManager.repositories } returns listOf(repo)
  every { panel.commitMessage } returns "fix video page"

  val result = makeHandler().beforeCheckin()

  // Should set commit message with the ticket prefix once
  verify(exactly = 1) { panel.setCommitMessage("DARK-123: fix video page") }
  assertEquals(CheckinHandler.ReturnResult.COMMIT, result)
 }

 @Test
 fun `does not change message when already prefixed`() {
  every { repo.currentBranchName } returns "DARK-123-fix-bug"
  every { repoManager.repositories } returns listOf(repo)
  every { panel.commitMessage } returns "DARK-123: fix video page"

  val result = makeHandler().beforeCheckin()

  verify(exactly = 0) { panel.setCommitMessage(any()) }
  assertEquals(CheckinHandler.ReturnResult.COMMIT, result)
 }

 @Test
 fun `does nothing when no repository present`() {
  every { repoManager.repositories } returns emptyList()
  every { panel.commitMessage } returns "some message"

  val result = makeHandler().beforeCheckin()

  verify(exactly = 0) { panel.setCommitMessage(any()) }
  assertEquals(CheckinHandler.ReturnResult.COMMIT, result)
 }

 @Test
 fun `does nothing when branch has no ticket pattern`() {
  every { repo.currentBranchName } returns "feature/cleanup"
  every { repoManager.repositories } returns listOf(repo)
  every { panel.commitMessage } returns "some message"

  val result = makeHandler().beforeCheckin()

  verify(exactly = 0) { panel.setCommitMessage(any()) }
  assertEquals(CheckinHandler.ReturnResult.COMMIT, result)
 }

 @Test
 fun `trims leading spaces in message when prefixing`() {
  every { repo.currentBranchName } returns "ABC-999-thing"
  every { repoManager.repositories } returns listOf(repo)
  every { panel.commitMessage } returns "   fix spacing"

  val result = makeHandler().beforeCheckin()

  verify(exactly = 1) { panel.setCommitMessage("ABC-999: fix spacing") }
  assertEquals(CheckinHandler.ReturnResult.COMMIT, result)
 }
}
