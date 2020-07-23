package com.jil.paintf.viewmodel;

import android.graphics.drawable.Drawable;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jil.paintf.repository.*;
import com.jil.paintf.repository.Package;
import com.jil.paintf.service.AppPaintF;
import com.jil.paintf.service.DataRoomService;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
        if(AppPaintF.instance.getEmoteMap().size()==0){
            //获取表情map
            doNetGetEmote();
        }
    }

    private void doNetGetEmote(){
        add(retrofitRepository.getEmoteMap().subscribe(new Consumer<EmoteData>() {
            @Override
            public void accept(EmoteData emoteData) {
                List<Package> packages =emoteData.getData().getPackages();
                for (Package p : packages) {
                    List<Emote> emotes =p.getEmote();
                    for (final Emote emote:emotes) {
                        Glide.with(AppPaintF.instance.getApplicationContext()).asDrawable()
                                .load(emote.getUrl()+"@64w_64h.webp").into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                resource.setBounds(0, 0, resource.getIntrinsicWidth(), resource.getIntrinsicHeight());
                                String key =emote.getText();
                                AppPaintF.instance.getEmoteMap().put(key,resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });

                    }
                }
            }
        }));
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
