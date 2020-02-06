package app.jerry.drink.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.jerry.drink.MainActivityViewModel
import app.jerry.drink.dataclass.source.DrinkRepository
import app.jerry.drink.home.HomeViewModel
import app.jerry.drink.post.PostViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val drinkRepository: DrinkRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {

                isAssignableFrom(HomeViewModel::class.java) ->
                    HomeViewModel(drinkRepository)

                isAssignableFrom(PostViewModel::class.java) ->
                    PostViewModel(drinkRepository)

                isAssignableFrom(MainActivityViewModel::class.java) ->
                    MainActivityViewModel(drinkRepository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}