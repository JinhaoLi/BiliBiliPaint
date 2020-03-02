package com.jil.paintf.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jil.paintf.repository.RetrofitRepository
import com.jil.paintf.repository.SearchRepository
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class SearchViewModel:ViewModel() {
    val searchDatas = MutableLiveData<SearchRepository>()
    val retrofitRepository =RetrofitRepository.getInstance()

    fun nextPage(keyword:String,page:Int,category: Int){
        retrofitRepository.getSearchData(keyword,page,category).subscribe(object :Observer<SearchRepository>{
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(t: SearchRepository) {
                searchDatas.postValue(t)
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
            }

        })
    }


}