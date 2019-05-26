package com.axelromero.myforecastapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    Button button;
    TextView textView, temperatureView;
    EditText searchInput;
    RecyclerView historyRecycler;
    SupportMapFragment mapFragment;
    HistoryAdapter historyAdapter;
    private GoogleMap mMap;
    List<ForecastModel> forecastModelList = new ArrayList<>();

    String appId = "5f8032c14c8e29b12e31b108aaaa51b4";

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    OpenWeatherMapService weatherService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        historyRecycler= findViewById(R.id.history_recycler);
        weatherService = retrofit.create(OpenWeatherMapService.class);

        button = findViewById(R.id.search_button);
        textView = findViewById(R.id.data_text_view);
        temperatureView= findViewById(R.id.data_temperature);
        searchInput = findViewById(R.id.serch_input);

        historyAdapter = new HistoryAdapter();
        historyRecycler.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        historyRecycler.setAdapter(historyAdapter);

        historyAdapter.setForecastModelList(Utils.loadHistoryList(this));
    }


    private void getForecastData(String query) {
        Call<ForecastModel> call = weatherService.getForecast(query, appId);

        call.enqueue(new Callback<ForecastModel>() {
            @Override
            public void onResponse(Call<ForecastModel> call, Response<ForecastModel> response) {
                if(response.body()!= null){
                    textView.setText(response.body().getAsString());
                    temperatureView.setText(Utils.getTempString(response.body().main.temp));
                    forecastModelList.add(response.body());
                    historyAdapter.addItem(response.body());

                    pinInMap(response.body().name, response.body().coord);
                }
                else {
                    Toast.makeText(MainActivity.this, "Can't find that city.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ForecastModel> call, Throwable t) {
                textView.setText(t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));


        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getForecastData(searchInput.getText().toString());
            }
        });


    }

    private void pinInMap(String name, ForecastModel.Coord coord){
        LatLng poinInMap = new LatLng(coord.lat, coord.lon);
        mMap.addMarker(new MarkerOptions().position(poinInMap).title(name));
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                poinInMap, 12);
        mMap.moveCamera(location);

        Utils.hideKeyboard(MainActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.saveHistoryList(this, historyAdapter.getForecastModelList());
    }
}
