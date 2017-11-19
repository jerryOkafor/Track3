package sdg.track3.track3

import android.app.Application
import timber.log.Timber

/**
 * @author <@Po10cio> on 11/19/17 for Track3App
 *
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}