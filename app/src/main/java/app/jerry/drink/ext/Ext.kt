package app.jerry.drink.ext

import android.icu.text.SimpleDateFormat
import java.util.*

fun Long.toDisplayFormat(): String {
    return SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.TAIWAN).format(this)
}