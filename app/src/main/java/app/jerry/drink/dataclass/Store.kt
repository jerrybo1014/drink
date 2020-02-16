package app.jerry.drink.dataclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Store(
    val storeId: String = "",
    val storeName: String ="",
    val uri: String = ""
): Parcelable