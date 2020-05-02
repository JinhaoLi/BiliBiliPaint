package com.jil.paintf.viewmodel;

import android.media.TimedMetaData;
import android.widget.Toast;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jil.paintf.repository.*;
import com.jil.paintf.service.DataRoomService;
import com.orhanobut.logger.Logger;
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
    private MutableLiveData<ReplyData> reply2Data;
    private MutableLiveData<List<Reply>> liveReplyData;
    private List<Reply> replyData =new ArrayList<>();
    boolean isLoading =false;
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
                saveHis(docRepository.getData());
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

    public MutableLiveData<ReplyData> getReply2Data(int oid,long root) {
        if(reply2Data==null){
            reply2Data =new MutableLiveData<>();
        }
        retrofitRepository.getDocNextReply(oid,root).subscribe(new Observer<ReplyRepository>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ReplyRepository replyRepository) {
                reply2Data.postValue(replyRepository.getData());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        return reply2Data;
    }

    public MutableLiveData<List<Reply>> getReplyData(final int id, boolean resetPage) {
        if(resetPage){
            isLoading=false;
            pn =1;
            replyData.clear();
        }
        if(liveReplyData ==null){
            liveReplyData =new MutableLiveData<>();
        }
        if(isLoading)
            return liveReplyData;
        if(pn!=-1)//-1 代表评论已经全部加载完成
        retrofitRepository.getDocReply(pn,id).subscribe(new Observer<ReplyRepository>() {
            @Override
            public void onSubscribe(Disposable d) {
                isLoading=true;
                Logger.d("正在加载第"+pn+"页评论！");
            }

            @Override
            public void onNext(ReplyRepository replyRepository) {
                List<Reply> list =replyRepository.getData().getReplies();
                if(list==null){
                    return;
                }
                replyData.addAll(list);
                liveReplyData.postValue(replyData);
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
                isLoading=false;
                //Logger.d("第"+pn+"页评论完成加载！"+"共"+replyData.size()+"条评论！");
                if(pn!=maxpn){
                    pn++;
                    //getReplyData(id,false);
                }else {
                    pn=-1;
                    //liveReplyData.postValue(replyData);
                    Logger.d("共"+maxpn+"页，加载评论完成！");
                }
            }
        });
        return liveReplyData;
    }
}
