package com.cybozu.sample.kintone.spaces.feature.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cybozu.sample.kintone.spaces.core.design.theme.KintoneSpacesTheme

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
){
  val uiState by viewModel.uiState.collectAsState()

    LoginContent(
        uiState = uiState,
        onUserNameChanged = viewModel::onInputUserName,
        onPasswordChanged = viewModel::onInputPassword,
        onLoginClick = {
            viewModel.onLoginClick()
            onLoginSuccess()
        },
        modifier = Modifier,
    )
}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    onUserNameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
){
    Column(modifier = modifier) {
        TextField(
            value = uiState.userName,
            onValueChange = onUserNameChanged,
        )

        Spacer(modifier = Modifier.height(4.dp))

        TextField(
            value = uiState.password,
            onValueChange = onPasswordChanged,
            visualTransformation = PasswordVisualTransformation(),
        )

        Button(
            onClick = onLoginClick
        ){
            Text("ログイン")
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview(
    // TODO ゆくゆくはパラメータで実装
    // @PreviewParameter(LoginContentPreviewParameter::class) uiState: LoginUiState
){
    KintoneSpacesTheme {
        LoginContent(
            onLoginClick = {},
            onPasswordChanged = {},
            onUserNameChanged = {},
            uiState = LoginUiState()
        )
    }
}