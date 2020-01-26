package app.jerry.drink.dataclass

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comment(
    @Json(name = "id")var productId:Long,
    @Json(name = "name")var title:String,
    var price:Int,
    var size : String,
    @Json(name = "qty")var quantityRequired : Long
): Parcelable