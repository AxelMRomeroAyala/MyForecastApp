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


public class Utils {

    public enum SharedPreferencesKeys {
        HISTORY_KEY
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
        saveToSharedPreferences(context, SharedPreferencesKeys.HISTORY_KEY, new Gson().toJson(list));
    }

    public static List<ForecastModel> loadHistoryList(Context context){

        List<ForecastModel> list = new Gson().fromJson(getFromSharedPreferences(context, SharedPreferencesKeys.HISTORY_KEY), new TypeToken<List<ForecastModel>>() {}.getType());
        if(list== null){
            list= new ArrayList<>();
        }
        return list;
    }


    public static double kelvinToCelsius(double kelvin){
        return kelvin - 273.15;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getTempString(double temp){
        return round(kelvinToCelsius(temp)) +" °C";
    }

    public static int round(double value) {
        return (int) Math.round(value);
    }
}
