package app.jerry.drink.dataclass.source

import app.jerry.drink.dataclass.Drink
import app.jerry.drink.dataclass.Result
import app.jerry.drink.dataclass.Store

class DefaultDrinkRepository(private val remoteDataSource: DrinkDataSource,
                                 private val localDataSource: DrinkDataSource
) : DrinkRepository {

    override suspend fun getAllStore(): Result<List<Store>> {
        return remoteDataSource.getAllStore()
    }

    override suspend fun getStoreMenu(store: Store): Result<List<Drink>> {
        return remoteDataSource.getStoreMenu(store)
    }
}