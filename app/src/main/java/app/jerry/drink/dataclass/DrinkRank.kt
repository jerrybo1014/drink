package app.jerry.drink.dataclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DrinkRank(
    val commentList: MutableList<Comment> = mutableListOf(),
    var drink: Drink = Drink("", "",Store("","","")),
    var store: Store = Store("", "",""),
    var score: Float = 0F
) : Parcelable