package app.jerry.drink.dataclass.source

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import app.jerry.drink.dataclass.*
import com.google.firebase.auth.FirebaseUser

interface DrinkDataSource {

    suspend fun addStoreToDrink(store: Store): Result<Boolean>

    suspend fun checkUser(user: User): Result<Boolean>

    suspend fun getNewComment(): Result<List<Comment>>

    suspend fun getAllStore(): Result<List<Store>>

    suspend fun getStoreMenu(store: Store): Result<List<Drink>>

    suspend fun postComment(comment: Comment, bitmap: Bitmap): Result<Boolean>

    suspend fun deleteComment(comment: Comment): Result<Boolean>

    suspend fun createOrder(order: Order): Result<String>

    fun getOrderLive(orderId: Long): LiveData<Order>

    fun getOrderItemLive(orderId: Long): LiveData<List<OrderItem>>

    suspend fun addOrder(orderList: OrderItem, orderId: Long): Result<Boolean>

    suspend fun removeOrder(orderId: Long ,id: String): Result<Boolean>

    suspend fun editOrderStatus(orderId: Long, editStatus: Boolean): Result<Boolean>

    suspend fun getUserCurrent():  Result<User>

    suspend fun uploadAvatar(bitmap: Bitmap): Result<Boolean>

    suspend fun getDetailComment(drink: Drink): Result<List<Comment>>

    suspend fun getUserComment(user: User): Result<List<Comment>>

    suspend fun getUserOrder(user: User): Result<List<Order>>

    suspend fun getStoreLocation(): Result<List<StoreLocation>>

    suspend fun getStoreComment(store: Store): Result<List<Comment>>

    suspend fun getSearchDrink(): Result<List<Drink>>

    suspend fun addNewDrink(comment: Comment, bitmap: Bitmap): Result<Boolean>
}