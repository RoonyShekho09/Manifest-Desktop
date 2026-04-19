package com.jawharat.manifest.presentation.feature.home

import androidx.compose.foundation.text.input.TextFieldState
import com.jawharat.manifest.domain.entity.Dispatch
import com.jawharat.manifest.domain.entity.DispatchLine
import com.jawharat.manifest.domain.entity.Driver
import com.jawharat.manifest.domain.entity.DriverInformation
import com.jawharat.manifest.domain.entity.Office
import com.jawharat.manifest.domain.entity.UserInformation
import com.jawharat.manifest.domain.entity.UserLocation
import com.jawharat.manifest.domain.repository.AuthRepository
import com.jawharat.manifest.domain.repository.ManifestRepository
import com.jawharat.manifest.presentation.feature.home.scanner.IDocumentScanner
import com.jawharat.manifest.presentation.feature.shared.AppSnackBarHostState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val authRepository: AuthRepository = mockk(relaxed = true)
    private val manifestRepository: ManifestRepository = mockk(relaxed = true)
    private val documentScanner: IDocumentScanner = mockk(relaxed = true)
    private val snackBarHostState: AppSnackBarHostState = mockk(relaxed = true)

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { documentScanner.isSoftwareInstalled } returns true

        coEvery { manifestRepository.getUserInformation() } returns UserInformation(
            location = UserLocation(id = "", name = "Erbil")
        )

        viewModel = HomeViewModel(
            authRepository = authRepository,
            manifestRepository = manifestRepository,
            documentScanner = documentScanner,
            snackBarHostState = snackBarHostState,
            ioDispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onDismissCountDownDialog_updatesState() {
        viewModel.onDismissCountDownDialog()
        assertFalse(viewModel.state.value.isCountDownVisible)
    }

    @Test
    fun onLogoutClick_showsLogoutConfirmation() {
        viewModel.onLogoutClick()
        assertTrue(viewModel.state.value.isLogoutConfirmationVisible)
    }

    @Test
    fun onDismissLogoutConfirmation_hidesLogoutConfirmation() {
        viewModel.onDismissLogoutConfirmation()
        assertFalse(viewModel.state.value.isLogoutConfirmationVisible)
    }

    @Test
    fun logout_callsRepositoryAndEmitsEvent() = runTest {
        coEvery { authRepository.logout() } returns Unit

        viewModel.logout()
        advanceUntilIdle()

        coVerify { authRepository.logout() }
        assertFalse(viewModel.state.value.isLogoutConfirmationVisible)
    }

    @Test
    fun scanDriverQrCode_updatesStateOnSuccess() = runTest {
        val mockDriverResponse = Driver(
            id = "12345",
            destination = "Baghdad",
            name = "John Doe",
            phone = "555-0192",
            driverId = "12345",
        )

        coEvery { manifestRepository.scanDriverQrCode("12345") } returns mockDriverResponse

        viewModel.scanDriverQrCode("12345")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("12345", state.manifest.driverId)
        assertEquals("Baghdad", state.manifest.to)
        assertEquals("John Doe", state.manifest.driverName)
        assertEquals("555-0192", state.manifest.driverPhoneNumber)
        assertTrue(state.scanState.isDriverScanned)
        assertFalse(state.isLoading)
    }

    @Test
    fun scanVehicleQrCode_updatesStateOnSuccess() = runTest {
        val mockDispatch = Dispatch(
            plateNumber = "ABC-123",
            price = 15000,
            vehicleName = "Minivan",
            driverInformation = DriverInformation(),
            id = "1234",
            isInside = true,
            dispatchLine = DispatchLine(name = "Line A"),
            office = Office(),
            vehicleType = "جمسي خارجي",
        )

        coEvery { manifestRepository.scanVehicleQrCode("1234") } returns mockDispatch

        viewModel.scanVehicleQrCode("1234")
        advanceUntilIdle()

        println(viewModel.state.value)
        val state = viewModel.state.value
        assertEquals("ABC-123", state.manifest.plateNumber)
        assertEquals(15000, state.manifest.price)
        assertEquals("Minivan", state.manifest.vehicleType)
        assertEquals("Line A", state.manifest.from)
        assertTrue(state.scanState.isVehicleScanned)
        assertFalse(state.isLoading)
    }

    @Test
    fun onAddPassengers_updatesPassengersListAndHidesDialog() {
        val newPassengers = listOf(
            PassengerFieldState(
                id = TextFieldState("ID1"),
                name = TextFieldState("Passenger One"),
                countryCode = TextFieldState("IQ"),
                isEditable = false
            )
        )

        viewModel.onAddPassengers(newPassengers)

        assertEquals(newPassengers, viewModel.state.value.passengers)
        assertFalse(viewModel.state.value.isAddPassengersDialogVisible)
    }

    @Test
    fun onPassengerFieldClick_showsDialogIfPriceIsNot10000() {
        val mockVehicleResponse = Dispatch(
            plateNumber = "ABC-123",
            price = 15000,
            vehicleName = "Minivan",
            driverInformation = DriverInformation(),
            id = "12345",
            isInside = false,
            dispatchLine = DispatchLine(),
            office = Office(),
            vehicleType = "",
        )
        coEvery { manifestRepository.scanVehicleQrCode("ABC-123") } returns mockVehicleResponse
        viewModel.scanVehicleQrCode("ABC-123")

        viewModel.onPassengerFieldClick()

        assertTrue(viewModel.state.value.isAddPassengersDialogVisible)
    }

    @Test
    fun onQrCodeResult_parsesAndTriggersCorrectScans() = runTest {
        val mockDriverResponse = Driver(
            id = "D123",
            destination = "Test",
            name = "Test Driver",
            driverId = "123",
            phone = "123-768",
        )
        val mockVehicleResponse = Dispatch(
            id = "1234",
            vehicleName = "Mercedes",
            driverInformation = DriverInformation(),
            isInside = false,
            dispatchLine = DispatchLine(),
            office = Office(),
            price = 3000,
            vehicleType = "",
            plateNumber = "A7592Ih",
        )

        coEvery { manifestRepository.scanDriverQrCode("D123") } returns mockDriverResponse
        coEvery { manifestRepository.scanVehicleQrCode("V123") } returns mockVehicleResponse

        viewModel.onQrCodeResult("D:D123|V:V123")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.scanState.isDriverScanned)
        assertFalse(state.scanState.isVehicleScanned)
    }
}
