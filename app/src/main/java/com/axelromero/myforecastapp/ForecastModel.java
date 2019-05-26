package com.axelromero.myforecastapp;

public class ForecastModel {

    public Coord coord;
    public Main main;
    public String name;

    public class Coord {
        public double lon;
        public double lat;
    }

    public class Main {
        public double temp;
        public double pressure;
        public double humidity;
        public double temp_min;
        public double temp_max;
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
