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
import com.dkapps.grokchef.ui.theme.ApplicationTheme

/**
 * Destinations used in the [GrokChefApp].
 */
object Screens {
    const val HOME_ROUTE = "home"
    const val INGREDIENTS_ROUTE = "ingredients"
    const val RECIPE_ROUTE = "recipe"
}

@Composable
fun GrokChefApp() {
    ApplicationTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Screens.HOME_ROUTE) {
            composable(Screens.HOME_ROUTE) {
                HomeScreen(onNavigate = { filePath ->
                    val encodedUriPath = Uri.encode(filePath)
                    navController.navigate(
                        "${Screens.INGREDIENTS_ROUTE}/$encodedUriPath"
                    )
                })
            }
            composable(
                "${Screens.INGREDIENTS_ROUTE}/{encodedImagePath}",
                arguments = listOf(navArgument("encodedImagePath") { type = NavType.StringType })
            ) {
                IngredientsScreen()
            }
        }
    }
}
