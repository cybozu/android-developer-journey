package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cybozu.sample.kintone.spaces.data.space.KintoneMessage
import com.cybozu.sample.kintone.spaces.feature.communicate.ThreadViewModel
import com.cybozu.sample.kintone.spaces.core.design.theme.KintoneSpacesTheme
import com.cybozu.sample.kintone.spaces.core.design.component.SystemBackNavButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreadScreen(
    threadId: String?,
    viewModel: ThreadViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()

    LaunchedEffect(threadId) {
        if (threadId != null) {
            viewModel.loadMessages(threadId)
        }
    }

    ThreadContent(
        threadId = threadId,
        messages = messages
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreadContent(
    threadId: String?,
    messages: List<KintoneMessage>
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (threadId != null) "Thread: $threadId" else "Thread not found")
                },
                navigationIcon = {
                    SystemBackNavButton()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (threadId == null) {
                Text("Thread not found.", modifier = Modifier.padding(16.dp))
                return@Column
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(messages) { message ->
                    MessageListItem(message = message)
                }
            }
        }
    }
}

private val previewMessages = listOf(
    KintoneMessage("msg-1", "thread-1", "Preview User 1", "", "This is a preview message 1"),
    KintoneMessage("msg-2", "thread-1", "Preview User 2", "", "This is a preview message 2"),
    KintoneMessage("msg-3", "thread-1", "Preview User 3", "", "This is a preview message 3")
)

@Composable
private fun MessageListItem(message: KintoneMessage) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "${message.userName}'s icon",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = message.userName, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = message.content, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ThreadContentPreview() {
    KintoneSpacesTheme {
        ThreadContent(
            threadId = "thread-1",
            messages = previewMessages
        )
    }
}
