package app.jerry.drink

import android.app.Application
import android.content.Context

class DrinkApplication : Application() {
    // Depends on the flavor,
//    val liTsapRepository: LiTsapRepository
//        get() = ServiceLocator.provideTasksRepository(this)
    companion object {
//        var instance: LiTsapApplication by Delegates.notNull()
        lateinit var appContext: Context
    }
    override fun onCreate() {
        super.onCreate()
//        instance = this
        appContext = applicationContext
    }
}