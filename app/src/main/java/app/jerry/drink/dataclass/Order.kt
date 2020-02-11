package app.jerry.drink.dataclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order(
    var id: String ="",
    var user: User? = null,
    var store: Store? = null,
    var createdTime: Long = 0,
    var orderId: Long = 0,
    var timeLimit: String? ="",
    var note: String = "",
    var status: Boolean = true
): Parcelable

@Parcelize
data class OrderList(
    var id: String ="",
    var user: User? = null,
    var drink: Drink? = null,
    var ice: String? = "",
    var sugar: String? = "",
    var qty: Long? = 1,
    var note: String = ""
): Parcelable{
    fun displayQty(): String{
        return "X$qty"
    }

    fun displayIceSugar(): String{
        return "$sugar$ice"
    }

}

@Parcelize
data class OrderLists(
    var order: Order? = null,
    var orderLists: List<OrderList>? = listOf()
): Parcelable
