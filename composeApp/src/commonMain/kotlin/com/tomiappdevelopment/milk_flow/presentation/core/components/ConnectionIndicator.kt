package com.tomiappdevelopment.milk_flow.presentation.core.components
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tomiappdevelopment.milk_flow.domain.core.ConnectionState
import milkflow.composeapp.generated.resources.Res
import milkflow.composeapp.generated.resources.connection_status_connected
import milkflow.composeapp.generated.resources.connection_status_weak
import milkflow.composeapp.generated.resources.connection_status_no_connection
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ConnectionIndicator(
    connectionState: ConnectionState,
    modifier: Modifier = Modifier
) {
    val (icon, color, description) = when (connectionState) {
        ConnectionState.Available -> Triple(Icons.Default.Done, MaterialTheme.colorScheme.primary, stringResource(Res.string.connection_status_connected))
        ConnectionState.Losing -> Triple(Icons.Default.Close, MaterialTheme.colorScheme.tertiary, stringResource(Res.string.connection_status_weak))
        ConnectionState.Lost, ConnectionState.Unavailable -> Triple(Icons.Default.Settings, MaterialTheme.colorScheme.error, stringResource(Res.string.connection_status_no_connection))
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = color,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = description,
            color = color,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
