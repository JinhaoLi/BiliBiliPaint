package com.jil.paintf.repository;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class DocReplyRetryWithDelay implements Function<Observable<Throwable>, Observable<ReplyRepository>> {
    @Override
    public Observable<ReplyRepository> apply(Observable<Throwable> throwableObservable) throws Exception {
        return throwableObservable.flatMap(new Function<Throwable, Observable<ReplyRepository>>() {
            @Override
            public Observable<ReplyRepository> apply(Throwable throwable) {
                return Observable.error(throwable);
            }
        });
    }
}
