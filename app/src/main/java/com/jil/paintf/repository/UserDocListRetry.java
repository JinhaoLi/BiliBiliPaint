package com.jil.paintf.repository;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class UserDocListRetry implements Function<Observable<Throwable>, ObservableSource<UserDocListRep>> {
    private RetrofitRepository retrofitRepository=RetrofitRepository.getInstance();
    private int uid,page;
    private String biz;

    public UserDocListRetry(int uid, int page, String biz) {
        this.uid = uid;
        this.page = page;
        this.biz = biz;
    }

    @Override
    public ObservableSource<UserDocListRep> apply(Observable<Throwable> throwableObservable) throws Exception {
        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<UserDocListRep>>() {
            @Override
            public ObservableSource<UserDocListRep> apply(Throwable throwable) throws Exception {
                return retrofitRepository.getUserDocList(uid,page,biz);
            }
        });
    }
}
