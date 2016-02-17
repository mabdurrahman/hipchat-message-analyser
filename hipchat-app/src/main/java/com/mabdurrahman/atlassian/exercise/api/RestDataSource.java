package com.mabdurrahman.atlassian.exercise.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mabdurrahman.atlassian.exercise.model.LinkItem;
import com.mabdurrahman.atlassian.exercise.model.YahooYqlResponse;
import com.mabdurrahman.atlassian.exercise.utils.YahooYqlUtils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/9/16.
 */
public class RestDataSource {

    private final YahooYqlApi endpoint;

    public RestDataSource() {
        OkHttpClient client = new OkHttpClient();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.interceptors().add(httpLoggingInterceptor);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);

        endpoint = new Retrofit.Builder()
                .baseUrl(YahooYqlApi.BASE_URL)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(YahooYqlApi.class);
    }

    public Observable<LinkItem> getURLTitle(String url) {
        return endpoint.getURLTitle(YahooYqlUtils.getHtmlTitleQuery(url))
                .retry(3)
                .onErrorResumeNext(new Func1<Throwable, Observable<YahooYqlResponse>>() {
                    @Override
                    public Observable<YahooYqlResponse> call(Throwable throwable) {
                        YahooYqlResponse response = new YahooYqlResponse();
                        response.setCount(0);
                        response.setTitleResult(new YahooYqlResponse.TitleResult("Can't extract title (" + throwable.getMessage() + ")"));

                        return Observable.just(response);
                    }
                })
                .compose(new FromYqlResponseToLinkItem(url));
    }

    public class FromYqlResponseToLinkItem implements Observable.Transformer<YahooYqlResponse, LinkItem> {

        String extractedUrl;

        public FromYqlResponseToLinkItem(String extractedUrl) {
            this.extractedUrl = extractedUrl;
        }

        @Override
        public Observable<LinkItem> call(Observable<YahooYqlResponse> apiObservable) {
            return apiObservable
                    .map(new Func1<YahooYqlResponse, LinkItem>() {
                        @Override
                        public LinkItem call(YahooYqlResponse yahooYqlResponse) {
                            if (yahooYqlResponse == null || yahooYqlResponse.getCount() == 0 || yahooYqlResponse.getTitleResult() == null)
                                return new LinkItem(extractedUrl, "");

                            return new LinkItem(extractedUrl, yahooYqlResponse.getTitleResult().getTitle());
                        }
                    });
        }
    }
}
