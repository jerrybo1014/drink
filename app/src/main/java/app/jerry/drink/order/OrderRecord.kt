package app.jerry.drink.order

import android.content.Context
import app.jerry.drink.DrinkApplication

object OrderRecord {

    private const val ORDER_DATA = "order_data"
    private const val ORDER_ID = "order_id"

    var orderId: String? = null
        get() = DrinkApplication.instance
            .getSharedPreferences(ORDER_DATA, Context.MODE_PRIVATE)
            .getString(ORDER_ID, null)
        set(value) {
            field = when (value) {
                null -> {
                    DrinkApplication.instance
                        .getSharedPreferences(ORDER_DATA, Context.MODE_PRIVATE).edit()
                        .remove(ORDER_ID)
                        .apply()
                    null
                }
                else -> {
                    DrinkApplication.instance
                        .getSharedPreferences(ORDER_DATA, Context.MODE_PRIVATE).edit()
                        .putString(ORDER_ID, value)
                        .apply()
                    value
                }
            }
        }
}