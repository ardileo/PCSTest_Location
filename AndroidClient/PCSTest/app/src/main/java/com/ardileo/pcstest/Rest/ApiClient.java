package com.ardileo.pcstest.Rest;

import android.content.Context;

import com.ardileo.pcstest.R;
import com.ardileo.pcstest.Utils.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static Retrofit retrofit = null;
    private Builder builder;

    protected ApiClient() {
    }

    public ApiClient(Context mContext) {
        new Builder(mContext).build();
    }

    private static class Header {
        public String key, value;

        public Header(String k, String v) {
            this.key = k;
            this.value = v;
        }
    }

    public static class Builder extends ApiClient {
        protected Context context;
        protected List<Header> headers;
        Header Accept = new Header("Accept", "application/json");
        Header ContentType = new Header("Content-Type", "application/json");
        Header TokenAuth = new Header("Authorization", "none");

        public Builder(@NonNull Context context) {
            this.context = context;
            TokenAuth.value = new SessionManager(context).getAuthToken();
            headers = new ArrayList<>();
            headers.add(Accept);
            headers.add(ContentType);
            headers.add(TokenAuth);
            if (TokenAuth.value.equals("none")) {
                headers.remove(TokenAuth);
            }
        }

        public ApiClient build() {
            String baseUrl = context.getString(R.string.base_url);
            OkHttpClient.Builder clientB = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request.Builder request = chain.request().newBuilder();
                    for (Header h : headers) {
                        request.header(h.key, h.value);
                    }
                    return chain.proceed(request.build());
                }
            });
            OkHttpClient client = clientB.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create()).build();
            return new ApiClient();
        }

    }

    public ApiEndPoint getInstance() {
        if (retrofit == null) {
            builder.build();
        }
        return retrofit.create(ApiEndPoint.class);
    }


}
