package app.jerry.drink.dataclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Star(
    var oneStar: Int,
    var twoStar: Int,
    var threeStar: Int,
    var fourStar: Int,
    var fiveStar: Int,
    var avgStar: Float,
    var qty: Int
): Parcelable