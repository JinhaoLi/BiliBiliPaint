package com.jil.paintf.repository;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class UserInfoRetry implements Function<Observable<Throwable>,ObservableSource<UserInfo>> {
    private int mid;
    public UserInfoRetry(int mid) {
        this.mid=mid;
    }

    @Override
    public ObservableSource<UserInfo> apply(Observable<Throwable> throwableObservable) throws Exception {
        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<UserInfo>>() {
            @Override
            public ObservableSource<UserInfo> apply(Throwable throwable) throws Exception {
                return RetrofitRepository.getInstance().getUserInfo(mid);
            }
        });
    }
}
