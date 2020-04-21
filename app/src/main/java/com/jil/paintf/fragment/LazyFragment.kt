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


//    override fun onPause() {
//        Logger.d(arguments?.getInt("param1").toString()+"onPause()")
//        super.onPause()
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        Logger.d(arguments?.getInt("param1").toString()+"onViewCreated()")
//        super.onViewCreated(view, savedInstanceState)
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        Logger.d(arguments?.getInt("param1").toString()+"onActivityCreated()")
//        super.onActivityCreated(savedInstanceState)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        Logger.d(arguments?.getInt("param1").toString()+"onCreate()")
//        super.onCreate(savedInstanceState)
//    }
//
//    override fun onStart() {
//        Logger.d(arguments?.getInt("param1").toString()+"onStart()")
//        super.onStart()
//    }
//
//    override fun onDetach() {
//        Logger.d(arguments?.getInt("param1").toString()+"onDetach()")
//        super.onDetach()
//    }
//
//    override fun onStop() {
//        Logger.d(arguments?.getInt("param1").toString()+"onStop()")
//        super.onStop()
//    }
//
//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        Logger.d(arguments?.getInt("param1").toString()+"onViewStateRestored()")
//        super.onViewStateRestored(savedInstanceState)
//    }
//
//    override fun onDestroy() {
//        Logger.d(arguments?.getInt("param1").toString()+"onDestroy()")
//        super.onDestroy()
//    }
}