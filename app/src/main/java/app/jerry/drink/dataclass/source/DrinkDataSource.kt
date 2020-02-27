package app.jerry.drink.dataclass.source

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.jerry.drink.dataclass.*

interface DrinkDataSource {

    suspend fun addStoreToDrink(store: Store): Result<Boolean>

    suspend fun checkUser(): Result<Boolean>

    suspend fun getNewComment(): Result<List<Comment>>

    suspend fun getAllStore(): Result<List<Store>>

    suspend fun getStoreMenu(store: Store): Result<List<Drink>>

    suspend fun postComment(comment: Comment, bitmap: Bitmap): Result<Boolean>

    suspend fun createOrder(order: Order): Result<String>

    suspend fun getOrder(orderId: Long): Result<OrderLists>

    fun getOrderLive(orderId: Long): LiveData<List<OrderList>>

    suspend fun addOrder(orderList: OrderList, orderId: Long): Result<Boolean>

    suspend fun removeOrder(orderId: Long ,id: String): Result<Boolean>

    suspend fun editOrderStatus(orderId: Long, editStatus: Boolean): Result<Boolean>

    suspend fun getUserCurrent():  Result<User>

    suspend fun uploadAvatar(bitmap: Bitmap): Result<Boolean>

    suspend fun getDetailComment(drinkDetail: DrinkDetail): Result<List<Comment>>

    suspend fun getUserComment(): Result<List<Comment>>

    suspend fun getUserOrder(): Result<List<Order>>

    suspend fun getStoreLocation(): Result<List<StoreLocation>>

    suspend fun getStoreComment(store: Store): Result<List<Comment>>

    suspend fun getSearchDrink(): Result<List<Drink>>
}