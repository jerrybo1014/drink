package app.jerry.drink.radar

import androidx.lifecycle.ViewModel
import app.jerry.drink.dataclass.Store
import app.jerry.drink.dataclass.source.DrinkRepository

class RadarViewModel(private val repository: DrinkRepository, private val store: Store) : ViewModel() {

}