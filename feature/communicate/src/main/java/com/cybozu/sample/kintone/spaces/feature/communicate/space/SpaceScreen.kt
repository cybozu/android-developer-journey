package com.cybozu.sample.kintone.spaces.feature.communicate.space

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cybozu.sample.kintone.spaces.data.space.KintoneThread
import com.cybozu.sample.kintone.spaces.feature.communicate.SpaceViewModel
import com.cybozu.sample.kintone.spaces.core.design.theme.KintoneSpacesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceScreen(
    onThreadClick: (String) -> Unit,
    viewModel: SpaceViewModel = hiltViewModel()
) {
    val threads by viewModel.threads.collectAsState()

    SpaceContent(
        threads = threads,
        onThreadClick = onThreadClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceContent(
    threads: List<KintoneThread>,
    onThreadClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Spaces") })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(threads) { thread ->
                ThreadListItem(thread = thread) {
                    onThreadClick(thread.id)
                }
            }
        }
    }
}

@Composable
private fun ThreadListItem(thread: KintoneThread, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = thread.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = thread.lastMessageSnippet, style = MaterialTheme.typography.bodySmall)
        }
    }
}

private val previewThreads = listOf(
    KintoneThread("thread-1", "Preview Thread 1", "This is a preview message snippet 1..."),
    KintoneThread("thread-2", "Preview Thread 2", "This is a preview message snippet 2..."),
    KintoneThread("thread-3", "Preview Thread 3", "This is a preview message snippet 3...")
)

@Preview(showBackground = true)
@Composable
fun SpaceContentPreview() {
    KintoneSpacesTheme {
        SpaceContent(
            threads = previewThreads,
            onThreadClick = {}
        )
    }
}
