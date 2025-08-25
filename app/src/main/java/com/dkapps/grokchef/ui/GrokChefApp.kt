package com.dkapps.grokchef.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dkapps.grokchef.ui.home.HomeScreen
import com.dkapps.grokchef.ui.ingredients.IngredientsScreen
import com.dkapps.grokchef.ui.recipe.RecipeScreen

/**
 * NavHost & App routing setup used in the [GrokChefApp].
 */
@Composable
fun GrokChefApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(onNavigate = { filePath ->
                val encodedUriPath = Uri.encode(filePath)
                navController.navigate(
                    "ingredients/$encodedUriPath"
                )
            })
        }
        composable(
            "ingredients/{encodedImagePath}",
            arguments = listOf(navArgument("encodedImagePath") { type = NavType.StringType })
        ) {
            IngredientsScreen(onNavigate = { ingredients ->
                // When navigating with a List, you need to use a Bundle
                navController.navigate("recipe/$ingredients")
            })
        }
        composable(
            "recipe/{ingredientsList}",
            arguments = listOf(navArgument("ingredientsList") { type = NavType.StringType })
        ) {
            RecipeScreen()
        }
    }
}
