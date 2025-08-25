package com.dkapps.grokchef.ui.ingredients

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.dkapps.grokchef.ui.theme.ApplicationTheme

@Composable
fun IngredientListItem(
    ingredient: String,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val swipeState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        modifier = modifier,
        state = swipeState,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = when (swipeState.targetValue) {
                    SwipeToDismissBoxValue.Settled -> Color.LightGray
                    SwipeToDismissBoxValue.StartToEnd -> Color.Red
                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                },
                label = "swipe"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
            )
        }) {
        ListItem(
            headlineContent = {
                Text(
                    text = ingredient,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        )
    }

    when (swipeState.currentValue) {
        SwipeToDismissBoxValue.EndToStart -> {
            LaunchedEffect(swipeState.currentValue) {
                onRemove(ingredient)
                swipeState.reset()
            }
        }

        SwipeToDismissBoxValue.StartToEnd -> {
            LaunchedEffect(swipeState.currentValue) {
                onRemove(ingredient)
                swipeState.reset()
            }
        }

        SwipeToDismissBoxValue.Settled -> {}
    }
}

@Preview
@Composable
fun LoadingPreview() {
    ApplicationTheme {
        IngredientListItem(
            "Onion",
            onRemove = {}
        )
    }
}