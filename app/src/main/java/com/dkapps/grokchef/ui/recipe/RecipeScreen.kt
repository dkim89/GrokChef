package com.dkapps.grokchef.ui.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dkapps.grokchef.ui.shared.Loading
import com.dkapps.grokchef.ui.theme.ApplicationTheme

@Composable
fun RecipeScreen(
    modifier: Modifier = Modifier,
    recipeViewModel: RecipeViewModel = hiltViewModel(),
) {
    // Observe the UI state from the ViewModel
    val uiState by recipeViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is RecipeUiState.Loading -> Loading(
            loadingText = s.loadingMessage,
            modifier = modifier.fillMaxSize()
        )

        is RecipeUiState.Success -> {
            Recipe(s.recipe)
        }

        is RecipeUiState.Error -> {
            ErrorScreen(s.errorMessage)
        }
    }
}

@Composable
fun Recipe(recipe: String, modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                AnnotatedString.fromHtml(recipe), modifier = Modifier.padding(8.dp)
            )
        }
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

@Preview
@Composable
fun RecipePreview() {
    ApplicationTheme {
        Recipe(
            recipe =
                "<h1>Veggie Frittata Recipe</h1>\n" +
                        "\n" +
                        "<p>This delicious veggie frittata is a perfect breakfast or brunch dish, packed with fresh vegetables and cheesy goodness. It serves 4 people and takes about 30 minutes to prepare and cook.</p>\n" +
                        "\n" +
                        "<h2>Ingredients</h2>\n" +
                        "<ul>\n" +
                        "    <li>1 cup milk</li>\n" +
                        "    <li>6 eggs</li>\n" +
                        "    <li>2 tablespoons butter</li>\n" +
                        "    <li>1 cup shredded cheese (e.g., cheddar or mozzarella)</li>\n" +
                        "    <li>2 cups fresh spinach, chopped</li>\n" +
                        "    <li>2 tomatoes, diced</li>\n" +
                        "    <li>1 cup mushrooms, sliced</li>\n" +
                        "    <li>1 onion, finely chopped</li>\n" +
                        "    <li>2 cloves garlic, minced</li>\n" +
                        "    <li>1 bell pepper, diced (any color)</li>\n" +
                        "    <li>Salt, to taste</li>\n" +
                        "    <li>Pepper, to taste</li>\n" +
                        "    <li>2 tablespoons olive oil</li>\n" +
                        "</ul>\n" +
                        "\n" +
                        "<h2>Instructions</h2>\n" +
                        "<ol>\n" +
                        "    <li>Preheat your oven to 375°F (190°C).</li>\n" +
                        "    <li>In a large skillet, heat the olive oil and 1 tablespoon of butter over medium heat. Add the chopped onion, minced garlic, diced bell pepper, and sliced mushrooms. Sauté for 5 minutes until softened.</li>\n" +
                        "    <li>Add the chopped spinach and diced tomatoes to the skillet. Cook for another 2-3 minutes until the spinach wilts. Season with salt and pepper to taste. Remove from heat.</li>\n" +
                        "    <li>In a bowl, whisk together the eggs and milk. Add half of the shredded cheese, and season with a pinch of salt and pepper.</li>\n" +
                        "    <li>Pour the egg mixture over the sautéed vegetables in the skillet (ensure it's oven-safe). Stir gently to combine.</li>\n" +
                        "    <li>Sprinkle the remaining cheese on top and dot with the remaining tablespoon of butter.</li>\n" +
                        "    <li>Transfer the skillet to the preheated oven and bake for 15-20 minutes, or until the frittata is set and golden on top.</li>\n" +
                        "    <li>Let it cool for a few minutes, then slice and serve warm. Enjoy!</li>\n" +
                        "</ol>\n" +
                        "\n" +
                        "<p><em>Tip: You can customize this recipe by adding herbs like basil for extra flavor.</em></p>"
        )

    }
}