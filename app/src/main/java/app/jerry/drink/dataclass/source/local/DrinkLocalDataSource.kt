package app.jerry.drink.dataclass.source.local

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.jerry.drink.dataclass.*
import app.jerry.drink.dataclass.source.DrinkDataSource

class DrinkLocalDataSource(val context: Context) : DrinkDataSource {

    override suspend fun addStoreToDrink(store: Store): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun checkUser(): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getNewComment(): Result<List<Comment>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getAllStore(): Result<List<Store>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getStoreMenu(store: Store): Result<List<Drink>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun postComment(comment: Comment, bitmap: Bitmap): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun createOrder(order: Order): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getOrder(orderId: Long): Result<OrderLists> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOrderLive(orderId: Long): LiveData<List<OrderList>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun addOrder(orderList: OrderList, orderId: Long): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun removeOrder(orderId: Long ,id: String): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun editOrderStatus(orderId: Long, editStatus: Boolean): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getUserCurrent():  Result<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun uploadAvatar(bitmap: Bitmap): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getDetailComment(drinkDetail: DrinkDetail): Result<List<Comment>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getUserComment(): Result<List<Comment>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getUserOrder(): Result<List<Order>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getStoreLocation(): Result<List<StoreLocation>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getStoreComment(store: Store): Result<List<Comment>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}