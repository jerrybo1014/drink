package app.jerry.drink.dataclass.source

import app.jerry.drink.dataclass.*

interface DrinkDataSource {

    suspend fun checkUser(): Result<Boolean>

    suspend fun getNewComment(): Result<List<Comment>>

    suspend fun getAllStore(): Result<List<Store>>

    suspend fun getStoreMenu(store: Store): Result<List<Drink>>

    suspend fun postComment(comment: Comment): Result<Boolean>

    suspend fun addOrder(order: Order): Result<Boolean>
}