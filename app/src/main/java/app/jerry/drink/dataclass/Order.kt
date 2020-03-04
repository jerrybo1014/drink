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


