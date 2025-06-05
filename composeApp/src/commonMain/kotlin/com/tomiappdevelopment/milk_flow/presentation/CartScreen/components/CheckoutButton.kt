package com.tomiappdevelopment.milk_flow.presentation.CartScreen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CheckoutButton(
    loading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Button(
            onClick = onClick,
            enabled = !loading,
            modifier = Modifier
                .align(Alignment.Center)
                .width(240.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (loading) {
                Text("מבצע...")
            } else {
                Text("בצע הזמנה")
            }
        }
    }
}
