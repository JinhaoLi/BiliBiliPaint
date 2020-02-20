package com.jil.paintf.repository;

import com.jil.paintf.network.Client;
import com.jil.paintf.service.AppApiService;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class RecommendRetryWithDelay implements Function<Observable<Throwable>, Observable<DocListRepository>> {
    private int time;
    private int retry=0;
    private AppApiService appApiService;
    private int size;
    private int page;

    public RecommendRetryWithDelay(int time, int page, int size) {
        this.time=time;
        this.page=page;
        this.size=size;
        appApiService =new Client().getRetrofitAppApi().create(AppApiService.class);
    }

    @Override
    public Observable<DocListRepository> apply(Observable<Throwable> throwableObservable) throws Exception {
        return throwableObservable.flatMap(new Function<Throwable, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Throwable throwable){
                if(retry<time){
                    System.out.println("失败重试...");
                    retry++;
                    return appApiService.getRecommedIllust(page,size);
                }else {
                    return Observable.error(throwable);
                }

            }
        });
    }
}
