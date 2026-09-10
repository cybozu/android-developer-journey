package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cybozu.sample.kintone.spaces.core.design.component.Html
import com.cybozu.sample.kintone.spaces.core.design.component.SystemBackNavButton
import com.cybozu.sample.kintone.spaces.core.design.theme.KintoneSpacesTheme
import com.cybozu.sample.kintone.spaces.data.space.entity.Comment
import com.cybozu.sample.kintone.spaces.data.space.entity.Creator
import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessage
import com.cybozu.sample.kintone.spaces.feature.communicate.R

@Composable
fun ThreadScreen(
    spaceId: String,
    threadId: String,
    threadName: String,
    viewModel: ThreadViewModel =
        hiltViewModel(
            creationCallback = { factory: ThreadViewModelFactory ->
                factory.create(spaceId = spaceId, threadId = threadId)
            }
        ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage = stringResource(R.string.error_load_messages)
    val retryActionLabel = stringResource(R.string.action_retry)
    val sendErrorMessage = stringResource(R.string.error_send_message)

    LaunchedEffect(uiState.errorMessageSeq) {
        if (uiState.errorMessageSeq != null) {
            val result =
                snackbarHostState.showSnackbar(
                    errorMessage,
                    actionLabel = retryActionLabel
                )
            viewModel.onErrorMessageShown()
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.onRetryClick()
            }
        }
    }

    LaunchedEffect(uiState.sendErrorMessageSeq) {
        if (uiState.sendErrorMessageSeq != null) {
            snackbarHostState.showSnackbar(sendErrorMessage)
            viewModel.onSendErrorMessageShown()
        }
    }

    ThreadContent(
        threadName = threadName,
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onRefresh = viewModel::onRefresh,
        onComposeClick = viewModel::onComposeClick,
        onCloseComposeClick = viewModel::onCloseComposeClick,
        onInputTextChange = viewModel::onInputTextChange,
        onSendClick = viewModel::onSendClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreadContent(
    threadName: String,
    uiState: ThreadUiState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onRefresh: () -> Unit = {},
    onComposeClick: () -> Unit = {},
    onCloseComposeClick: () -> Unit = {},
    onInputTextChange: (String) -> Unit = {},
    onSendClick: () -> Unit = {},
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (!uiState.isComposerOpen) {
                FloatingActionButton(
                    onClick = onComposeClick,
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "メッセージを作成")
                }
            }
        },
        bottomBar = {
            if (uiState.isComposerOpen) {
                MessageComposer(
                    inputText = uiState.inputText,
                    isSending = uiState.isSending,
                    onInputTextChange = onInputTextChange,
                    onSendClick = onSendClick,
                    onCloseClick = onCloseComposeClick
                )
            }
        }
    ) { innerPadding ->
        val contentModifier = Modifier.fillMaxSize().padding(innerPadding)
        if (uiState.isLoading) {
            Box(
                modifier = contentModifier,
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefresh,
                modifier = contentModifier
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(all = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.threadMessages) { threadMessage ->
                        MessageListItem(threadMessage = threadMessage)
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageComposer(
    inputText: String,
    isSending: Boolean,
    onInputTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onCloseClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onCloseClick, enabled = !isSending) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = "閉じる")
        }
        TextField(
            value = inputText,
            onValueChange = onInputTextChange,
            modifier = Modifier.weight(1f).heightIn(min = 84.dp),
            enabled = !isSending,
            placeholder = { Text("メッセージを入力") }
        )
        IconButton(
            onClick = onSendClick,
            enabled = inputText.isNotBlank() && !isSending
        ) {
            if (isSending) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "送信")
            }
        }
    }
}

@Composable
private fun MessageListItem(threadMessage: ThreadMessage) {
    Column {
        MessageCard(
            creatorName = threadMessage.creator.name,
            body = threadMessage.body
        )

        threadMessage.comments.forEach { comment ->
            MessageCard(
                creatorName = comment.creator.name,
                body = comment.body,
                modifier = Modifier.padding(start = 40.dp, top = 8.dp),
                nameStyle = MaterialTheme.typography.labelMedium,
                bodyStyle = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun MessageCard(
    creatorName: String,
    body: String,
    modifier: Modifier = Modifier,
    nameStyle: TextStyle = MaterialTheme.typography.titleSmall,
    bodyStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "$creatorName's icon",
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = creatorName,
                    style = nameStyle
                )
                Spacer(modifier = Modifier.height(4.dp))
                Html(
                    htmlString = body,
                    style = bodyStyle
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
                            creator = Creator(name = "name1"),
                            comments = emptyList()
                        ),
                        ThreadMessage(
                            id = "2",
                            body = "plain text with comments",
                            creator = Creator(name = "name3"),
                            comments =
                                listOf(
                                    Comment(
                                        id = "1",
                                        body = "comment 1",
                                        creator = Creator(name = "commenter1")
                                    ),
                                    Comment(
                                        id = "2",
                                        body = "comment 2",
                                        creator = Creator(name = "commenter2")
                                    )
                                )
                        ),
                        ThreadMessage(
                            id = "3",
                            body =
                                """
                                <h1>Jetpack Compose</h1>
                                <p>
                                    Build <b>better apps</b> faster with <a href="https://www.android.com">Jetpack Compose</a>
                                </p> 
                                """.trimIndent(),
                            creator = Creator(name = "name2"),
                            comments = emptyList()
                        )
                    ),
                isLoading = false
            ),
            ThreadUiState(
                threadMessages = emptyList(),
                isLoading = true
            ),
            ThreadUiState(
                threadMessages =
                    listOf(
                        ThreadMessage(
                            id = "1",
                            body = "plain text",
                            creator = Creator(name = "name1"),
                            comments = emptyList()
                        )
                    ),
                isLoading = false,
                isRefreshing = true
            ),
            ThreadUiState(
                threadMessages =
                    listOf(
                        ThreadMessage(
                            id = "1",
                            body = "plain text",
                            creator = Creator(name = "name1"),
                            comments = emptyList()
                        )
                    ),
                isLoading = false,
                isComposerOpen = true,
                inputText = "こんにちは"
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
