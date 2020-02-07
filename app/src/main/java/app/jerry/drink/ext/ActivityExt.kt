package app.jerry.drink.ext

import android.app.Activity
import app.jerry.drink.DrinkApplication
import app.jerry.drink.factory.ViewModelFactory

fun Activity.getVmFactory(): ViewModelFactory {
    val repository = (applicationContext as DrinkApplication).repository
    return ViewModelFactory(repository)
}