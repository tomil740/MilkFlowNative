package com.tomiappdevelopment.milk_flow.presentation.core

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import network.chaintech.ui.datepicker.WheelPicker



@Composable
fun NumberWheelPicker(
    modifier: Modifier = Modifier,
    start: Int,
    endInclusive: Int,
    selectedNumber: Int,
    rowCount: Int = 5,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    color: Color = LocalContentColor.current,
    onValueChange: (Int) -> Unit
) {
    val numbers = (start..endInclusive).toList()
    val texts = numbers.map { it.toString() }
    val selectedIndex = numbers.indexOf(selectedNumber).coerceAtLeast(0)

    WheelPicker(
        modifier = modifier,
        startIndex = selectedIndex,
        count = numbers.size,
        rowCount = rowCount,
        texts = texts,
        style = style,
        color = color,
        contentAlignment = Alignment.Center,
        onScrollFinished = { snappedIndex ->
            onValueChange(numbers[snappedIndex])
            null // if you don't want the picker to auto-correct
        }
    )
}