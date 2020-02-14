package app.jerry.drink.dataclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Drink(
    val drinkId: String = "",
    val drinkName: String = ""
): Parcelable
@Parcelize

data class DrinkDetail(
    val drink: Drink? = null,
    val store: Store? = null
): Parcelable