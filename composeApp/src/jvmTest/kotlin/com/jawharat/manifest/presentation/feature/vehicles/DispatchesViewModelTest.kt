package com.jawharat.manifest.presentation.feature.vehicles

import com.jawharat.manifest.domain.entity.manifest.DispatchSummary
import com.jawharat.manifest.domain.repository.ManifestRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNull


@OptIn(ExperimentalCoroutinesApi::class)
class DispatchesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val repository: ManifestRepository = mockk(relaxed = true)

    private lateinit var viewModel: DispatchesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getUserInformation() } returns mockk(relaxed = true)
        coEvery { repository.getDrivers(any()) } returns emptyList()
        coEvery { repository.getVehicleTypes(any()) } returns emptyList()
        coEvery { repository.getLines(any()) } returns emptyList()
        coEvery { repository.getDispatches(any()) } returns emptyList()

        viewModel = DispatchesViewModel(repository = repository, ioDispatcher = testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initCallsAllInitializationMethods() = runTest {
        coVerify(exactly = 1) { repository.getUserInformation() }
        coVerify(exactly = 1) { repository.getDrivers(true) }
        coVerify(exactly = 1) { repository.getVehicleTypes(true) }
        coVerify(exactly = 1) { repository.getLines(true) }
        coVerify(exactly = 1) { repository.getDispatches(true) }
    }

    @Test
    fun onAddClickShowsDialog() {
        viewModel.onAddClick()

        assertTrue(viewModel.state.value.isDialogVisible)
    }

    @Test
    fun onDismissDialogHidesDialogAndClearsEditState() {
        viewModel.onAddClick()

        viewModel.onDismissDialog()

        assertFalse(viewModel.state.value.isDialogVisible)
        assertNull(viewModel.state.value.dispatchToEdit)
    }

    @Test
    fun addDispatchCallsRepositoryAndHidesDialog() = runTest {
        val dispatchUiState: DispatchUiState = mockk(relaxed = true) {
            every { plateNumber } returns "12345"
            every { vehicleName } returns "Toyota"
            every { price } returns "5000"
            every { vehicleType } returns "Sedan"
            every { driver.id } returns "driver-123"
            every { line.id } returns "line-123"
        }
        val expectedResult = mockk<DispatchSummary>(relaxed = true) {
            every { id } returns "dispatch-123"
        }
        coEvery {
            repository.addDispatch(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns expectedResult

        viewModel.addDispatch(dispatchUiState)

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) {
            repository.addDispatch(
                plateNumber = "12345",
                vehicleName = "Toyota",
                price = 5000,
                driverId = "driver-123",
                line = "line-123",
                vehicleType = "Sedan"
            )
        }
        assertFalse(viewModel.state.value.isDialogVisible)
    }

    @Test
    fun editDispatchCallsRepositoryAndHidesDialog() = runTest {
        val dispatchUiState: DispatchUiState = mockk(relaxed = true) {
            every { id } returns "dispatch-123"
            every { plateNumber } returns "12345"
            every { vehicleName } returns "Toyota"
            every { price } returns "5000"
            every { vehicleType } returns "Sedan"
            every { driver.id } returns "driver-123"
            every { line.id } returns "line-123"
        }
        coEvery {
            repository.editDispatch(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Unit

        viewModel.editDispatch(dispatchUiState)

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) {
            repository.editDispatch(
                vehicleNumber = "12345",
                vehicleName = "Toyota",
                vehicleType = "Sedan",
                price = 5000,
                driverId = "driver-123",
                line = "line-123",
                id = "dispatch-123"
            )
        }
        assertFalse(viewModel.state.value.isDialogVisible)
    }
}
