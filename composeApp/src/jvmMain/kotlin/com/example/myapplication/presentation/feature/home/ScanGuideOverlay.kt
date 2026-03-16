package com.example.myapplication.presentation.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun ScanGuideOverlay(sizeMultiplier: Float = 0.4f) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val guideSize = minOf(size.width, size.height) * sizeMultiplier
        val left = (size.width - guideSize) / 2f
        val top = (size.height - guideSize) / 2f

        val path = Path().apply {
            addRect(Rect(0f, 0f, size.width, size.height))
            addRect(Rect(left, top, left + guideSize, top + guideSize))
        }
        drawPath(path, color = Color.Black.copy(alpha = 0.5f), blendMode = BlendMode.Overlay)

        // Draw green border
        drawRect(
            color = Color.Green,
            topLeft = Offset(left, top),
            size = Size(guideSize, guideSize),
            style = Stroke(width = 3.dp.toPx())
        )


        val cornerLength = guideSize * 0.1f
        val strokeWidth = 4.dp.toPx()
        listOf(

            Pair(Offset(left, top + cornerLength), Offset(left, top)),
            Pair(Offset(left, top), Offset(left + cornerLength, top)),

            Pair(Offset(left + guideSize - cornerLength, top), Offset(left + guideSize, top)),
            Pair(Offset(left + guideSize, top), Offset(left + guideSize, top + cornerLength)),

            Pair(Offset(left, top + guideSize - cornerLength), Offset(left, top + guideSize)),
            Pair(Offset(left, top + guideSize), Offset(left + cornerLength, top + guideSize)),

            Pair(
                Offset(left + guideSize - cornerLength, top + guideSize),
                Offset(left + guideSize, top + guideSize)
            ),
            Pair(
                Offset(left + guideSize, top + guideSize),
                Offset(left + guideSize, top + guideSize - cornerLength)
            ),
        ).forEach { (start, end) ->
            drawLine(Color.Green, start, end, strokeWidth, StrokeCap.Round)
        }
    }
}
