package com.tomiappdevelopment.milk_flow.core.presentation

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

sealed class UiText {
    data class DynamicString(val value: String): UiText()
    class StringResource @OptIn(ExperimentalResourceApi::class) constructor(
        val resId: org.jetbrains.compose.resources.StringResource,
        vararg val args: Any
    ): UiText()
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(resId,*args)
        }
    }
    @OptIn(ExperimentalResourceApi::class)
    suspend fun asString2():String{
        return when (this) {
            is DynamicString -> value
            is StringResource -> getString(resId,*args)
        }
    }

}