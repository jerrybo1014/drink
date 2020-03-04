package app.jerry.drink.dataclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Drink(
    var drinkId: String = "",
    val drinkName: String = "",
    var store: Store = Store("","","")
) : Parcelable
