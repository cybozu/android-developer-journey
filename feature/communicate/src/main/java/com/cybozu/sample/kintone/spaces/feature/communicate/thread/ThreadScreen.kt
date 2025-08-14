package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cybozu.sample.kintone.spaces.core.design.component.Html
import com.cybozu.sample.kintone.spaces.core.design.component.SystemBackNavButton
import com.cybozu.sample.kintone.spaces.core.design.theme.KintoneSpacesTheme
import com.cybozu.sample.kintone.spaces.data.space.entity.Creator
import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessage

@Composable
fun ThreadScreen(
    threadId: String,
    threadName: String,
    viewModel: ThreadViewModel =
        hiltViewModel(
            creationCallback = { factory: ThreadViewModelFactory ->
                factory.create(threadId)
            }
        ),
) {
    val uiState by viewModel.uiState.collectAsState()

    ThreadContent(
        threadName = threadName,
        uiState = uiState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreadContent(
    threadName: String,
    uiState: ThreadUiState,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(threadName)
                },
                navigationIcon = {
                    SystemBackNavButton()
                }
            )
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
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                items(uiState.threadMessages) { threadMessage ->
                    MessageListItem(threadMessage = threadMessage)
                }
            }
        }
    }
}

@Composable
private fun MessageListItem(threadMessage: ThreadMessage) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "${threadMessage.creator.name}'s icon",
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = threadMessage.creator.name, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Html(
                    htmlString = threadMessage.body,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

class ThreadContentPreviewParameter :
    CollectionPreviewParameterProvider<ThreadUiState>(
        listOf(
            ThreadUiState(
                threadMessages =
                    listOf(
                        ThreadMessage(
                            id = "1",
                            body = "plain text",
                            creator = Creator(name = "name1")
                        ),
                        ThreadMessage(
                            id = "2",
                            body =
                                """
                                <h1>Jetpack Compose</h1>
                                <p>
                                    Build <b>better apps</b> faster with <a href="https://www.android.com">Jetpack Compose</a>
                                </p> 
                                """.trimIndent(),
                            creator = Creator(name = "name2")
                        )
                    ),
                isLoading = false
            ),
            ThreadUiState(
                threadMessages = emptyList(),
                isLoading = true
            )
        )
    )

@Preview(showBackground = true)
@Composable
fun ThreadContentPreview(
    @PreviewParameter(ThreadContentPreviewParameter::class) uiState: ThreadUiState,
) {
    KintoneSpacesTheme {
        ThreadContent(
            threadName = "Sample Thread",
            uiState = uiState
        )
    }
}
