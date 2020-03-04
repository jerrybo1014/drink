package app.jerry.drink.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.jerry.drink.dataclass.Order
import app.jerry.drink.dataclass.source.DrinkRepository
import app.jerry.drink.order.addorder.AddOrderViewModel

@Suppress("UNCHECKED_CAST")
class OrderFactory(
    private val repository: DrinkRepository,
    private val order: Order
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(AddOrderViewModel::class.java)) {
            return AddOrderViewModel(repository, order) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}