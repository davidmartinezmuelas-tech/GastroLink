package tech.davidmartinezmuelas.gastrolink.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import tech.davidmartinezmuelas.gastrolink.domain.NutritionCalculator
import tech.davidmartinezmuelas.gastrolink.ui.AppViewModel
import tech.davidmartinezmuelas.gastrolink.ui.screens.BranchScreen
import tech.davidmartinezmuelas.gastrolink.ui.screens.CartScreen
import tech.davidmartinezmuelas.gastrolink.ui.screens.MenuScreen
import tech.davidmartinezmuelas.gastrolink.ui.screens.SummaryScreen

object AppRoute {
    const val BRANCH = "branch"
    const val MENU = "menu"
    const val CART = "cart"
    const val SUMMARY = "summary"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = AppRoute.BRANCH,
        modifier = modifier
    ) {
        composable(AppRoute.BRANCH) {
            BranchScreen(
                branches = state.branches,
                onSelectBranch = {
                    viewModel.selectBranch(it)
                    navController.navigate(AppRoute.MENU)
                }
            )
        }

        composable(AppRoute.MENU) {
            MenuScreen(
                selectedBranchName = state.selectedBranch?.name,
                dishes = state.dishes,
                onAddDish = viewModel::addDishToCart,
                onGoToCart = { navController.navigate(AppRoute.CART) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoute.CART) {
            CartScreen(
                items = state.cartItems,
                onIncrease = { viewModel.increaseItem(it) },
                onDecrease = { viewModel.decreaseItem(it) },
                onGoToSummary = { navController.navigate(AppRoute.SUMMARY) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoute.SUMMARY) {
            val totals = NutritionCalculator.calculateTotals(state.cartItems)
            val recommendations = NutritionCalculator.recommendations(totals)

            SummaryScreen(
                totals = totals,
                recommendations = recommendations,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
