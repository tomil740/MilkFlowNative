package com.tomiappdevelopment.milk_flow.core.presentation

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tomiappdevelopment.milk_flow.core.notifications.NotificationPermissionHelper
import milkflow.composeapp.generated.resources.Res
import milkflow.composeapp.generated.resources.notification_permission_confirm
import milkflow.composeapp.generated.resources.notification_permission_message
import milkflow.composeapp.generated.resources.notification_permission_title
import org.jetbrains.compose.resources.ExperimentalResourceApi


@OptIn(ExperimentalResourceApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NotificationPermissionRequester(
    activity: Activity,
    permissionHelper: NotificationPermissionHelper,
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) onPermissionGranted()
            // else: optionally handle denial here or silently ignore
        }
    )

    var showRationale by remember { mutableStateOf(permissionHelper.shouldShowRationale(activity)) }

    if (!permissionHelper.hasNotificationPermission()) {
        if (showRationale) {
            AlertDialog(
                onDismissRequest = { /* Prevent dismissing without action, or allow if you prefer */ },
                title = {
                    Text(
                        text = UiText.StringResource(Res.string.notification_permission_title).asString(),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                },
                text = {
                    Text(
                        text = UiText.StringResource(Res.string.notification_permission_message).asString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            showRationale = false
                        }
                    ) {
                        Text(text = UiText.StringResource(Res.string.notification_permission_confirm).asString())
                    }
                },
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LaunchedEffect(Unit) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    } else {
        onPermissionGranted()
    }
}
