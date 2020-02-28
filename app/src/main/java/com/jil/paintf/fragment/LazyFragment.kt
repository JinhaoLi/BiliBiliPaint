package com.jil.paintf.fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.jil.paintf.R
import com.orhanobut.logger.Logger

abstract class LazyFragment : Fragment(){
    private var isLoaded = false
    private var isRecovery: Boolean=false
    var rootView:View? =null
    override fun onResume() {
        super.onResume()
        if (!isLoaded) {
            isLoaded = true
            loadAndObserveData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (rootView !=null ) {
            if(rootView!!.parent!=null){
                (rootView!!.parent as ViewGroup).removeView(rootView)
            }
        }
        Logger.d(arguments?.getInt("param1").toString()+"onDestroyView()")
    }


    protected abstract fun loadAndObserveData()

    fun initView(inflater: LayoutInflater, container: ViewGroup?,layoutRes:Int):View?{
        if (rootView == null) {
            isRecovery = false
            rootView = inflater.inflate(layoutRes, container, false)
        } else {
            isRecovery = true
        }

        return rootView
    }
}