package com.example.myapplication.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.myapplication.utils.painter
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.ic_arrow_upward

@Composable
fun ScrollToTopBox(onClick: () -> Unit) {
    Box(
        Modifier.fillMaxWidth()
            .fillMaxHeight(0.8f)
            .padding(24.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier.clip(CircleShape)
                .clickable(onClick = onClick)
                .background(MaterialTheme.colorScheme.tertiary)
        ) {
            Icon(
                painter = Res.drawable.ic_arrow_upward.painter,
                contentDescription = null,
                modifier = Modifier.padding(8.dp),
                tint = MaterialTheme.colorScheme.onTertiary
            )
        }
    }
}
