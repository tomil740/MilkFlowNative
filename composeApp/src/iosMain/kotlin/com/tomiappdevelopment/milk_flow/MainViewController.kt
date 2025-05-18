package com.tomiappdevelopment.milk_flow

import androidx.compose.ui.window.ComposeUIViewController
import com.tomiappdevelopment.milk_flow.di.initKoin


fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    App()
}