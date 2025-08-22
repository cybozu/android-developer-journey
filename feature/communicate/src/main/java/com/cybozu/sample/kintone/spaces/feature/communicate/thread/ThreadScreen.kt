package com.cybozu.sample.kintone.spaces.feature.communicate.thread

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.cybozu.sample.kintone.spaces.core.design.component.Html
import com.cybozu.sample.kintone.spaces.core.design.component.SystemBackNavButton
import com.cybozu.sample.kintone.spaces.core.design.theme.KintoneSpacesTheme
import com.cybozu.sample.kintone.spaces.data.space.entity.Comment
import com.cybozu.sample.kintone.spaces.data.space.entity.Creator
import com.cybozu.sample.kintone.spaces.data.space.entity.ThreadMessage
import com.cybozu.sample.kintone.spaces.feature.communicate.R

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
        uiState = uiState,
        onRefresh = viewModel::refreshMessages,
        onChanged = viewModel::onTextChanged,
        onTapActionButton = viewModel::openDialog,
        onSubmit = viewModel::sendComment,
        onDismissRequest = viewModel::closeDialog
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreadContent(
    threadName: String,
    uiState: ThreadUiState,
    onRefresh: () -> Unit,
    onChanged: (String) -> Unit,
    onTapActionButton: () -> Unit,
    onSubmit: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    val pullToRefreshState = rememberPullToRefreshState()

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
        floatingActionButton = {
            if (uiState is ThreadUiState.Success) {
                FloatingActionButton(
                    onClick = onTapActionButton
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "a")
                }
            }
        }
    ) { innerPadding ->
        val isRefreshing = uiState is ThreadUiState.Refreshing
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = pullToRefreshState,
            modifier = Modifier.padding(innerPadding)
        ) {
            when (uiState) {
                is ThreadUiState.Idle -> {}
                is ThreadUiState.Error -> {
                    Toast.makeText(context, stringResource(uiState.messageId), Toast.LENGTH_SHORT).show()
                }
                is ThreadUiState.Loading -> {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ThreadUiState.Success -> {
                    MessageList(
                        threadMessages = uiState.threadMessages,
                        modifier =
                            Modifier
                                .fillMaxSize()
                    )
                    if (uiState.isDialogVisible) {
                        InputDialog(
                            inputText = uiState.inputText,
                            onChanged = onChanged,
                            onSubmit = onSubmit,
                            onDismissRequest = onDismissRequest,
                            isPostError = uiState.isPostError,
                            postErrorMessageId = uiState.postErrorMessageId
                        )
                    }
                }
                is ThreadUiState.Refreshing -> {
                    MessageList(
                        threadMessages = uiState.threadMessages,
                        modifier =
                            Modifier
                                .fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun InputDialog(
    inputText: String,
    onChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismissRequest: () -> Unit,
    isPostError: Boolean,
    postErrorMessageId: Int? = null,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties =
            DialogProperties()
    ) {
        Card(
            modifier =
                Modifier
                    .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = stringResource(R.string.comment_dialog),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = inputText,
                    minLines = 5,
                    maxLines = 5,
                    onValueChange = onChanged,
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                        ),
                    isError = isPostError
                )
                Box(
                    modifier = Modifier.height(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    postErrorMessageId?.let {
                        Text(
                            text = stringResource(postErrorMessageId),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Button(
                        onClick = onSubmit,
                        modifier =
                            Modifier
                                .padding(end = 16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.submit_comment)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageList(
    threadMessages: List<ThreadMessage>,
    modifier: Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(threadMessages) { threadMessage ->
            MessageListItem(
                threadMessage = threadMessage
            )
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
        modifier =
            modifier
                .fillMaxWidth()
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
            ThreadUiState.Success(
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
                    )
            ),
            ThreadUiState.Loading,
            ThreadUiState.Error(
                messageId = R.string.thread_error
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
            uiState = uiState,
            onRefresh = {},
            onChanged = {},
            onTapActionButton = {},
            onSubmit = {},
            onDismissRequest = {}
        )
    }
}

class DialogPreviewParameter :
    CollectionPreviewParameterProvider<Pair<Boolean, Int?>>(
        listOf(
            Pair(false, null),
            Pair(true, R.string.post_io_exception)
        )
    )

@Preview
@Composable
fun DialogPreview(
    @PreviewParameter(DialogPreviewParameter::class) isPostError: Boolean,
) {
    KintoneSpacesTheme {
        InputDialog(
            inputText = "text",
            onChanged = {},
            onSubmit = {},
            onDismissRequest = {},
            isPostError = isPostError
        )
    }
}
