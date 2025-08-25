package com.dkapps.grokchef.ui.ingredients

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dkapps.grokchef.ui.shared.Loading

@Composable
fun IngredientsScreen(
    modifier: Modifier = Modifier,
    ingredientsViewModel: IngredientsViewModel = hiltViewModel(),
) {
    // Observe the UI state from the ViewModel
    val uiState by ingredientsViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is IngredientsUiState.Loading -> Loading(loadingText = s.loadingMessage, modifier = modifier.fillMaxSize())

        is IngredientsUiState.Success -> IngredientsContent(ingredients = s.ingredients, modifier = modifier.fillMaxSize())

        is IngredientsUiState.Error -> ErrorScreen(errorMessage = s.errorMessage, modifier = modifier.fillMaxSize())
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
fun IngredientsContent(ingredients: List<String>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ingredients.forEach { message ->
            Text(text = message, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
