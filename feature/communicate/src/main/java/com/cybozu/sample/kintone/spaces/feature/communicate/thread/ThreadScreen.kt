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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cybozu.sample.kintone.spaces.core.design.component.SystemBackNavButton
import com.cybozu.sample.kintone.spaces.feature.communicate.MessageItemData
import com.cybozu.sample.kintone.spaces.feature.communicate.sampleMessages
import com.cybozu.sample.kintone.spaces.core.design.theme.KintoneSpacesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreadScreen(threadId: String?, messages: List<MessageItemData>) {
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

@Composable
fun MessageListItem(message: MessageItemData) {
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
fun ThreadScreenPreview() {
    KintoneSpacesTheme {
        ThreadScreen(
            threadId = "thread-1",
            messages = sampleMessages.filter { it.threadId == "thread-1" }
        )
    }
}
