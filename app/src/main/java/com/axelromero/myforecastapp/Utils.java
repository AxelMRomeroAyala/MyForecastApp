package com.axelromero.myforecastapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Utils {

    public enum SharedPreferencesKeys {
        HISTORY_KEY
    }

    public static final String METRIC = "metric";
    public static final String IMPERIAL = "imperial";

    public static OpenWeatherMapService generateOpenWeatherMapService(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(OpenWeatherMapService.class);
    }

    public static void saveToSharedPreferences(Context context, SharedPreferencesKeys key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key.toString(), value);
        editor.apply();
    }

    public static String getFromSharedPreferences(Context context, SharedPreferencesKeys key) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        return sharedPref.getString(key.toString(), "");
    }

    public static void saveHistoryList(Context context, List<ForecastModel> list){
        //Use Gson to convert the list to a string representation, then save it in shared prefs.
        saveToSharedPreferences(context, SharedPreferencesKeys.HISTORY_KEY, new Gson().toJson(list));
    }

    public static List<ForecastModel> loadHistoryList(Context context){
        //Recover the previously saved string representation of the history list, if there were none, return with an empty list.
        List<ForecastModel> list = new Gson().fromJson(getFromSharedPreferences(context, SharedPreferencesKeys.HISTORY_KEY), new TypeToken<List<ForecastModel>>() {}.getType());
        if(list== null){
            list= new ArrayList<>();
        }
        return list;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getTempString(double temp){
        return round(temp) +" °C";
    }

    public static String getHumidityString(double hum){
        return round(hum) +"%";
    }

    public static String getPressureString(double press){
        return round(press) +" hPa";
    }

    public static int round(double value) {
        return (int) Math.round(value);
    }
}
