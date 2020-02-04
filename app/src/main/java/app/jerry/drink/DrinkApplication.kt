package app.jerry.drink

import android.app.Application
import app.jerry.drink.dataclass.source.DrinkRepository
import kotlin.properties.Delegates


class DrinkApplication : Application() {

    // Depends on the flavor,
    val repository: DrinkRepository
        get() = ServiceLocator.provideRepository(this)

    companion object {
        var instance: DrinkApplication by Delegates.notNull()
//        lateinit var context : Context
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}

//class DrinkApplication : Application() {
////    // Depends on the flavor,
////    val drinkRepository: DrinkRepository
////        get() = ServiceLocator.provideTasksRepository(this)
////    companion object {
////        var instance: DrinkApplication by Delegates.notNull()
////        lateinit var appContext: Context
////    }
////    override fun onCreate() {
////        super.onCreate()
//////        instance = this
////        appContext = applicationContext
////    }
////}