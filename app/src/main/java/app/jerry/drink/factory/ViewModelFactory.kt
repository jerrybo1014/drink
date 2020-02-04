package app.jerry.drink.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.jerry.drink.dataclass.source.DrinkRepository
import app.jerry.drink.post.PostViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val drinkRepository: DrinkRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(PostViewModel::class.java) ->
                    PostViewModel(drinkRepository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}