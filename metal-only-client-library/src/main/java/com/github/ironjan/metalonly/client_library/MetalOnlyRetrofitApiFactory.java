package com.github.ironjan.metalonly.client_library;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Cache;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MetalOnlyRetrofitApiFactory {
    Context context;

    public MetalOnlyRetrofitApiFactory(Context context) {
        this.context = context;
    }

    public MetalOnlyRetrofitApi build(){
        int cacheSize = 10 * 1024 * 1024; // 10 MB
        Cache cache = new Cache(context.getExternalCacheDir(), cacheSize);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("https://www.metal-only.de/botcon/")
                .build()
                .create(MetalOnlyRetrofitApi.class);
    }
}
