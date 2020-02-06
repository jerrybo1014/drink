package app.jerry.drink.dataclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Drink(
    val drinkId: String = "",
    val drinkName: String = ""
): Parcelable