package app.jerry.drink.dataclass.source.local

import android.content.Context
import app.jerry.drink.dataclass.Drink
import app.jerry.drink.dataclass.Result
import app.jerry.drink.dataclass.Store
import app.jerry.drink.dataclass.source.DrinkDataSource

class DrinkLocalDataSource(val context: Context) : DrinkDataSource {

    override suspend fun getAllStore(): Result<List<Store>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getStoreMenu(store: Store): Result<List<Drink>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}