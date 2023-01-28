package my.id.andraaa.dstory

import android.app.Application
import my.id.andraaa.dstory.stories.di.ApplicationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DStoryApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@DStoryApplication)
            modules(ApplicationModule)
        }
    }
}