package app.jerry.drink.dataclass

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.firebase.firestore.DocumentReference
import com.squareup.moshi.Json
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat

@Parcelize
data class Comment(
    var userId: String ="",
    var user: User? = null,
    var id: String = "",
    val storeName: String = "",
    val storeId: String = "",
    val drinkName: String = "",
    val drinkId: String = "",
    val ice: String = "",
    val sugar: String = "",
    val star: Int = 0,
    val comment: String = "",
    var drinkImage : String = "",
    var createdTime: Long = 0
): Parcelable{

    fun iceAndSugar(): String{
        return "$sugar$ice"
    }

}