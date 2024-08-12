package pl.p24.sdk.demo

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.Main.route) {
        composable(route = Routes.Main.route) {
            MainScreen(navController = navController)
        }
        composable(route = Routes.ChargeCard.route) {
            ChargeCardScreen(navController = navController)
        }
        composable(route = Routes.TokenizeCard.route) {
            TokenizeCardScreen(navController = navController)
        }
    }
}