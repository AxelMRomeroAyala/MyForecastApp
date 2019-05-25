package com.axelromero.myforecastapp;

public class ForecastModel {

    public Coord coord;
    public Main main;

    public class Coord{
        public double lon;
        public double lat;
    }

    public class Main{
        public double temp;
        public double pressure;
        public double humidity;
        public double temp_min;
        public double temp_max;
    }
}
