package app.jerry.drink.dataclass

import android.os.Parcelable
import com.google.firebase.firestore.DocumentReference
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comment(
    val user: User,
    var id: String,
    val drinkName: String,
    val storeName: String,
    val drinkId: String,
    val ice: String,
    val sugar: String,
    val star: Int,
    val comment: String,
    var createdTime: String
): Parcelable

