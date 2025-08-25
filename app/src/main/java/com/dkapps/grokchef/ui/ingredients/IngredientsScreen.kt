package com.dkapps.grokchef.ui.ingredients

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dkapps.grokchef.R
import com.dkapps.grokchef.ui.shared.Loading
import com.dkapps.grokchef.ui.theme.ApplicationTheme

@Composable
fun IngredientsScreen(
    modifier: Modifier = Modifier,
    ingredientsViewModel: IngredientsViewModel = hiltViewModel(),
    onNavigate: (List<String>) -> Unit
) {
    // Observe the UI state from the ViewModel
    val uiState by ingredientsViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is IngredientsUiState.Loading -> Loading(
            loadingText = s.loadingMessage,
            modifier = modifier.fillMaxSize()
        )

        is IngredientsUiState.Success -> IngredientsContent(
            s.ingredients,
            ingredientsViewModel::removeIngredient,
            onClickRecipeFab = onNavigate
        )

        is IngredientsUiState.Error -> ErrorScreen(
            errorMessage = s.errorMessage,
            modifier = modifier.fillMaxSize()
        )
    }
}

@Composable
fun ErrorScreen(errorMessage: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
fun IngredientsContent(
    ingredients: List<String>,
    onRemoveItem: (String) -> Unit,
    onClickRecipeFab: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize(),
        floatingActionButton = {
        if (ingredients.isNotEmpty()) {
            RecipeButton(onClick = { onClickRecipeFab(ingredients) })
        }
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "Ingredients:",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
            Spacer(Modifier.padding(bottom = 8.dp))
            LazyColumn(modifier = modifier) {
                items(ingredients) { item ->
                    IngredientListItem(
                        ingredient = item,
                        onRemove = { onRemoveItem(item) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick, modifier = modifier) {
        Image(
            painter = painterResource(R.drawable.chef_hat),
            contentDescription = stringResource(R.string.recipe_button)
        )
    }
}

@Preview
@Composable
fun IngredientsContentPreview() {
    ApplicationTheme {
        IngredientsContent(
            ingredients = listOf("Onion", "Tomato", "Garlic"),
            onRemoveItem = {},
            onClickRecipeFab = {}
        )
    }
}
