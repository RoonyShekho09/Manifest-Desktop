package com.jawharat.manifest.presentation.feature.drivers

import com.jawharat.manifest.domain.entity.Driver
import com.jawharat.manifest.domain.repository.ManifestRepository
import com.jawharat.manifest.presentation.feature.shared.AppSnackBarHostState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DriversViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: ManifestRepository = mockk(relaxed = true)
    private val snackBarHostState: AppSnackBarHostState = mockk(relaxed = true)

    private lateinit var viewModel: DriversViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getDrivers(any()) } returns emptyList()
        viewModel = DriversViewModel(repository, snackBarHostState, ioDispatcher = testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onDismissDialog_updatesStateCorrectly() {
        viewModel.onDismissDialog()

        assertFalse(viewModel.state.value.isDialogVisible)
    }

    @Test
    fun onRefresh_clearsQueryAndFetchesDrivers() = runTest {
        viewModel.onRefresh()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("", viewModel.state.value.mainSearchState.query.text.toString())
        coVerify(atLeast = 1) { repository.getDrivers(fetch = true) }
    }

    @Test
    fun addDriver_withValidData_callsRepository() = runTest {
        val driver = Driver(
            id = "1",
            driverId = "D1",
            name = "Test Name ",
            phoneNumber = "12345 ",
            destination = "Test Dest",
            blocked = true
        )

        viewModel.addDriver(driver)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            repository.addDriver(
                driverId = "D1",
                name = "Test Name",
                phoneNumber = "12345",
                destination = "Test Dest"
            )
        }
    }

    @Test
    fun editDriver_withValidData_callsRepository() = runTest {
        val driver = Driver(
            id = "1",
            driverId = "D1",
            name = "Test Name",
            phoneNumber = "12345",
            destination = "Test Dest",
            blocked = true
        )

        viewModel.editDriver(driver)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            repository.editDriver(
                id = "1",
                driverId = "D1",
                name = "Test Name",
                phoneNumber = "12345",
                destination = "Test Dest"
            )
        }
    }

    @Test
    fun onConfirmAddEditDriver_withIsEditFalse_callsAdd() = runTest {
        viewModel.onConfirmAddEditDriver(
            Driver(
                id = "eloquentiam",
                name = "Mavis Callahan",
                phoneNumber = "(899) 671-4903",
                destination = "option",
                driverId = "dignissim",
                blocked = true
            ),
        )

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repository.addDriver(any(), any(), any(), any()) }
        coVerify(exactly = 0) { repository.editDriver(any(), any(), any(), any(), any()) }
    }
}
