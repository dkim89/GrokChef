package com.dkapps.grokchef.ui.ingredients

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dkapps.grokchef.ui.theme.ApplicationTheme

@Composable
fun IngredientListItem(
    ingredient: String,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when(it) {
                SwipeToDismissBoxValue.StartToEnd,
                SwipeToDismissBoxValue.EndToStart -> onRemove(ingredient)
                SwipeToDismissBoxValue.Settled -> return@rememberSwipeToDismissBoxState false
            }
            return@rememberSwipeToDismissBoxState true
        }
    )

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        modifier = modifier.fillMaxSize(),
        backgroundContent = {
            when (swipeToDismissBoxState.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> DeleteIcon(Alignment.CenterStart)
                SwipeToDismissBoxValue.EndToStart -> DeleteIcon(Alignment.CenterEnd)
                SwipeToDismissBoxValue.Settled -> {}
            }
        }
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = ingredient,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        )
    }
}

@Composable
fun DeleteIcon(alignment: Alignment) {
    Icon(
        imageVector = Icons.Default.Delete,
        contentDescription = "Remove item",
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red)
            .wrapContentSize(alignment)
            .padding(12.dp)
    )
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