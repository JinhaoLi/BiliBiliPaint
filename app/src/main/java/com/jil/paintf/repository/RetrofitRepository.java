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
    private DataListRetryWithDelay dataListRetryWithDelay;
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
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DataListRetryWithDelay(3,page,size, DataListRetryWithDelay.RI));
    }

    public Observable<DocRepository> getDocDetail(final int id){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocRepository>>() {
            @Override
            public Observable<DocRepository> apply(Integer integer) throws Exception {
                return appApiService.getIllustDoc(id);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DocRetryWithDelay(3,id));
    }

    public Observable<DocListRepository> getNewIllusts(final int page, final int size){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Integer integer) throws Exception {
                return appApiService.getNewIllusts(page,size);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DataListRetryWithDelay(3,page,size, DataListRetryWithDelay.NI));
    }

    public Observable<DocListRepository> getHotIllusts(final int page, final int size){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Integer integer) throws Exception {
                return appApiService.getHotIllusts(page,size);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DataListRetryWithDelay(3,page,size, DataListRetryWithDelay.HI));
    }

    public Observable<DocListRepository> getRecommendCosplay(final int page, final int size){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Integer integer) throws Exception {
                return appApiService.getRecommedCosplay(page,size);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DataListRetryWithDelay(3,page,size, DataListRetryWithDelay.RC));
    }

    public Observable<DocListRepository> getHotCosplay(final int page, final int size){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Integer integer) throws Exception {
                return appApiService.getHotCosplay(page,size);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DataListRetryWithDelay(3,page,size, DataListRetryWithDelay.HC));
    }

    public Observable<DocListRepository> getNewCosplay(final int page, final int size){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Integer integer) throws Exception {
                return appApiService.getNewCosplay(page,size);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DataListRetryWithDelay(3,page,size, DataListRetryWithDelay.NC));
    }




}
