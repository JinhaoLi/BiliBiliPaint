package com.jil.paintf.repository;

import com.jil.paintf.network.Client;
import com.jil.paintf.service.AppApiService;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RetrofitRepository {
    private Client client;
    private AppApiService appApiService;
    private RecommendRetryWithDelay recommendRetryWithDelay;
    private static RetrofitRepository retrofitRepository;
    public static RetrofitRepository getInstance(){
        if(retrofitRepository==null){
                synchronized (RetrofitRepository.class){
                    if(retrofitRepository==null){
                        return retrofitRepository =new RetrofitRepository();
                    }
                }
        }
        return retrofitRepository;
    }

    private RetrofitRepository() {
        client =new Client();
        appApiService =client.getRetrofitAppApi().create(AppApiService.class);
    }

    public Observable<DocListRepository> getRecommend(final int page, final int size){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Integer integer) throws Exception {
                return appApiService.getRecommedIllust(page,size);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new RecommendRetryWithDelay(3,page,size));
    }

    public Observable<DocRepository> getDocDetail(final int id){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocRepository>>() {
            @Override
            public Observable<DocRepository> apply(Integer integer) throws Exception {
                return appApiService.getIllustDoc(id);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DocRetryWithDelay(3,id));
    }

    public Observable<DocListRepository> getNew(final int page, final int size){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Integer integer) throws Exception {
                return appApiService.getNewIllusts(page,size);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new RecommendRetryWithDelay(3,page,size));
    }

    public Observable<DocListRepository> getHot(final int page, final int size){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Integer integer) throws Exception {
                return appApiService.getHotIllusts(page,size);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new RecommendRetryWithDelay(3,page,size));
    }
}
