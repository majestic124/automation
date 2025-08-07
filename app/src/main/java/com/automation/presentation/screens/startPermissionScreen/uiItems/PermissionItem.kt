package com.automation.presentation.screens.startPermissionScreen.uiItems

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PermissionItem(
    modifier: Modifier = Modifier,
    titleText: String = "",
    subtitleText: String = "",
    isChecked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = titleText,
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme.onBackground
            )
            Text(
                text = subtitleText,
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorScheme.onPrimaryContainer,
                checkedTrackColor = colorScheme.primaryContainer,
                checkedBorderColor = colorScheme.outline,
                uncheckedThumbColor = colorScheme.onPrimaryContainer,
                uncheckedTrackColor = colorScheme.outline,
                uncheckedBorderColor = colorScheme.outline,
            )
        )
    }
}