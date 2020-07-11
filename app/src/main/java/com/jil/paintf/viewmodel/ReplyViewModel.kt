package com.jil.paintf.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.jil.paintf.repository.AfterReplyResult
import com.jil.paintf.repository.ReplyNextRespository
import com.jil.paintf.repository.RetrofitRepository
import com.jil.paintf.repository.UserOperateResult
import com.jil.paintf.service.AppPaintF
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/**==============================
 *===============================
 *== @Date: 2020/7/10 19:16
 *== @author JIL
 *===============================
 *===============================
 **/
class ReplyViewModel: BaseViewModel() {
    val reply2Data: MutableLiveData<ReplyNextRespository> = MutableLiveData()
    var voteReplyLive = MutableLiveData<UserOperateResult>()
    val postReplyResult = MutableLiveData<AfterReplyResult>()
    val retrofitRepository =RetrofitRepository.getInstance()

    var pn =1
    var maxpn=0
    var loading =false

    fun doNetReply2Data(oid: Int, root: Long) {
        if (loading)
            return
        if (pn!=-1)
        retrofitRepository.getDocNextReply(oid,root,pn).subscribe(object : Observer<ReplyNextRespository> {
            override fun onSubscribe(d: Disposable) {
                Log.d("onSubscribe","loading=$loading")
                loading=true
            }

            override fun onNext(replyRepository: ReplyNextRespository) {
                reply2Data.postValue(replyRepository)
                if (replyRepository.data.page.count % 20 == 0)
                    maxpn = replyRepository.data.page.count / 20
                else
                    maxpn = replyRepository.data.page.count / 20 + 1
            }

            override fun onError(e: Throwable) {
                loading=false
            }
            override fun onComplete() {
                loading=false
                if (pn != maxpn) {
                    pn++
                } else {
                    pn = -1
                }
            }
        })
    }

    fun doNetPostReply(oid: Int, type: Int, root: Long, parent: Long, message: String?, plat: Int, csrf: String?) {
        retrofitRepository.postReply(oid, type, root, parent, message, plat, csrf)
            .subscribe { afterReplyResult -> postReplyResult.postValue(afterReplyResult) }.add()
    }

    fun doNetVoteReply(oid: Long, type: Int, rpid: Long, action: Int,csrf: String?) {
        if (AppPaintF.instance.csrf == null) return
        retrofitRepository.voteReply(oid, type, rpid, action,csrf)
            .subscribe({
                voteReplyLive.postValue(it)
            }, {
                it.printStackTrace()
            }).add()
    }
}