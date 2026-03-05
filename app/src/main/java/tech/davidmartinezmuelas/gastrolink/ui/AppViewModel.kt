package tech.davidmartinezmuelas.gastrolink.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import tech.davidmartinezmuelas.gastrolink.data.LocalJsonRepository
import tech.davidmartinezmuelas.gastrolink.model.Branch
import tech.davidmartinezmuelas.gastrolink.model.CartItem
import tech.davidmartinezmuelas.gastrolink.model.Dish

data class AppUiState(
    val branches: List<Branch> = emptyList(),
    val selectedBranch: Branch? = null,
    val dishes: List<Dish> = emptyList(),
    val cartItems: List<CartItem> = emptyList()
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LocalJsonRepository(application)

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState

    init {
        val branches = repository.loadBranches()
        val dishes = repository.loadDishes()
        _uiState.value = _uiState.value.copy(
            branches = branches,
            dishes = dishes
        )
    }

    fun selectBranch(branch: Branch) {
        _uiState.value = _uiState.value.copy(selectedBranch = branch)
    }

    fun addDishToCart(dish: Dish) {
        val currentItems = _uiState.value.cartItems
        val existing = currentItems.find { it.dish.id == dish.id }

        val updatedItems = if (existing == null) {
            currentItems + CartItem(dish = dish, qty = 1)
        } else {
            currentItems.map {
                if (it.dish.id == dish.id) it.copy(qty = it.qty + 1) else it
            }
        }

        _uiState.value = _uiState.value.copy(cartItems = updatedItems)
    }

    fun increaseItem(dishId: String) {
        val updatedItems = _uiState.value.cartItems.map {
            if (it.dish.id == dishId) it.copy(qty = it.qty + 1) else it
        }
        _uiState.value = _uiState.value.copy(cartItems = updatedItems)
    }

    fun decreaseItem(dishId: String) {
        val updatedItems = _uiState.value.cartItems.mapNotNull {
            if (it.dish.id != dishId) return@mapNotNull it

            val newQty = it.qty - 1
            if (newQty <= 0) null else it.copy(qty = newQty)
        }

        _uiState.value = _uiState.value.copy(cartItems = updatedItems)
    }
}
