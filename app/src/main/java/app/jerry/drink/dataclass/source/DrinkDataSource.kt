package app.jerry.drink.dataclass.source

import app.jerry.drink.dataclass.Drink
import app.jerry.drink.dataclass.Result
import app.jerry.drink.dataclass.Store

interface DrinkDataSource {

    suspend fun getAllStore(): Result<List<Store>>

    suspend fun getStoreMenu(store: Store): Result<List<Drink>>
}