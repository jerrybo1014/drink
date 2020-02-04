package app.jerry.drink

import androidx.fragment.app.Fragment
import app.jerry.drink.factory.ViewModelFactory

fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as DrinkApplication).repository
    return ViewModelFactory(repository)
}