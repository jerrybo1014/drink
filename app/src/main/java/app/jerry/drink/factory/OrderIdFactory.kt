package app.jerry.drink.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.jerry.drink.dataclass.source.DrinkRepository
import app.jerry.drink.order.OrderViewModel

@Suppress("UNCHECKED_CAST")
class OrderIdFactory(
    private val repository: DrinkRepository,
    private val orderId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            return OrderViewModel(repository, orderId) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}