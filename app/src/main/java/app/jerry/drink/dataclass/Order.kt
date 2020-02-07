package app.jerry.drink.dataclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order(
    val user: User? = null,
    val store: Store? = null,
    var createdTime: Long = 0,
    var orderId: Long = 0
): Parcelable