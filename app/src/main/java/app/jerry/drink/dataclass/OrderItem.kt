package app.jerry.drink.dataclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderItem(
    var id: String = "",
    var userId: String = "",
    var user: User? = null,
    var drink: Drink? = null,
    var ice: String? = "",
    var sugar: String? = "",
    var qty: Long? = 1,
    var note: String = ""
) : Parcelable {

    fun displayQty(): String {
        return "X$qty"
    }

    fun displayIceSugar(): String {
        return "$sugar$ice"
    }

}