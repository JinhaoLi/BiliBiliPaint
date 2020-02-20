package com.jil.paintf.repository;

import com.jil.paintf.network.Client;
import com.jil.paintf.service.AppApiService;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class DocRetryWithDelay implements Function<Observable<Throwable>, Observable<DocRepository>> {
    private int time;
    private int retry=0;
    private AppApiService appApiService;
    private int id;

    public DocRetryWithDelay(int time, int id) {
        this.time=time;
        this.id=id;
        appApiService =new Client().getRetrofitAppApi().create(AppApiService.class);
    }

    @Override
    public Observable<DocRepository> apply(Observable<Throwable> throwableObservable) throws Exception {
        return throwableObservable.flatMap(new Function<Throwable, Observable<DocRepository>>() {
            @Override
            public Observable<DocRepository> apply(Throwable throwable){
                if(retry<time){
                    System.out.println("失败重试...");
                    retry++;
                    return appApiService.getIllustDoc(id);
                }else {
                    return Observable.error(throwable);
                }

            }
        });
    }
}
