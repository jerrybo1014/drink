package app.jerry.drink.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.jerry.drink.dataclass.DrinkDetail
import app.jerry.drink.dataclass.source.DrinkRepository
import app.jerry.drink.detail.DetailViewModel

@Suppress("UNCHECKED_CAST")
class DrinkDetailFactory(
    private val repository: DrinkRepository,
    private val drinkDetail: DrinkDetail
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(repository, drinkDetail) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}