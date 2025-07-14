package com.tomiappdevelopment.milk_flow.presentation.LoginScreen

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.presentation.core.components.LoadingSpinner
import com.tomiappdevelopment.milk_flow.presentation.core.components.LoginLoadingSplash
import com.tomiappdevelopment.milk_flow.presentation.productCatalog.ProductCatalogScreenClass
import kotlinx.coroutines.delay
import milkflow.composeapp.generated.resources.Res
import milkflow.composeapp.generated.resources.dialog_login_success_button
import milkflow.composeapp.generated.resources.dialog_login_success_message
import milkflow.composeapp.generated.resources.dialog_login_success_title
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LoginScreen(
    stateAndEvents: LoginStatesAndEvents
) {
    val uiState = stateAndEvents.uiState

    val focusManager = LocalFocusManager.current

    val navigator = LocalNavigator.currentOrThrow

    var triggerNav by remember { mutableStateOf(false) }

    // LaunchedEffect to delay + navigate
    if (triggerNav) {
        LaunchedEffect(Unit) {

            delay(1500L)
            navigator.replaceAll(ProductCatalogScreenClass())
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "התחברות",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = uiState.phoneNumber,
                onValueChange = stateAndEvents.onPhoneChange,
                label = { Text("מספר טלפון") },
                isError = uiState.phoneNumberError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            uiState.phoneNumberError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            uiState.errorMessage?.let {
                Text(
                    text = "שגיאה: $it",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Spacer(Modifier.height(56.dp))

            Button(
                onClick = stateAndEvents.onLogin,
                enabled = !uiState.isLoading && uiState.isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("מתחבר...")
                } else {
                    Text("התחבר")
                }
            }
        }
        LoginLoadingSplash(isVisible = uiState.isLoading)

        if (uiState.showSuccessDialog && (!triggerNav)) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    Button(
                        onClick = {
                            triggerNav = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = UiText.StringResource(Res.string.dialog_login_success_button).asString(),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
                ,
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                title = {
                    Text(
                        text =  UiText.StringResource(Res.string.dialog_login_success_title).asString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Text(
                        text = UiText.StringResource(Res.string.dialog_login_success_message).asString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            )

        }

        LoadingSpinner(triggerNav)

    }
}
