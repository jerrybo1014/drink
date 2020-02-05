package app.jerry.drink.dataclass.source

import app.jerry.drink.dataclass.Comment
import app.jerry.drink.dataclass.Drink
import app.jerry.drink.dataclass.Result
import app.jerry.drink.dataclass.Store

interface DrinkRepository {

    suspend fun getAllStore(): Result<List<Store>>

    suspend fun getStoreMenu(store: Store): Result<List<Drink>>

    suspend fun postComment(comment: Comment): Result<Boolean>

}