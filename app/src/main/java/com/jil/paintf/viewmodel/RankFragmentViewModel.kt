package com.jil.paintf.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.jil.paintf.repository.RankResult
import com.jil.paintf.repository.RetrofitRepository
import java.text.SimpleDateFormat
import java.util.*

/**
 * 2020/8/29 22:19
 * @author JIL
 **/
class RankFragmentViewModel: BaseViewModel() {
    val weekLiveData = arrayOf(MutableLiveData<RankResult>(),MutableLiveData<RankResult>())
    val monthLiveData = arrayOf(MutableLiveData<RankResult>(),MutableLiveData<RankResult>())
    val dayLiveData = arrayOf(MutableLiveData<RankResult>(),MutableLiveData<RankResult>())


    fun doNetPaintWeek() {
        RetrofitRepository.getInstance().getRankList(1,"","week",getMondayDate()).subscribe({
            weekLiveData[0].postValue(it)
        }, {
            it.printStackTrace()
        }).add()
    }

    fun doNetPaintMonth(){
        RetrofitRepository.getInstance().getRankList(1,"","month",getMonth()).subscribe({
            monthLiveData[0].postValue(it)
        }, {
            it.printStackTrace()
        }).add()
    }

    fun doNetPaintDay(){
        RetrofitRepository.getInstance().getRankList(1,"","day","").subscribe({
            dayLiveData[0].postValue(it)
        }, {
            it.printStackTrace()
        }).add()
    }

    fun doNetCosWeek() {
        RetrofitRepository.getInstance().getRankList(2,"cos","week",getMondayDate()).subscribe({
            weekLiveData[1].postValue(it)
        }, {
            it.printStackTrace()
        }).add()
    }

    fun doNetCosMonth(){
        RetrofitRepository.getInstance().getRankList(2,"cos","month",getMonth()).subscribe({
            monthLiveData[1].postValue(it)
        }, {
            it.printStackTrace()
        }).add()
    }

    fun doNetCosDay(){
        RetrofitRepository.getInstance().getRankList(2,"cos","day","").subscribe({
            dayLiveData[1].postValue(it)
        }, {
            it.printStackTrace()
        }).add()
    }

    /**
     * 获取这周的周一的日期（YYYY-MM-dd）
     */
    @SuppressLint("SimpleDateFormat")
    fun getMondayDate():String{
        val calendar = Calendar.getInstance()
        calendar[Calendar.DAY_OF_WEEK] = 2
        val date = SimpleDateFormat("YYYY-MM-dd").format(calendar.time)
        return date
    }

    @SuppressLint("SimpleDateFormat")
    fun getMonth():String{
        val date = SimpleDateFormat("YYYY-MM").format(Date())
        return date
    }
}