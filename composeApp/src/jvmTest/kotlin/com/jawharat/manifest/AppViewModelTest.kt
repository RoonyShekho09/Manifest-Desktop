package com.jawharat.manifest

import ManifestDesktop.composeApp.BuildConfig
import com.jawharat.manifest.domain.entity.UpdateInfo
import com.jawharat.manifest.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {
    private lateinit var viewModel: AppViewModel
    private val repository: AuthRepository = mockk(relaxed = true)
    private lateinit var testDispatcher: TestDispatcher
    private val currentBuild = BuildConfig.BUILD_NUMBER

    @Before
    fun setup() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        viewModel = AppViewModel(repository = repository, ioDispatcher = testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should set login and session state from repository`() = runTest {
        every { repository.isUserLoggedIn } returns true
        every { repository.hasSessionExpired } returns false

        testDispatcher = UnconfinedTestDispatcher()

        viewModel = AppViewModel(repository = repository, ioDispatcher = testDispatcher)

        advanceUntilIdle()

        assertEquals(true, viewModel.state.value.isUsedLoggedIn)
        assertEquals(false, viewModel.state.value.hasSessionExpired)

        coVerify(atLeast = 2) { repository.getUpdateInfo(any()) }
    }

    @Test
    fun `show dialog when latest build is higher than current build`() = runTest {
        val updateInfo = UpdateInfo(latestBuild = currentBuild + 4, minBuild = 90, isForced = false)
        coEvery { repository.getUpdateInfo(any()) } returns updateInfo

        testDispatcher = UnconfinedTestDispatcher()
        viewModel = AppViewModel(repository = repository, ioDispatcher = testDispatcher)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isDialogVisible)
        assertTrue(viewModel.state.value.isForcedUpdate)
    }

    @Test
    fun `should set forced update when current build is below min build`() =
        runTest {
            val updateInfo =
                UpdateInfo(latestBuild = 105, minBuild = currentBuild + 1, isForced = false)

            coEvery { repository.getUpdateInfo(any()) } returns updateInfo

            testDispatcher = UnconfinedTestDispatcher()
            viewModel = AppViewModel(repository = repository, ioDispatcher = testDispatcher)
            advanceUntilIdle()

            assertTrue(viewModel.state.value.isDialogVisible)
            assertTrue(viewModel.state.value.isForcedUpdate)
        }

    @Test
    fun `forced update is false when current build equals to min build and iaForced is false`() =
        runTest {
            val updateInfo =
                UpdateInfo(latestBuild = 110, minBuild = currentBuild, isForced = false)
            coEvery { repository.getUpdateInfo(any()) } returns updateInfo

            testDispatcher = UnconfinedTestDispatcher()
            viewModel = AppViewModel(repository = repository, ioDispatcher = testDispatcher)
            advanceUntilIdle()

            assertFalse(viewModel.state.value.isForcedUpdate)
        }

    @Test
    fun `onDismissDialog should set isDialogVisible to false`() = runTest {
        val updateInfo = UpdateInfo(latestBuild = 110, minBuild = 90, isForced = false)
        coEvery { repository.getUpdateInfo(any()) } returns updateInfo

        testDispatcher = UnconfinedTestDispatcher()
        viewModel = AppViewModel(repository = repository, ioDispatcher = testDispatcher)
        advanceUntilIdle()

        viewModel.onDismissDialog()

        assertFalse(viewModel.state.value.isDialogVisible)
    }
}
