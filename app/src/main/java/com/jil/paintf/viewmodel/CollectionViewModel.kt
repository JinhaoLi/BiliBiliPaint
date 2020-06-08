package com.jil.paintf.viewmodel

import androidx.lifecycle.MutableLiveData
import com.jil.paintf.repository.CollectionResult
import com.jil.paintf.repository.RetrofitRepository
import io.reactivex.functions.Consumer

class CollectionViewModel:BaseViewModel() {
    val mutableLiveData:MutableLiveData<CollectionResult> =MutableLiveData()
    var loadFinsh =false

    fun doNetMCollection(page:Int):Boolean{
        if(!loadFinsh){
            RetrofitRepository.getInstance().getMCollection(page).subscribe({
                if(it.data.list.isNullOrEmpty() ||it.data.list.size<30){
                    loadFinsh=true
                }
                mutableLiveData.postValue(it)
            }, {

            }).add()
            return true
        }else{
            return false
        }

    }
}