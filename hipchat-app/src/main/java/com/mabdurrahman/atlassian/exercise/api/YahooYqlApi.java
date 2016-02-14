package com.mabdurrahman.atlassian.exercise.api;

import com.mabdurrahman.atlassian.exercise.model.YahooYqlResponse;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/14/16.
 */
public interface YahooYqlApi {

    String BASE_URL = "https://query.yahooapis.com";

    @GET("/v1/public/yql?format=json&callback=")
    Observable<YahooYqlResponse> getURLTitle(@Query("q") String query);
}
