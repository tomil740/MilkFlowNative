package com.tomiappdevelopment.milk_flow.presentation.core

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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.tomiappdevelopment.milk_flow.NavigationManager
import com.tomiappdevelopment.milk_flow.presentation.LoginScreen.LoginScreenClass
import com.tomiappdevelopment.milk_flow.presentation.productCatalog.components.util.ActionButton

@Composable
fun TopBar(
    isLoggedIn: Boolean,
    isDistributor: Boolean,
    cartItemCount: Int = 0,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 8.dp)
        ,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ActionButton(
            icon = "🛍️",
            label = "כל המוצרים",
            onClick = { onNavigate("/") }
        )

        if (!isLoggedIn) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "היי אורח",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { NavigationManager.navigateTo(LoginScreenClass()) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("🔐 התחבר כדי לצפות בנתונים שלך")
                }
            }
        } else {
            ActionButton(
                icon = "📋",
                label = if (isDistributor) "מנהל הזמנות" else "ההזמנות שלי",
                onClick = { onNavigate("/demandsView") }
            )

            if (!isDistributor) {
                ActionButton(
                    icon = "🛒",
                    label = "העגלה שלי",
                    floatingLabel = cartItemCount,
                    onClick = { onNavigate("/cart") }
                )
            }

            ActionButton(
                icon = "🚪",
                label = "התנתקות",
                onClick = onLogout
            )
        }
    }
}
