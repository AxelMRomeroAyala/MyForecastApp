package com.axelromero.myforecastapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MainActivityPresenter.MainActInterface, OnMapReadyCallback, HistoryInteractor, View.OnClickListener{

    public static final int PERMISSIONS_REQUEST_LOCATION = 123;

    private ImageView searchButton;
    private ImageButton currentLocation;
    private TextView textView, temperatureView, humidityView, pressureView, maxView, minView;
    private EditText searchInput;
    private RecyclerView historyRecycler;
    private SupportMapFragment mapFragment;
    private LinearLayout bottomSheet;
    private HistoryAdapter historyAdapter;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private Criteria criteria;
    private BottomSheetBehavior behavior;

    private MainActivityPresenter mainActivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivityPresenter= new MainActivityPresenter(getBaseContext(),this);


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        historyRecycler = findViewById(R.id.history_recycler);
        bottomSheet = findViewById(R.id.bottom_sheet);
        searchButton = findViewById(R.id.search_button);
        textView = findViewById(R.id.data_text_view);
        temperatureView = findViewById(R.id.data_temperature);
        humidityView = findViewById(R.id.data_humidity);
        pressureView = findViewById(R.id.data_pressure);
        maxView = findViewById(R.id.data_max);
        minView = findViewById(R.id.data_min);
        searchInput = findViewById(R.id.search_input);
        currentLocation= findViewById(R.id.center_map_current_location);

        historyAdapter = new HistoryAdapter(getBaseContext(), this);
        historyRecycler.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        historyRecycler.setAdapter(historyAdapter);
        historyAdapter.setForecastModelList(Utils.loadHistoryList(this));

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
        behavior = (BottomSheetBehavior) params.getBehavior();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
    }

    @Override
    public void displayWeatherInfo(ForecastModel model) {
        textView.setVisibility(View.VISIBLE);
        textView.setText(model.name.toUpperCase() + ", " + model.sys.country);
        temperatureView.setText(Utils.getTempString(model.main.temp));
        humidityView.setText(getString(R.string.humidity) + " " + Utils.getHumidityString(model.main.humidity));
        pressureView.setText(getString(R.string.pressure) + " " + Utils.getPressureString(model.main.pressure));
        minView.setText(getString(R.string.min_temp) + " " + Utils.getTempString(model.main.temp_min));
        maxView.setText(getString(R.string.max_temp) + " " + Utils.getTempString(model.main.temp_max));
        historyAdapter.addItem(model);
        pinInMap(model.name, model.coord);
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        searchInput.setText("");
        Utils.hideKeyboard(MainActivity.this);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void manageBottomSheet() {
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
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

        searchButton.setOnClickListener(this);
        bottomSheet.setOnClickListener(this);
        textView.setOnClickListener(this);
        currentLocation.setOnClickListener(this);

        loadLastSearchedCity();
    }

    private void pinInMap(String name, ForecastModel.Coord coord) {
        mMap.clear();
        LatLng pointInMap = new LatLng(coord.lat, coord.lon);
        mMap.addMarker(new MarkerOptions().position(pointInMap).title(name));
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                pointInMap, 12);
        mMap.moveCamera(location);
    }

    private void loadLastSearchedCity() {
        if (historyAdapter.getForecastModelList() != null && !historyAdapter.getForecastModelList().isEmpty()) {
            displayWeatherInfo(historyAdapter.getForecastModelList().get(historyAdapter.getForecastModelList().size() - 1));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.saveHistoryList(this, historyAdapter.getForecastModelList());
    }

    @Override
    public void onCitySelected(ForecastModel model) {
        displayWeatherInfo(model);
    }

    public void getCurrentLocationWeather() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);

            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        mainActivityPresenter.getForecastDataWithLocation(location);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocationWeather();
                } else {
                    Toast.makeText(getBaseContext(), "You Rejected the Required permission", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.search_button:
                mainActivityPresenter.getForecastData(searchInput.getText().toString());
                break;
            case R.id.bottom_sheet:
                manageBottomSheet();
                break;
            case R.id.data_text_view:
                loadLastSearchedCity();
                break;
            case R.id.center_map_current_location:
                getCurrentLocationWeather();
                break;
        }
    }

}
