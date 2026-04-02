package com.jawharat.manifest.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.ic_add
import com.jawharat.manifest.utils.handPointerHover
import com.jawharat.manifest.utils.painter

@Composable
fun AddItemButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.handPointerHover()
    ) {
        Icon(
            painter = Res.drawable.ic_add.painter,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}
