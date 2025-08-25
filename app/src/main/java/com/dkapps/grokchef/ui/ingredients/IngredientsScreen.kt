package com.dkapps.grokchef.ui.ingredients

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dkapps.grokchef.ui.shared.Loading
import com.dkapps.grokchef.ui.theme.ApplicationTheme

@Composable
fun IngredientsScreen(
    modifier: Modifier = Modifier,
    ingredientsViewModel: IngredientsViewModel = hiltViewModel(),
) {
    // Observe the UI state from the ViewModel
    val uiState by ingredientsViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is IngredientsUiState.Loading -> Loading(
            loadingText = s.loadingMessage,
            modifier = modifier.fillMaxSize()
        )

        is IngredientsUiState.Success ->
            Scaffold(modifier = modifier.fillMaxSize()) { paddingValues ->
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
                        items(s.ingredients) { item ->
                            IngredientListItem(
                                ingredient = item,
                                onRemove = { ingredientsViewModel.removeIngredient(item) },
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }
            }

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
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier
) {

}

@Preview
@Composable
fun IngredientsContentPreview() {
    ApplicationTheme {
        IngredientsContent(
            ingredients = listOf("Onion", "Tomato", "Garlic"),
            onRemove = {}
        )
    }
}
