package app.jerry.drink.dataclass.source.local

import android.content.Context
import app.jerry.drink.dataclass.Comment
import app.jerry.drink.dataclass.Drink
import app.jerry.drink.dataclass.Result
import app.jerry.drink.dataclass.Store
import app.jerry.drink.dataclass.source.DrinkDataSource

class DrinkLocalDataSource(val context: Context) : DrinkDataSource {

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

    override suspend fun postComment(comment: Comment): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}