package app.jerry.drink.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.jerry.drink.dataclass.OrderLists
import app.jerry.drink.dataclass.source.DrinkRepository
import app.jerry.drink.order.addorder.AddOrderViewModel

@Suppress("UNCHECKED_CAST")
class OrderListsFactory(
    private val repository: DrinkRepository,
    private val orderLists: OrderLists
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(AddOrderViewModel::class.java)) {
            return AddOrderViewModel(repository, orderLists) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}