package com.example.aquariumapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FirebaseData {

    private Float sensorValue;
    private long timestamp;

    public FirebaseData(){
        sensorValue = (float)0;
        timestamp = 0;
    }
    public FirebaseData(Float sensorValue, long timestamp){
        this.sensorValue = sensorValue;
        this.timestamp = timestamp;
    }

    public Float getSensorValue() {
        return sensorValue;
    }

    public void setSensorValue(Float sensorValue) {
        this.sensorValue = sensorValue;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    //change how the date is converted if there's a better way
    public String convertTimestamp(){
        try {
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            return sfd.format(new Date(timestamp));
        } catch(Exception e){
            return "date";
        }
    }

    public Float tempToFahrenheit(){
        return ((sensorValue *9)/5 + 32);
    }
}
