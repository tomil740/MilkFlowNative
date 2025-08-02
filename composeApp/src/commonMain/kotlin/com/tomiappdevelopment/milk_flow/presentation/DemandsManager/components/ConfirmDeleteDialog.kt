package com.tomiappdevelopment.milk_flow.presentation.DemandsManager.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import milkflow.composeapp.generated.resources.Res
import milkflow.composeapp.generated.resources.cancel
import milkflow.composeapp.generated.resources.delete_confirm
import milkflow.composeapp.generated.resources.delete_dialog_text
import milkflow.composeapp.generated.resources.delete_dialog_title
import org.jetbrains.compose.resources.ExperimentalResourceApi


@OptIn(ExperimentalResourceApi::class)
@Composable
fun ConfirmDeleteDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (!show) return

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = UiText.StringResource(Res.string.delete_dialog_title).asString(),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = UiText.StringResource(Res.string.delete_dialog_text).asString(),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = UiText.StringResource(Res.string.delete_confirm).asString(),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = UiText.StringResource(Res.string.cancel).asString())
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    )
}
