package com.jil.paintf.viewmodel

import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jil.paintf.repository.*
import com.jil.paintf.service.AppPaintF
import com.jil.paintf.service.DataRoomService
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class DocViewModel : BaseViewModel() {
    var retrofitRepository = RetrofitRepository.getInstance()
    val data: MutableLiveData<DocRepository>

    //==================================评论相关数据
    var liveReplyData: MutableLiveData<ReplyRepository> = MutableLiveData()
    var liveMyReply:MutableLiveData<Reply> = MutableLiveData()
    var pn = 1
    var maxpn = 2
    private fun doNetGetEmote() {
        retrofitRepository.emoteMap.subscribe { (_, data1) ->
            val packages = data1.packages
            for ((_, emotes) in packages) {
                for ((_, _, _, _, _, _, key, _, url) in emotes) {
                    Glide.with(AppPaintF.instance.applicationContext).asDrawable()
                        .load("$url@64w_64h.webp").into(object : CustomTarget<Drawable?>() {
                            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable?>?) {
                                resource.setBounds(
                                    0,
                                    0,
                                    resource.intrinsicWidth,
                                    resource.intrinsicHeight
                                )
                                AppPaintF.instance.emoteMap[key] = resource
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {}
                        })
                }
            }
        }.add()
    }

    fun doNetGetDoc(id: Int) {
        retrofitRepository.getDocDetail(id).subscribe(object : Observer<DocRepository> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(docRepository: DocRepository) {
                data.postValue(docRepository)
                if(docRepository.code==0)
                    saveHis(docRepository.data)
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
            }

            override fun onComplete() {}
        })
    }

    fun saveHis(docData: DocData) {
        val insert =
            Observable.create<Int> {
                val docid = docData.item.doc_id
                val title = docData.item.title
                val image = docData.item.pictures[0].img_src
                val count = docData.item.pictures.size
                DataRoomService.getDatabase().hisItemDao.insert(HisItem(docid, image, title, count))
            }
        insert.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { }.add()
    }

    fun resetReply() {
        pn = 1
    }

    fun doNetReply(id: Int) {
        if (pn != -1) //-1 代表评论已经全部加载完成
            retrofitRepository.getDocReply(pn, id).subscribe(object : Observer<ReplyRepository> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(replyRepository: ReplyRepository) {
                    if(!replyRepository.data.replies.isNullOrEmpty()){
                        replyRepository.data.replies.map {
                            if(it.mid==AppPaintF.instance.loginId){
                                liveMyReply.postValue(it)
                            }
                        }
                    }

                    liveReplyData.postValue(replyRepository)
                    maxpn =
                        if (replyRepository.data.page.count % 20 == 0)
                            replyRepository.data.page.count / 20
                        else
                            replyRepository.data.page.count / 20 + 1
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onComplete() {
                    if (pn < maxpn) {
                        pn++
                    } else {
                        pn = -1
                    }
                }
            })
    }

    init {
        data = MutableLiveData()
        if (AppPaintF.instance.emoteMap.size == 0) {
            //获取表情map
            doNetGetEmote()
        }
    }
}