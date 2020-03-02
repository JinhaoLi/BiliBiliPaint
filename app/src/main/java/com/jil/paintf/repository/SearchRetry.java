package com.jil.paintf.repository;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class SearchRetry implements Function<Observable<Throwable>, ObservableSource<SearchRepository>> {
    int page; String keyword; int categid;
    public SearchRetry(int page, String keyword, int categid) {
        this.categid=categid;
        this.keyword=keyword;
        this.page=page;
    }

    @Override
    public ObservableSource<SearchRepository> apply(Observable<Throwable> throwableObservable) throws Exception {
        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<SearchRepository>>() {
            @Override
            public ObservableSource<SearchRepository> apply(Throwable throwable) throws Exception {
                return RetrofitRepository.getInstance().getSearchData(keyword,page,categid);
            }
        });
    }
}
