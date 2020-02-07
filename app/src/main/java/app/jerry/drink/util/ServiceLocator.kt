package app.jerry.drink.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import app.jerry.drink.dataclass.source.DefaultDrinkRepository
import app.jerry.drink.dataclass.source.DrinkDataSource
import app.jerry.drink.dataclass.source.DrinkRepository
import app.jerry.drink.dataclass.source.local.DrinkLocalDataSource
import app.jerry.drink.dataclass.source.remote.DrinkRemoteDataSource

object ServiceLocator {

    @Volatile
    var repository: DrinkRepository? = null
        @VisibleForTesting set

    fun provideRepository(context: Context): DrinkRepository {
        synchronized(this) {
            return repository
                ?: repository
                ?: createDrinkRepository(context)
        }
    }

    private fun createDrinkRepository(context: Context): DrinkRepository {
        return DefaultDrinkRepository(
            DrinkRemoteDataSource,
            createLocalDataSource(context)
        )
    }

    private fun createLocalDataSource(context: Context): DrinkDataSource {
        return DrinkLocalDataSource(context)
    }
}