package com.jil.paintf.viewmodel

import androidx.lifecycle.MutableLiveData
import com.jil.paintf.repository.DocListRepository
import com.jil.paintf.repository.Item
import com.jil.paintf.repository.MyInfo
import com.jil.paintf.repository.RetrofitRepository
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class MainViewModel : BaseViewModel() {
    val retrofitRepository = RetrofitRepository.getInstance()

    val myInfoMutableLiveData: MutableLiveData<MyInfo> = MutableLiveData()
    val ril = arrayListOf<Item>()
    var recommendIllustsList: MutableLiveData<List<Item>> = MutableLiveData()
    var newIllustsList: MutableLiveData<List<Item>> =  MutableLiveData()
    var hotIllustsList: MutableLiveData<List<Item>> = MutableLiveData()
    val recommendCosPlayList: MutableLiveData<List<Item>> =MutableLiveData()
    var newCosplayList: MutableLiveData<List<Item>> =MutableLiveData()
    var hotCosplayList: MutableLiveData<List<Item>> = MutableLiveData()

    fun doNetMyInfo() {
        retrofitRepository.myInfo.subscribe({ myInfo ->
            if (myInfo != null) myInfoMutableLiveData.postValue(myInfo)
        },{},{}).add()
    }

    private fun doNetRecommendCosPlay() {
        retrofitRepository.getRecommendCosplay(page[RC], 20)
            .subscribe(object : Observer<DocListRepository> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(docListRepository: DocListRepository) {

                    recommendCosPlayList.postValue(docListRepository.data.items)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onComplete() {
                    page[RC]++
                }
            })
    }

    private fun doNetNewCosPlay() {
        retrofitRepository.getNewCosplay(page[NC], 20)
            .subscribe(object : Observer<DocListRepository> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(docListRepository: DocListRepository) {
                    newCosplayList.postValue(docListRepository.data.items)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onComplete() {
                    page[NC]++
                }
            })
    }

    private fun doNetHotCosPlay() {
        retrofitRepository.getHotCosplay(page[HC], 20)
            .subscribe(object : Observer<DocListRepository> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(docListRepository: DocListRepository) {
                    hotCosplayList.postValue(docListRepository.data.items)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onComplete() {
                    page[HC]++
                }
            })
    }

    private fun doNetNewIllust() {
        retrofitRepository.getNewIllusts(page[1], 20)
            .subscribe(object : Observer<DocListRepository> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(docListRepository: DocListRepository) {
                    newIllustsList.postValue(docListRepository.data.items)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onComplete() {
                    page[1]++
                }
            })
    }

    private fun doNetHotIllust() {
        retrofitRepository.getHotIllusts(page[2], 20)
            .subscribe(object : Observer<DocListRepository> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(docListRepository: DocListRepository) {
                    hotIllustsList.postValue(docListRepository.data.items)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onComplete() {
                    page[2]++
                }
            })
    }

    private fun doNetRecommendIllust() {
        retrofitRepository.getRecommend(page[0], SIZE)
            .subscribe(object : Observer<DocListRepository> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(docListRepository: DocListRepository) {
                    recommendIllustsList.postValue(docListRepository.data.items)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onComplete() {
                    page[0]++
                }
            })
    }

    fun refresh(type: Int) {
        when (type) {
            RI -> doNetRecommendIllust()
            NI -> doNetNewIllust()
            HI -> doNetHotIllust()
            RC -> doNetRecommendCosPlay()
            NC -> doNetNewCosPlay()
            HC -> doNetHotCosPlay()
            else -> {
            }
        }
    }

    companion object {
        const val RI = 0
        const val NI = 1
        const val HI = 2
        const val RC = 3
        const val NC = 4
        const val HC = 5
        private val page = intArrayOf(0, 0, 0, 0, 0, 0)
        private const val SIZE = 45
    }
}