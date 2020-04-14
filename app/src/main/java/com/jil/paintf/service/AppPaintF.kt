package com.jil.paintf.service

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.jil.paintf.network.NetCookie
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

class AppPaintF : Application() {
    companion object {
        const val TAG = "AppPaintF"
        @JvmStatic
        var LoadLevel=1080
        @JvmStatic
        lateinit var instance: AppPaintF
    }
    override fun onCreate() {
        super.onCreate()
        instance=this
        Logger.addLogAdapter(AndroidLogAdapter())
        LoadLevel =PreferenceManager.getDefaultSharedPreferences(this).getInt("LOAD_LEVEL",720)
        registerActivityLifecycleCallbacks(object: ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                Logger.t(TAG).v("${activity.simpleName}: onActivityCreated")

                ActivityCollector.collect(activity)
            }

            override fun onActivityStarted(activity: Activity) {
                Logger.t(TAG).v("${activity.simpleName}: onActivityStarted")
            }

            override fun onActivityResumed(activity: Activity) {
                Logger.t(TAG).v("${activity.simpleName}: onActivityResumed")
            }

            override fun onActivityPaused(activity: Activity) {
                Logger.t(TAG).v("${activity.simpleName}: onActivityPaused")
            }

            override fun onActivityStopped(activity: Activity) {
                Logger.t(TAG).v("${activity.simpleName}: onActivityStopped")
            }

            override fun onActivityDestroyed(activity: Activity) {
                Logger.t(TAG).v("${activity.simpleName}: onActivityDestroyed")

                ActivityCollector.discard(activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                //
            }
        })
    }

    private val Activity.simpleName get() = javaClass.simpleName

    val cookie: NetCookie? get() = if(DataRoomService.getDatabase().cookieDao.loadAll().size==0) null
    else DataRoomService.getDatabase().cookieDao.loadAll()[0]

    object ActivityCollector {

        @JvmStatic
        private val activityList = mutableListOf<Activity>()

        @JvmStatic
        fun collect(activity: Activity) {
            activityList.add(activity)
        }

        @JvmStatic
        fun discard(activity: Activity) {
            activityList.remove(activity)
        }

        @JvmStatic
        fun recreate() {
            for (i in activityList.size - 1 downTo 0) {
                activityList[i].recreate()
            }
        }
    }


}