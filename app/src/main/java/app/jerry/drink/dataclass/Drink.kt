package app.jerry.drink.dataclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Drink(
    val drinkId: String = "",
    val drinkName: String = ""
) : Parcelable

@Parcelize
data class DrinkDetail(
    val drink: Drink? = null,
    val store: Store? = null
) : Parcelable

@Parcelize
data class DrinkRank(
    val commentList: MutableList<Comment> = mutableListOf(),
    var drink: Drink = Drink("", ""),
    var store: Store = Store("", ""),
    var score: Float = 0F
) : Parcelable