package app.jerry.drink.dataclass.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.jerry.drink.dataclass.*

class DefaultDrinkRepository(private val remoteDataSource: DrinkDataSource,
                                 private val localDataSource: DrinkDataSource
) : DrinkRepository {

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

    override suspend fun postComment(comment: Comment): Result<Boolean> {
        return remoteDataSource.postComment(comment)
    }

    override suspend fun createOrder(order: Order): Result<Boolean> {
        return remoteDataSource.createOrder(order)
    }

    override suspend fun getOrder(orderId: Long): Result<OrderLists> {
        return remoteDataSource.getOrder(orderId)
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

    override fun getUserCurrent(): User {
        return remoteDataSource.getUserCurrent()
    }
}