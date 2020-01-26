package app.jerry.drink.dataclass

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comment(
    var comment: String
): Parcelable