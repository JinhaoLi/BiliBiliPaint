package com.jil.paintf.service;

import com.jil.paintf.repository.DocRepository;
import com.jil.paintf.repository.DocListRepository;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * API
 */
public interface AppApiService {

    @GET("/link_draw/v2/Doc/index?type=recommend")
    Observable<DocListRepository> getRecommedIllust(@Query("page_num") int page, @Query("page_size") int size);

    @GET("/link_draw/v1/doc/detail")
    Observable<DocRepository> getIllustDoc(@Query("doc_id") int doc_id);

    /**
     * https://api.vc.bilibili.com/link_draw/v2/Doc/list?category=all&type=new&page_num=1&page_size=20
     */
    @GET("/link_draw/v2/Doc/list?category=all&type=new")
    Observable<DocListRepository> getNewIllusts(@Query("page_num") int page, @Query("page_size") int size);

    /**
     * https://api.vc.bilibili.com/link_draw/v2/Doc/list?category=all&type=hot&page_num=0&page_size=20
     */
    @GET("/link_draw/v2/Doc/list?category=all&type=hot")
    Observable<DocListRepository> getHotIllusts(@Query("page_num") int page, @Query("page_size") int size);
}
