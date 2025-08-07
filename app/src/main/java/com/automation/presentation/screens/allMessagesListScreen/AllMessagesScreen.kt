package com.automation.presentation.screens.allMessagesListScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.automation.R
import com.automation.presentation.MainViewModel
import com.automation.presentation.screens.allMessagesListScreen.uiItems.MessagesItem
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllMessagesScreen(
    modifier: Modifier = Modifier,
    viewModel: AllMessagesViewModel = koinViewModel(),
    mainViewModel: MainViewModel
) {
    val state by viewModel.smsList.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier.statusBarsPadding()
    ) {
        CenterAlignedTopAppBar(
            modifier = Modifier,
            title = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(1f),  // Ensure text is on top if overlapping
                    contentAlignment = Alignment.Center  // Centers content in the Box
                ) {
                    Text(
                        text = stringResource(id = R.string.all_message_title),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall,
                        color = colorScheme.onBackground
                    )
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        mainViewModel.openSettingsScreen()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = colorScheme.onBackground
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorScheme.background
            )
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(state) {
                MessagesItem(state = it)
            }
        }
    }
}