package app.jerry.drink.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.jerry.drink.dataclass.DrinkDetail
import app.jerry.drink.dataclass.source.DrinkRepository
import app.jerry.drink.detail.DetailViewModel
import app.jerry.drink.order.OrderVIewModel

@Suppress("UNCHECKED_CAST")
class OrderIdFactory(
    private val repository: DrinkRepository,
    private val orderId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(OrderVIewModel::class.java)) {
            return OrderVIewModel(repository, orderId) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}