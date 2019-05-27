package com.axelromero.myforecastapp;

import android.content.Context;
import android.location.Location;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityPresenter {

    private OpenWeatherMapService weatherService;
    private MainActInterface mainActInterface;

    private String appId = "5f8032c14c8e29b12e31b108aaaa51b4";

    //Not saving the context should be a reliable Hack here.
    MainActivityPresenter(Context context, MainActInterface mainActInterface){
        weatherService = Utils.generateOpenWeatherMapService();
        this.mainActInterface= mainActInterface;
    }

    void getForecastData(String query) {
        Call<ForecastModel> call = weatherService.getForecast(query, appId, Utils.METRIC);
        mainActInterface.isLoading(true);
        call.enqueue(new Callback<ForecastModel>() {
            @Override
            public void onResponse(Call<ForecastModel> call, Response<ForecastModel> response) {
                if (response.body() != null) {
                    mainActInterface.displayWeatherInfo(response.body());
                } else {
                    mainActInterface.showToast("Can't find that city.");
                }
                mainActInterface.isLoading(false);
            }

            @Override
            public void onFailure(Call<ForecastModel> call, Throwable t) {
                mainActInterface.isLoading(false);
            }
        });
    }

    void getForecastDataWithLocation(Location location) {
        Call<ForecastModel> call = weatherService.getForecastWithLocation(appId, Utils.METRIC, location.getLatitude(), location.getLongitude());
        mainActInterface.isLoading(true);
        call.enqueue(new Callback<ForecastModel>() {
            @Override
            public void onResponse(Call<ForecastModel> call, Response<ForecastModel> response) {
                if (response.body() != null) {
                    mainActInterface.displayWeatherInfo(response.body());
                } else {
                    mainActInterface.showToast("Can't find that city.");
                }
                mainActInterface.isLoading(false);
            }

            @Override
            public void onFailure(Call<ForecastModel> call, Throwable t) {
                mainActInterface.isLoading(false);
            }
        });
    }

    interface MainActInterface {
        void displayWeatherInfo(ForecastModel forecastModel);
        void showToast(String message);
        void isLoading(boolean isLoading);
    }
}
