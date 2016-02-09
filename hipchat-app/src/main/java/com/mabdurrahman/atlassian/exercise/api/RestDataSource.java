package com.mabdurrahman.atlassian.exercise.api;

import com.mabdurrahman.atlassian.exercise.model.ApiStatus;
import com.mabdurrahman.atlassian.exercise.model.ApiURLTitle;
import com.mabdurrahman.atlassian.exercise.model.LinkItem;
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

    private final AlchemyApi endpoint;

    public RestDataSource() {
        OkHttpClient client = new OkHttpClient();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.interceptors().add(httpLoggingInterceptor);
        endpoint = new Retrofit.Builder()
                .baseUrl(AlchemyApi.BASE_URL)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(AlchemyApi.class);
    }

    public Observable<LinkItem> getURLTitle(String url) {
        return endpoint.getURLTitle(url)
                .onErrorReturn(new Func1<Throwable, ApiURLTitle>() {
                    @Override
                    public ApiURLTitle call(Throwable throwable) {
                        ApiURLTitle apiURLTitle = new ApiURLTitle();
                        apiURLTitle.setStatus(ApiStatus.ERROR);
                        apiURLTitle.setTitle("Can't extract title (" + throwable.getMessage() + ")");

                        return apiURLTitle;
                    }
                })
                .compose(new FromApiURLTitleToLinkItem(url));
    }

    public class FromApiURLTitleToLinkItem implements Observable.Transformer<ApiURLTitle, LinkItem> {

        String extractedUrl;

        public FromApiURLTitleToLinkItem(String extractedUrl) {
            this.extractedUrl = extractedUrl;
        }

        @Override
        public Observable<LinkItem> call(Observable<ApiURLTitle> apiObservable) {
            return apiObservable
                    .map(new Func1<ApiURLTitle, LinkItem>() {
                        @Override
                        public LinkItem call(ApiURLTitle apiURLTitle) {
                            return new LinkItem(extractedUrl, apiURLTitle.getTitle());
                        }
                    });
        }
    }
}
