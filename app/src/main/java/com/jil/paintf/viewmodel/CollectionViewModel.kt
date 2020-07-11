package com.jil.paintf.viewmodel

import androidx.lifecycle.MutableLiveData
import com.jil.paintf.repository.CollectionResult
import com.jil.paintf.repository.FavOperateResult
import com.jil.paintf.repository.RetrofitRepository
import com.jil.paintf.service.AppPaintF

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
    var removeFavResult = MutableLiveData<FavOperateResult>()
    fun doNetDeleteFav(id: Int) {
        if (AppPaintF.instance.csrf == null) {
            removeFavResult.postValue(FavOperateResult(-1, arrayListOf(), "没有登录", "没有登录"))
            return
        }
        RetrofitRepository.getInstance().postDeleteFav(id).subscribe(
            { favOperateResult -> removeFavResult.postValue(favOperateResult) },
            { }).add()
    }
}