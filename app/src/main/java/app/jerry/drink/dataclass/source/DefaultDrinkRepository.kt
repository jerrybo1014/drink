package app.jerry.drink.dataclass.source

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.jerry.drink.dataclass.*

class DefaultDrinkRepository(private val remoteDataSource: DrinkDataSource,
                                 private val localDataSource: DrinkDataSource
) : DrinkRepository {

    override suspend fun addStoreToDrink(store: Store): Result<Boolean> {
        return remoteDataSource.addStoreToDrink(store)
    }

    override suspend fun checkUser(): Result<Boolean> {
        return remoteDataSource.checkUser()
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

    override suspend fun createOrder(order: Order): Result<String> {
        return remoteDataSource.createOrder(order)
    }

    override suspend fun getOrder(orderId: Long): Result<OrderLists> {
        return remoteDataSource.getOrder(orderId)
    }

    override fun getOrderIdLive(orderId: Long): LiveData<OrderLists> {
        return remoteDataSource.getOrderIdLive(orderId)
    }

    override fun getOrderLive(orderId: Long): LiveData<List<OrderList>> {
        return remoteDataSource.getOrderLive(orderId)
    }

    override suspend fun addOrder(orderList: OrderList, orderId: Long): Result<Boolean> {
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

    override suspend fun getDetailComment(drinkDetail: DrinkDetail): Result<List<Comment>> {
        return remoteDataSource.getDetailComment(drinkDetail)
    }

    override suspend fun getUserComment(): Result<List<Comment>> {
        return remoteDataSource.getUserComment()
    }

    override suspend fun getUserOrder(): Result<List<Order>> {
        return remoteDataSource.getUserOrder()
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
}