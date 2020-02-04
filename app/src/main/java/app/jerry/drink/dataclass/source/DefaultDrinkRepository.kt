package app.jerry.drink.dataclass.source

class DefaultDrinkRepository(private val remoteDataSource: DrinkDataSource,
                                 private val localDataSource: DrinkDataSource
) : DrinkRepository {

}