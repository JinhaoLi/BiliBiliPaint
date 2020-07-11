package com.jil.paintf.viewmodel;

import androidx.lifecycle.MutableLiveData;
import com.jil.paintf.repository.*;
import com.jil.paintf.service.DataRoomService;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class DocViewModel extends BaseViewModel {
    RetrofitRepository retrofitRepository =RetrofitRepository.getInstance();
    private MutableLiveData<DocData> data;

    //==================================评论相关数据
    public MutableLiveData<ReplyRepository> liveReplyData;
    int pn=1;
    int maxpn=2;

    public DocViewModel() {
        liveReplyData =new MutableLiveData<>();
        data=new MutableLiveData<>();
    }

    public MutableLiveData<DocData> getData(){
        return data;
    }

    public void doNetGetDoc(int id){
        retrofitRepository.getDocDetail(id).subscribe(new Observer<DocRepository>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DocRepository docRepository) {
                data.postValue(docRepository.getData());
                saveHis(docRepository.getData());
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void saveHis(final DocData docData){
        Observable<Integer> insert = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter){
                int docid =docData.getItem().getDoc_id();
                String title =docData.getItem().getTitle();
                String image =docData.getItem().getPictures().get(0).getImg_src();
                int count =docData.getItem().getPictures().size();
                DataRoomService.getDatabase().getHisItemDao().insert(new HisItem(docid, image,title,count));
            }
        });

        add(insert.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

            }
        }));

    }

    public void resetReply(){
        pn =1;
    }
    public void doNetReply(final int id) {
        if(pn!=-1)//-1 代表评论已经全部加载完成
        retrofitRepository.getDocReply(pn,id).subscribe(new Observer<ReplyRepository>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(ReplyRepository replyRepository) {
                liveReplyData.postValue(replyRepository);
                if(replyRepository.getData().getPage().getCount()%20==0)
                    maxpn=replyRepository.getData().getPage().getCount()/20;
                else
                    maxpn=(replyRepository.getData().getPage().getCount()/20)+1;
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                if(pn<maxpn){
                    pn++;
                }else {
                    pn=-1;
                }
            }
        });
    }
}
