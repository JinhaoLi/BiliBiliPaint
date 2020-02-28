package com.jil.paintf.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jil.paintf.repository.*;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class DocViewModel extends ViewModel {
    RetrofitRepository retrofitRepository =RetrofitRepository.getInstance();
    private MutableLiveData<DocData> data;
    private MutableLiveData<ReplyData> replyData;
    int pn=1;
    int maxpn=2;

    public MutableLiveData<DocData> getData(int id){
        if(data==null){
            data=new MutableLiveData<>();
        }
        retrofitRepository.getDocDetail(id).subscribe(new Observer<DocRepository>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DocRepository docRepository) {
                data.postValue(docRepository.getData());
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
        return data;
    }

    public MutableLiveData<ReplyData> getReplyData(int id,boolean reSetId) {
        if(reSetId){
            pn =1;
        }
        if(replyData==null){
            replyData =new MutableLiveData<>();
        }
        if(pn<=maxpn+1)
        retrofitRepository.getDocReply(pn,id).subscribe(new Observer<ReplyRepository>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ReplyRepository replyRepository) {
                replyData.postValue(replyRepository.getData());
                maxpn=replyRepository.getData().getPage().getCount()/20;
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                pn++;
            }
        });
        return replyData;
    }
}
