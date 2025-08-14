package com.cybozu.sample.kintone.spaces.feature.communicate.space

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cybozu.sample.kintone.spaces.core.design.theme.KintoneSpacesTheme
import com.cybozu.sample.kintone.spaces.data.space.entity.Thread

@Composable
fun SpaceScreen(
    onThreadClick: (Thread) -> Unit,
    viewModel: SpaceViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    SpaceContent(
        uiState = uiState,
        onThreadClick = onThreadClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceContent(
    uiState: SpaceUiState,
    onThreadClick: (Thread) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Spaces") })
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
            ) {
                items(uiState.threads) { thread ->
                    ThreadListItem(thread = thread) {
                        onThreadClick(thread)
                    }
                }
            }
        }
    }
}

@Composable
private fun ThreadListItem(
    thread: Thread,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = thread.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = thread.body ?: "本文未設定", style = MaterialTheme.typography.bodySmall)
        }
    }
}

class SpaceContentPreviewParameter :
    CollectionPreviewParameterProvider<SpaceUiState>(
        listOf(
            SpaceUiState(
                threads =
                    listOf(
                        Thread(
                            id = "1",
                            spaceId = "3",
                            name = "thread-1",
                            body = "This is a preview message snippet 1..."
                        ),
                        Thread(
                            id = "2",
                            spaceId = "3",
                            name = "thread-2",
                            body = "This is a preview message snippet 2..."
                        ),
                        Thread(
                            id = "3",
                            spaceId = "3",
                            name = "thread-2",
                            body = "This is a preview message snippet 3..."
                        )
                    ),
                isLoading = false
            ),
            SpaceUiState(
                threads = emptyList(),
                isLoading = true
            )
        )
    )

@Preview(showBackground = true)
@Composable
fun SpaceContentPreview(
    @PreviewParameter(SpaceContentPreviewParameter::class) uiState: SpaceUiState,
) {
    KintoneSpacesTheme {
        SpaceContent(
            uiState = uiState,
            onThreadClick = { }
        )
    }
}
