package com.bianca.ai_assistant.viewModel.task

import com.bianca.ai_assistant.infrastructure.room.task.TaskEntity
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var repository: ITaskRepository
    private lateinit var viewModel: TaskViewModel

    @Before
    fun setUp() {
        repository = mockk()
    }

    @Test
    fun `verify task addition updates the tasks list`() = runTest {
        val newTask = TaskEntity(id = 1L, title = "test")
        coEvery { repository.getAllTasks() } returnsMany listOf(emptyList(), listOf(newTask))
        coJustRun { repository.addTask(newTask) }

        viewModel = TaskViewModel(repository)
        advanceUntilIdle()

        viewModel.addTask(newTask)
        advanceUntilIdle()

        assertEquals(listOf(newTask), viewModel.tasks.value)
    }

    @Test
    fun `verify toggling a task updates its completion state`() = runTest {
        val task = TaskEntity(id = 1L, title = "test")
        val toggled = task.copy(isDone = true)
        coEvery { repository.getAllTasks() } returnsMany listOf(listOf(task), listOf(toggled))
        coJustRun { repository.updateTask(toggled) }

        viewModel = TaskViewModel(repository)
        advanceUntilIdle()

        viewModel.toggleTask(task)
        advanceUntilIdle()

        assertTrue(viewModel.tasks.value.first().isDone)
    }
}
