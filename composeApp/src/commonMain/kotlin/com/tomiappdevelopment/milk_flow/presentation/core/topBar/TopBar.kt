package com.tomiappdevelopment.milk_flow.presentation.core.topBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.tomiappdevelopment.milk_flow.presentation.core.AppRoute
import com.tomiappdevelopment.milk_flow.presentation.core.components.ActionButton
import com.tomiappdevelopment.milk_flow.presentation.core.components.AuthActionButton

@Composable
fun TopBar(
    state: TopBarUiState,
    onNavigate: (AppRoute) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(top = 30.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ActionButton(
            icon = "🛍️",
            label = "כל המוצרים",
            onClick = { onNavigate(AppRoute.ProductsCatalog) }
        )

        if (!state.isLoggedIn) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "היי אורח",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { onNavigate(AppRoute.Login) }, // Adjust route
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("🔐 התחבר כדי לצפות בנתונים שלך")
                }
            }
        } else {
            ActionButton(
                icon = "📋",
                label = if (state.isDistributor) "מנהל הזמנות" else "ההזמנות שלי",
                onClick = { onNavigate(AppRoute.DemandsManger) }
            )

            if (!state.isDistributor) {
                ActionButton(
                    icon = "🛒",
                    label = "העגלה שלי",
                    floatingLabel = state.cartItemCount,
                    onClick = { onNavigate(AppRoute.Cart) }
                )
            }

            AuthActionButton(
                userName = state.name,
                onLogout =  onLogout
            )
        }
    }
}

