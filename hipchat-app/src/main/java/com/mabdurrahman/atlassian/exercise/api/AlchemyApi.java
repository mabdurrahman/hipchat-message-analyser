package com.mabdurrahman.atlassian.exercise.api;

import com.mabdurrahman.atlassian.exercise.model.ApiURLTitle;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/9/16.
 */
public interface AlchemyApi {

    String API_KEY = "b197025ef676fedcdfbc14901b8d253e01609639";
    String BASE_URL = "http://gateway-a.watsonplatform.net";

    @GET("/calls/url/URLGetTitle?apikey=" + API_KEY + "&outputMode=json")
    Observable<ApiURLTitle> getURLTitle(@Query("url") String url);
}
