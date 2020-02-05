package app.jerry.drink.dataclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var id: String,
    var name: String?,
    var email: String?,
    var image: String? =""
): Parcelable