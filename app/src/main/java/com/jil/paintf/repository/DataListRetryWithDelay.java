package com.jil.paintf.repository;

import com.jil.paintf.network.BaseNetClient;
import com.jil.paintf.service.AppApiService;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

import static com.jil.paintf.viewmodel.MainViewModel.*;

public class DataListRetryWithDelay implements Function<Observable<Throwable>, Observable<DocListRepository>> {
    private int time;
    private int retry=0;
    private AppApiService appApiService;
    private int size;
    private int page;
    int type;

    public DataListRetryWithDelay(int time, int page, int size, int type) {
        this.time=time;
        this.page=page;
        this.size=size;
        this.type=type;
        appApiService =new BaseNetClient().getApiVcBiliClient().create(AppApiService.class);
    }

    @Override
    public Observable<DocListRepository> apply(Observable<Throwable> throwableObservable) throws Exception {
        return throwableObservable.flatMap(new Function<Throwable, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Throwable throwable){
                if(retry<time){
                    System.out.println("失败重试...");
                    retry++;
                    switch(type){
                        case RI:
                            return appApiService.getRecommedIllust(page,size);
                        case NI:
                            return appApiService.getNewIllusts(page,size);
                        case HI:
                            return appApiService.getHotIllusts(page,size);
                        case RC:
                            return appApiService.getRecommedCosplay(page,size);
                        case NC:
                            return appApiService.getNewCosplay(page,size);
                        case HC:
                            return appApiService.getHotCosplay(page,size);
                        default:
                            return Observable.error(throwable);
                    }

                }else {
                    return Observable.error(throwable);
                }

            }
        });
    }
}
