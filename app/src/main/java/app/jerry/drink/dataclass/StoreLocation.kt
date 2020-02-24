package app.jerry.drink.dataclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoreLocation(
    val store: Store = Store("",""),
    val branchName: String ="",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val phone: String = "123"
): Parcelable