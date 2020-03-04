package app.jerry.drink.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.jerry.drink.dataclass.Drink
import app.jerry.drink.dataclass.source.DrinkRepository
import app.jerry.drink.detail.DetailViewModel

@Suppress("UNCHECKED_CAST")
class DrinkFactory(
    private val repository: DrinkRepository,
    private val drink: Drink
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(repository, drink) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}