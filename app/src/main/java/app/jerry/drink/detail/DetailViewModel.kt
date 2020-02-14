package app.jerry.drink.detail

import androidx.lifecycle.ViewModel
import app.jerry.drink.dataclass.DrinkDetail
import app.jerry.drink.dataclass.source.DrinkRepository

class DetailViewModel(private val repository: DrinkRepository, private val drinkDeatil: DrinkDetail) : ViewModel() {

}
