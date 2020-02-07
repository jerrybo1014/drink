package app.jerry.drink.ext

import androidx.fragment.app.Fragment
import app.jerry.drink.DrinkApplication
import app.jerry.drink.factory.ViewModelFactory

fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as DrinkApplication).repository
    return ViewModelFactory(repository)
}