package com.dkapps.grokchef.ui.ingredients

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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

    if (uiState.isLoading) {
        Loading(modifier)
    } else if (uiState.ingredients.isNotEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            uiState.ingredients.forEach { message ->
                Text(text = message)
            }
        }
    } else if (uiState.errorMessages.isNotEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            uiState.errorMessages.forEach { message ->
                Text(text = message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
