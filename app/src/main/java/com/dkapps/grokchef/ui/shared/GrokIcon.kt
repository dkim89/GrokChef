package com.dkapps.grokchef.ui.shared

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.dkapps.grokchef.R

@Composable
fun GrokIcon(
    modifier: Modifier, tint: Color = Color.Unspecified
) {
    val iconPainter = if (isSystemInDarkTheme()) { // Changed imageVector to painter
        painterResource(id = R.drawable.grok_background_light)
    } else {
        painterResource(id = R.drawable.grok_background_dark)
    }
    Icon(
        painter = iconPainter,
        contentDescription = stringResource(R.string.grok_icon_desc),
        modifier = modifier,
        tint = tint
    )
}