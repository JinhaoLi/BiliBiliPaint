package com.jil.paintf.service

import android.app.Activity
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import androidx.preference.PreferenceManager
import com.jil.paintf.network.NetCookie
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import java.io.File


class AppPaintF : Application() {
    companion object {
        const val TAG = "AppPaintF"
        const val SAVE_DIR ="SAVE_DIR"
        @JvmStatic
        var LoadLevel=1080
        @JvmStatic
        var save_dir_path =Environment.getExternalStorageDirectory().path+ File.separator+Environment.DIRECTORY_DOWNLOADS
        @JvmStatic
        lateinit var instance: AppPaintF
        @JvmStatic
        var isDebug=true
    }
    val emoteMap =HashMap<String,Drawable>()
    private val Activity.simpleName get() = javaClass.simpleName
    var enableAnimator =false
    var stagger =false
    var csrf:String? =null
    var FirstEntry = false
    val cookie: NetCookie? get() = if(DataRoomService.getDatabase().cookieDao.loadAll().size==0) null else DataRoomService.getDatabase().cookieDao.loadAll()[0]
    override fun onCreate() {
        super.onCreate()
        instance=this
        isDebug =isDebugState()
        csrf = cookie?.bili_jct
        Logger.addLogAdapter(AndroidLogAdapter())
        FirstEntry =PreferenceManager.getDefaultSharedPreferences(this).getBoolean("FirstEntry", true)
        enableAnimator=PreferenceManager.getDefaultSharedPreferences(this).getBoolean("EnableAnimator", false)
        stagger=PreferenceManager.getDefaultSharedPreferences(this).getBoolean("Stagger", false)
        save_dir_path = PreferenceManager.getDefaultSharedPreferences(this).getString(SAVE_DIR, save_dir_path)!!
        LoadLevel =PreferenceManager.getDefaultSharedPreferences(this).getInt("LOAD_LEVEL",720)
        registerActivityLifecycleCallbacks(object: ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                //Logger.t(TAG).v("${activity.simpleName}: onActivityCreated")

                ActivityCollector.collect(activity)
            }

            override fun onActivityStarted(activity: Activity) {
                //Logger.t(TAG).v("${activity.simpleName}: onActivityStarted")
            }

            override fun onActivityResumed(activity: Activity) {
                //Logger.t(TAG).v("${activity.simpleName}: onActivityResumed")
            }

            override fun onActivityPaused(activity: Activity) {
                //Logger.t(TAG).v("${activity.simpleName}: onActivityPaused")
            }

            override fun onActivityStopped(activity: Activity) {
                //Logger.t(TAG).v("${activity.simpleName}: onActivityStopped")
            }

            override fun onActivityDestroyed(activity: Activity) {
                //Logger.t(TAG).v("${activity.simpleName}: onActivityDestroyed")

                ActivityCollector.discard(activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }
        })
    }

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

    fun isDebugState():Boolean{
        var isdebug =true
        try {
            val info=instance.applicationContext.applicationInfo
            isdebug =info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        }catch (e:Exception){

            val clipboard = instance.getSystemService(Application.CLIPBOARD_SERVICE) as ClipboardManager
            val clipdata =ClipData.newPlainText(null,e.message)
            clipboard.setPrimaryClip(clipdata)
        }
        Logger.i("检测是否debug"+isdebug)
        return isdebug
    }


}