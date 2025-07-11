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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cybozu.sample.kintone.spaces.feature.communicate.ThreadItemData
import com.cybozu.sample.kintone.spaces.feature.communicate.sampleThreads
import com.cybozu.sample.kintone.spaces.core.design.theme.KintoneSpacesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceScreen(
    threads: List<ThreadItemData>,
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
fun ThreadListItem(thread: ThreadItemData, onClick: () -> Unit) {
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

@Preview(showBackground = true)
@Composable
fun SpaceScreenPreview() {
    KintoneSpacesTheme {
        SpaceScreen(
            threads = sampleThreads,
            onThreadClick = {}
        )
    }
}
