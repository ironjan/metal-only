package com.github.ironjan.metalonly.client_library;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface MetalOnlyRetrofitApi {
    String BASE_URL  = BuildConfig.METAL_ONLY_API_BASE_URL;

    @GET("mob.php?action=stats")
    Observable<RetrofitStats> getStats();

    @GET("mob.php?action=plannew")
    Observable<Plan> getPlan();

    @GET("track")
    Observable<TrackWrapper> getTrack();
}
