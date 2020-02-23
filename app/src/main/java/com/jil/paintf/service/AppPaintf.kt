package com.jil.paintf.service

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

class AppPaintf : Application() {
    override fun onCreate() {
        super.onCreate()
        APP = this
        Logger.addLogAdapter(AndroidLogAdapter())

        registerActivityLifecycleCallbacks(object: ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
//                Logger.t(TAG).v("${activity.simpleName}: onActivityCreated")

                ActivityCollector.collect(activity)
            }

            override fun onActivityStarted(activity: Activity) {
//                Logger.t(TAG).v("${activity.simpleName}: onActivityStarted")
            }

            override fun onActivityResumed(activity: Activity) {
//                Logger.t(TAG).v("${activity.simpleName}: onActivityResumed")
            }

            override fun onActivityPaused(activity: Activity) {
//                Logger.t(TAG).v("${activity.simpleName}: onActivityPaused")
            }

            override fun onActivityStopped(activity: Activity) {
//                Logger.t(TAG).v("${activity.simpleName}: onActivityStopped")
            }

            override fun onActivityDestroyed(activity: Activity) {
//                Logger.t(TAG).v("${activity.simpleName}: onActivityDestroyed")

                ActivityCollector.discard(activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                //
            }
        })
    }

    private val Activity.simpleName get() = javaClass.simpleName

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



    companion object {
        var APP: AppPaintf? = null
        var theme = 0
    }
}