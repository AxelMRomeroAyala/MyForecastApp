package com.axelromero.myforecastapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherMapService {

    @GET("/data/2.5/weather")
    Call<ForecastModel> getForecast(@Query("q") String city, @Query("appid") String appId,  @Query("units") String units);

    @GET("/data/2.5/weather")
    Call<ForecastModel> getForecastWithLocation(@Query("appid") String appId,  @Query("units") String units, @Query("lat") double lat, @Query("lon") double lon);
}
