package com.cybozu.sample.kintone.spaces.core.design.component

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun Html(
    htmlString: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
) {
    Text(
        modifier = modifier,
        text =
            AnnotatedString.fromHtml(
                htmlString = htmlString,
                linkStyles =
                    TextLinkStyles(
                        style =
                            SpanStyle(
                                textDecoration = TextDecoration.Underline,
                                fontStyle = FontStyle.Italic,
                                color = Color.Blue
                            )
                    )
            ),
        style = style
    )
}
