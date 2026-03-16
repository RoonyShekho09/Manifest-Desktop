@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.myapplication.presentation.feature.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.presentation.components.AppTextField
import com.example.myapplication.presentation.feature.shared.AppSnackBarVisuals
import com.example.myapplication.presentation.feature.shared.LocalSnackBarState
import com.example.myapplication.utils.Listen
import com.example.myapplication.utils.painter
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.ic_qr_code_scanning
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsState(null)

    event?.Listen {
        when (it) {
            else -> {}
        }
    }

    Content(state = state, viewModel = viewModel)
}

@Composable
fun Content(state: HomeUiState, viewModel: HomeViewModel) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { androidx.compose.material.Text("Register Trip") },
                backgroundColor = androidx.compose.material.MaterialTheme.colors.primary,
                contentColor = androidx.compose.material.MaterialTheme.colors.onPrimary
            )
        },
        snackbarHost = {
            SnackbarHost(LocalSnackBarState.current.nativeHostState) { data ->
                val visuals = data.visuals as? AppSnackBarVisuals
                Card(
                    modifier = Modifier.padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            if (visuals?.isError == true)
                                MaterialTheme.colorScheme.errorContainer
                            else
                                Color(0xFF0C9912),
                        contentColor = if (visuals?.isError == true) MaterialTheme.colorScheme.onErrorContainer else Color.White
                    )
                ) {
                    Row(
                        Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        visuals?.icon?.let {
                            androidx.compose.material3.Icon(
                                painterResource(it),
                                null
                            )
                        }
                        Text(data.visuals.message)
                    }
                }
            }
        }
    ) { paddingValues ->

        Box(Modifier.fillMaxSize()) {
            if (state.startScanning) {
                ScanGuideOverlay()
            }

//            ScanQrFromCamera {
//
//            }

            Column(
                modifier = Modifier.padding(24.dp).padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FormSection(title = "Trip Details") {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        AppTextField(
                            value = state.manifest.date,
                            placeholder = "Date",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.weight(1f)
                        )
                        AppTextField(
                            value = state.manifest.price,
                            placeholder = "Price",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    AppTextField(
                        value = if (state.manifest.from.isNotEmpty()) "${state.manifest.from} - ${state.manifest.to}" else "",
                        placeholder = "From - To",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                FormSection(title = "Vehicle Information") {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        AppTextField(
                            value = state.manifest.vehicleNumber,
                            placeholder = "Vehicle Number",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.weight(1f)
                        )

                        AppTextField(
                            value = state.manifest.vehicleType,
                            placeholder = "Vehicle Type",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.weight(1f)
                        )

                        AppTextField(
                            value = state.manifest.passengers.ifEmpty { "" }.toString(),
                            placeholder = "Passengers",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                FormSection(title = "Personnel") {
                    AppTextField(
                        value = state.manifest.driverName,
                        placeholder = "Driver Name",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        AppTextField(
                            value = state.manifest.driverIdNumber,
                            placeholder = "Driver ID Number",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.weight(1f)
                        )
                        AppTextField(
                            value = state.manifest.driverPhoneNumber,
                            placeholder = "Driver Phone Number",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                FormSection(title = "") {
                    Button(
                        onClick = viewModel::onStartScanning,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            painter = Res.drawable.ic_qr_code_scanning.painter,
                            tint = Color.White,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Scan QR Code")
                    }
                }
            }
        }
    }
}

@Composable
fun QrScannerOverlay(modifier: Modifier = Modifier, onCancel: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val scanLineProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
        label = ""
    )

    val bracketAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Box(
        modifier = modifier.fillMaxSize()
            .background(Color.Black.copy(0.6f))
    ) {
        Canvas(modifier = Modifier.size(200.dp).background(Color.White).align(Alignment.Center)) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val cutoutTopLeft = Offset(
                x = (canvasWidth - canvasWidth) / 2f,
                y = (canvasHeight - canvasWidth) / 2f
            )

            val path = Path().apply {
                addRect(Rect(0f, 0f, canvasWidth, canvasHeight))
                addRoundRect(
                    RoundRect(
                        rect = Rect(cutoutTopLeft, Size(canvasWidth, canvasWidth)),
                        cornerRadius = CornerRadius(16.dp.toPx())
                    )
                )
                fillType = PathFillType.EvenOdd
            }
            drawPath(path, Color.Black.copy(alpha = 0.6f))

            val bracketLength = 40.dp.toPx()
            val strokeWidth = 4.dp.toPx()
            val cornerRadius = 16.dp.toPx()
            val color = Color.Green.copy(alpha = bracketAlpha)

            drawPath(
                path = Path().apply {
                    moveTo(cutoutTopLeft.x, cutoutTopLeft.y + bracketLength)
                    quadraticTo(
                        cutoutTopLeft.x,
                        cutoutTopLeft.y,
                        cutoutTopLeft.x + cornerRadius,
                        cutoutTopLeft.y
                    )
                    lineTo(cutoutTopLeft.x + bracketLength, cutoutTopLeft.y)
                },
                color = color,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            drawPath(
                path = Path().apply {
                    moveTo(cutoutTopLeft.x + canvasWidth - bracketLength, cutoutTopLeft.y)
                    quadraticTo(
                        cutoutTopLeft.x + canvasWidth,
                        cutoutTopLeft.y,
                        cutoutTopLeft.x + canvasWidth,
                        cutoutTopLeft.y + cornerRadius
                    )
                    lineTo(cutoutTopLeft.x + canvasWidth, cutoutTopLeft.y + bracketLength)
                },
                color = color,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            drawPath(
                path = Path().apply {
                    moveTo(cutoutTopLeft.x, cutoutTopLeft.y + canvasWidth - bracketLength)
                    quadraticTo(
                        cutoutTopLeft.x,
                        cutoutTopLeft.y + canvasWidth,
                        cutoutTopLeft.x + cornerRadius,
                        cutoutTopLeft.y + canvasWidth
                    )
                    lineTo(cutoutTopLeft.x + bracketLength, cutoutTopLeft.y + canvasWidth)
                },
                color = color,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            drawPath(
                path = Path().apply {
                    moveTo(
                        cutoutTopLeft.x + canvasWidth,
                        cutoutTopLeft.y + canvasWidth - bracketLength
                    )
                    quadraticTo(
                        cutoutTopLeft.x + canvasWidth,
                        cutoutTopLeft.y + canvasWidth,
                        cutoutTopLeft.x + canvasWidth - cornerRadius,
                        cutoutTopLeft.y + canvasWidth
                    )
                    lineTo(
                        cutoutTopLeft.x + canvasWidth - bracketLength,
                        cutoutTopLeft.y + canvasWidth
                    )
                },
                color = color,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            val lineY = cutoutTopLeft.y + (canvasWidth * scanLineProgress)

            drawLine(
                color = Color.Green,
                start = Offset(cutoutTopLeft.x, lineY),
                end = Offset(cutoutTopLeft.x + canvasWidth, lineY),
                strokeWidth = 2.dp.toPx()
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Green.copy(alpha = 0.4f),
                        Color.Transparent
                    ),
                    startY = lineY - 12.dp.toPx(),
                    endY = lineY + 12.dp.toPx()
                ),
                topLeft = Offset(cutoutTopLeft.x, lineY - 12.dp.toPx()),
                size = Size(canvasWidth, 24.dp.toPx())
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .size(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                .clickable(onClick = onCancel)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedScanningText()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tap anywhere to cancel",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AnimatedScanningText(modifier: Modifier = Modifier) {
    var dotCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(400)
            dotCount = (dotCount + 1) % 4
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.CenterStart) {
            Text(
                text = "Scanning...",
                color = Color.Transparent
            )

            Text(
                text = "Scanning${".".repeat(dotCount)}",
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = DividerDefaults.color.copy(0.6f)
        )
        content()
    }
}
