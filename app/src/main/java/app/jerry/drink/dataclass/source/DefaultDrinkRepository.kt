package app.jerry.drink.dataclass.source

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import app.jerry.drink.dataclass.*
import com.google.firebase.auth.FirebaseUser

class DefaultDrinkRepository(private val remoteDataSource: DrinkDataSource,
                                 private val localDataSource: DrinkDataSource
) : DrinkRepository {

    override suspend fun addStoreToDrink(store: Store): Result<Boolean> {
        return remoteDataSource.addStoreToDrink(store)
    }

    override suspend fun checkUser(user: User): Result<Boolean> {
        return remoteDataSource.checkUser(user)
    }

    override suspend fun getNewComment(): Result<List<Comment>> {
        return remoteDataSource.getNewComment()
    }

    override suspend fun getAllStore(): Result<List<Store>> {
        return remoteDataSource.getAllStore()
    }

    override suspend fun getStoreMenu(store: Store): Result<List<Drink>> {
        return remoteDataSource.getStoreMenu(store)
    }

    override suspend fun postComment(comment: Comment, bitmap: Bitmap): Result<Boolean> {
        return remoteDataSource.postComment(comment, bitmap)
    }

    override suspend fun deleteComment(comment: Comment): Result<Boolean> {
        return remoteDataSource.deleteComment(comment)
    }

    override suspend fun createOrder(order: Order): Result<String> {
        return remoteDataSource.createOrder(order)
    }

    override fun getOrderLive(orderId: Long): LiveData<Order> {
        return remoteDataSource.getOrderLive(orderId)
    }

    override fun getOrderItemLive(orderId: Long): LiveData<List<OrderItem>> {
        return remoteDataSource.getOrderItemLive(orderId)
    }

    override suspend fun addOrder(orderList: OrderItem, orderId: Long): Result<Boolean> {
        return remoteDataSource.addOrder(orderList, orderId)
    }

    override suspend fun removeOrder(orderId: Long ,id: String): Result<Boolean> {
        return remoteDataSource.removeOrder(orderId, id)
    }

    override suspend fun editOrderStatus(orderId: Long, editStatus: Boolean): Result<Boolean> {
        return remoteDataSource.editOrderStatus(orderId, editStatus)
    }

    override suspend fun getUserCurrent():  Result<User> {
        return remoteDataSource.getUserCurrent()
    }

    override suspend fun uploadAvatar(bitmap: Bitmap): Result<Boolean> {
        return remoteDataSource.uploadAvatar(bitmap)
    }

    override suspend fun getDetailComment(drink: Drink): Result<List<Comment>> {
        return remoteDataSource.getDetailComment(drink)
    }

    override suspend fun getUserComment(user: User): Result<List<Comment>> {
        return remoteDataSource.getUserComment(user)
    }

    override suspend fun getUserOrder(user: User): Result<List<Order>> {
        return remoteDataSource.getUserOrder(user)
    }

    override suspend fun getStoreLocation(): Result<List<StoreLocation>> {
        return remoteDataSource.getStoreLocation()
    }

    override suspend fun getStoreComment(store: Store): Result<List<Comment>> {
        return remoteDataSource.getStoreComment(store)
    }

    override suspend fun getSearchDrink(): Result<List<Drink>> {
        return remoteDataSource.getSearchDrink()
    }

    override suspend fun addNewDrink(comment: Comment, bitmap: Bitmap): Result<Boolean> {
        return remoteDataSource.addNewDrink(comment, bitmap)
    }
}