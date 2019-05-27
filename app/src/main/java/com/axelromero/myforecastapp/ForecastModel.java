package com.axelromero.myforecastapp;

public class ForecastModel {

    Coord coord;
    Main main;
    Sys sys;
    String name;
    int id;

    class Coord {
        double lon;
        double lat;
    }

    public class Main {
        double temp;
        double pressure;
        double humidity;
        double temp_min;
        double temp_max;
    }

    class Sys{
        String country;
    }

    public String getAsString() {
        String string = "";

        string = string +
                name.toUpperCase()+ "\n" +
                "Humidity: " + main.humidity + "\n" +
                "Pressure: " + main.pressure + "\n" +
                "Temperature: " + main.temp + "\n" +
                "Max Temp. " + main.temp_max + "\n" +
                "Min Temp. " + main.temp_min;
        return string;
    }
}
