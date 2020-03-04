package app.jerry.drink.ext

import androidx.fragment.app.Fragment
import app.jerry.drink.DrinkApplication
import app.jerry.drink.dataclass.Drink
import app.jerry.drink.dataclass.DrinkDetail
import app.jerry.drink.dataclass.OrderLists
import app.jerry.drink.dataclass.Store
import app.jerry.drink.factory.*

fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as DrinkApplication).repository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(orderLists: OrderLists): OrderListsFactory {
    val repository = (requireContext().applicationContext as DrinkApplication).repository
    return OrderListsFactory(repository, orderLists)
}

fun Fragment.getVmFactory(drink: Drink): DrinkFactory {
    val repository = (requireContext().applicationContext as DrinkApplication).repository
    return DrinkFactory(repository, drink)
}

fun Fragment.getVmFactory(orderId: String): OrderIdFactory {
    val repository = (requireContext().applicationContext as DrinkApplication).repository
    return OrderIdFactory(repository, orderId)
}

fun Fragment.getVmFactory(store: Store): StoreFactory {
    val repository = (requireContext().applicationContext as DrinkApplication).repository
    return StoreFactory(repository, store)
}