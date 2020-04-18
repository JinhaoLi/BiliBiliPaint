package com.jil.paintf.repository;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class UserUpLoadRetry implements Function<Observable<Throwable>, ObservableSource<UserUpLoadInfo>> {
    private int uid;
    public static int retry =3;
    private RetrofitRepository retrofitRepository=RetrofitRepository.getInstance();
    public UserUpLoadRetry(int uid) {
        this.uid =uid;
    }

    @Override
    public ObservableSource<UserUpLoadInfo> apply(Observable<Throwable> throwableObservable) throws Exception {
        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<UserUpLoadInfo>>() {
            @Override
            public ObservableSource<UserUpLoadInfo> apply(Throwable throwable) throws Exception {
                if(retry>=0){
                    retry--;
                    return retrofitRepository.getUserUpLoad(uid);
                }else {
                    return Observable.error(throwable);
                }

            }
        });
    }
}
