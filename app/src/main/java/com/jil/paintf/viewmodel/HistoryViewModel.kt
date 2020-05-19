package com.jil.paintf.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jil.paintf.repository.HisItem
import com.jil.paintf.service.DataRoomService
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.*


class HistoryViewModel: BaseViewModel() {
    val mutableLiveData:MutableLiveData<List<HisItem>> =MutableLiveData()

    fun doLoadHis(){
        val download: Observable<List<HisItem>> = Observable.create(object : ObservableOnSubscribe<List<HisItem>> {
            override fun subscribe(emitter: ObservableEmitter<List<HisItem>>) {
                val list =DataRoomService.getDatabase().hisItemDao.loadAll()
                emitter.onNext(list)
            }
        })

        download.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(Consumer {
            mutableLiveData.postValue(it)
        }).add()


    }

    fun clearHis(){
        val download: Observable<Any?> = Observable.create(object : ObservableOnSubscribe<Any?> {
            override fun subscribe(emitter: ObservableEmitter<Any?>) {
                DataRoomService.getDatabase().hisItemDao.deleteByDate(System.currentTimeMillis())
                emitter.onNext("null")
            }

        })

        download.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(Consumer {
            mutableLiveData.postValue(arrayListOf())
        }).add()
    }
}