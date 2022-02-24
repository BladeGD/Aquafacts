package com.example.aquariumapp;

import android.os.Build;
import android.renderscript.Sampler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class FirebaseHelper {

    private FirebaseDatabase mDatabase; //firebase reference
    private DatabaseReference mDatabaseRef; //reference to the unique hardware ID node for the user
    private DatabaseReference mDataRef; //reference to Data node for a device
    private String mHardwareID; //unique Hardware ID passed to the class
    private List<FirebaseData> temps; //list which holds all temp values
    private List<FirebaseData> pHs;
    private Query allTempsQuery;
    private Query latestTempQuery;
    private Query allPhQuery;
    private Query latestPhQuery;
    private ValueEventListener allPhListener;
    private ValueEventListener latestPhListener;
    private ValueEventListener allTempsListener;
    private ValueEventListener latestTempsListener;
    private ValueEventListener allSalinityListener;
    private ValueEventListener latestSalinityListener;
    private int daysToKeepData = 30; //hardcoded for now
    //Strings to access the nodes
    private static final String SENSOR_DATA = "Data";
  //  private static final String TEMP_NODE = "Temp";
    private static final String TEMPERATURE = "temperature";
    private static final String TIMESTAMP = "timestamp";
    private static final String PH = "ph";
    public static final String SALINITY = "salinity";
    private static String TAG = "FirebaseHelper";
    private Query allDataQuery;
    private ValueEventListener allDataListener;
    private Query allSalinityQuery;
    private Query latestSalinityQuery;


    //interface used for callbacks of all sensor values
    public interface FirebaseCallbackAllValues{
        void onSuccess(List<FirebaseData> firebaseData, List<String> keys);
        void onFailure();
    }

    //interface used for callbacks of just latest sensor value
    public interface FirebaseCallBackLatestValue{
        void onSuccess(FirebaseData latestValue);
        void onFailure();
    }

    //interface used for callbacks of just latest sensor value for all sensors
    public interface FirebaseCallBackAllLatestValue{
        void onSuccess(List<FirebaseData> latestValues);
        void onFailure();
    }

    public interface FirebaseCallbackVerifyId{
        void onSuccess(Boolean valid);
        void onFailure();
    }

    //constructor when the hardwareID is known
    public FirebaseHelper(String hardwareID){
        mDatabase = FirebaseDatabase.getInstance();
        mHardwareID = hardwareID;
        mDatabaseRef = mDatabase.getReference(mHardwareID);
        mDataRef = mDatabaseRef.child(SENSOR_DATA);
        temps = new ArrayList<>();
        pHs = new ArrayList<>();
    }

    //constructor when the hardwareID is not known
    public FirebaseHelper(){
        mDatabase = FirebaseDatabase.getInstance();
        mHardwareID = null;
        temps = new ArrayList<>();
        pHs = new ArrayList<>();
    }

    //sets hardware ID and db references using the hardwareID
    public void setHardwareId(String id) {
        mHardwareID = id;
        mDatabaseRef = mDatabase.getReference(mHardwareID);
        mDataRef = mDatabaseRef.child(SENSOR_DATA);
    }

    public void readAllLatestValues(final FirebaseCallBackAllLatestValue dataStatus){
        List<FirebaseData> allLatestData = new ArrayList<>();
        allDataQuery = mDataRef.orderByChild(TIMESTAMP).limitToLast(1);
        allDataListener = allDataQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allLatestData.clear();
                FirebaseData temp = new FirebaseData();
                FirebaseData ph = new FirebaseData();
                FirebaseData salinity = new FirebaseData();
                for(DataSnapshot keyNode: snapshot.getChildren()) {
                    temp.setSensorValue(keyNode.child(TEMPERATURE).getValue(Float.class));
                    ph.setSensorValue(keyNode.child(PH).getValue(Float.class));
                    salinity.setSensorValue(keyNode.child(SALINITY).getValue(Float.class));
                    allLatestData.add(temp);
                    allLatestData.add(ph);
                    allLatestData.add(salinity);
                    dataStatus.onSuccess(allLatestData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error);
                dataStatus.onFailure();

            }
        });
    }

    //reads all the temp values in order of their timestamps
    public void readAllTemps(final FirebaseCallbackAllValues dataStatus){
        allTempsQuery = mDataRef.orderByChild(TIMESTAMP);
        allTempsListener = allTempsQuery.addValueEventListener(new ValueEventListener() {
            @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               temps.clear();
                Log.d(TAG, "size " + temps.size());
               List<String> keys = new ArrayList<>();
               for(DataSnapshot keyNode: snapshot.getChildren()){
                   keys.add(keyNode.getKey());
                   FirebaseData newData = new FirebaseData();
                   Float temp = keyNode.child(TEMPERATURE).getValue(Float.class);
                   long timestamp = keyNode.child(TIMESTAMP).getValue(long.class);
                   newData.setSensorValue(temp);
                   newData.setTimestamp(timestamp);
                   temps.add(newData);
               }
               dataStatus.onSuccess(temps, keys);
                Log.d(TAG, "size " + temps.size());
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {
               Log.d(TAG, "onCancelled: " + error);
               dataStatus.onFailure();
           }
       });
    }

    //reads the latest temp value based on its timestamp
    public void readLatestTemp(final FirebaseCallBackLatestValue dataStatus){
        latestTempQuery = mDataRef.orderByChild(TIMESTAMP).limitToLast(1);
        Log.d(TAG, "readLatestTemp:");
        latestTempsListener = latestTempQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: ");
                for(DataSnapshot keyNode: snapshot.getChildren()) {
                    FirebaseData newData = new FirebaseData();
                    Float temp = keyNode.child(TEMPERATURE).getValue(Float.class);
                    long timestamp = keyNode.child(TIMESTAMP).getValue(long.class);
                    newData.setSensorValue(temp);
                    newData.setTimestamp(timestamp);
                    dataStatus.onSuccess(newData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error);
                dataStatus.onFailure();

            }
        });
    }

    public void readAllPh(final FirebaseCallbackAllValues dataStatus){
        allPhQuery = mDataRef.orderByChild(TIMESTAMP);
        allPhListener = allPhQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pHs.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode: snapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    FirebaseData newData = new FirebaseData();
                    Float pH = keyNode.child(PH).getValue(Float.class);
                    long timestamp = keyNode.child(TIMESTAMP).getValue(long.class);
                    newData.setSensorValue(pH);
                    newData.setTimestamp(timestamp);
                    pHs.add(newData);
                }
                dataStatus.onSuccess(pHs, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error);
                dataStatus.onFailure();
            }
        });
    }

    //reads the latest temp value based on its timestamp
    public void readLatestPh(final FirebaseCallBackLatestValue dataStatus){
        latestPhQuery = mDataRef.orderByChild(TIMESTAMP).limitToLast(1);
        Log.d(TAG, "readLatestpH:");
        latestPhListener = latestPhQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: ");
                for(DataSnapshot keyNode: snapshot.getChildren()) {
                    FirebaseData newData = new FirebaseData();
                    Float pH = keyNode.child(PH).getValue(Float.class);
                    long timestamp = keyNode.child(TIMESTAMP).getValue(long.class);
                    newData.setSensorValue(pH);
                    newData.setTimestamp(timestamp);
                    dataStatus.onSuccess(newData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error);
                dataStatus.onFailure();

            }
        });
    }

    public void readAllSalinity(final FirebaseCallbackAllValues dataStatus){
        allSalinityQuery = mDataRef.orderByChild(TIMESTAMP);
        allSalinityListener = allSalinityQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pHs.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode: snapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    FirebaseData newData = new FirebaseData();
                    Float salinity = keyNode.child(SALINITY).getValue(Float.class);
                    long timestamp = keyNode.child(TIMESTAMP).getValue(long.class);
                    newData.setSensorValue(salinity);
                    newData.setTimestamp(timestamp);
                    pHs.add(newData);
                }
                dataStatus.onSuccess(pHs, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error);
                dataStatus.onFailure();
            }
        });
    }

    public void readLatestSalinity(final FirebaseCallBackLatestValue dataStatus){
        latestSalinityQuery = mDataRef.orderByChild(TIMESTAMP).limitToLast(1);
        Log.d(TAG, "readLatestSalinity:");
        latestSalinityListener = latestSalinityQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: ");
                for(DataSnapshot keyNode: snapshot.getChildren()) {
                    FirebaseData newData = new FirebaseData();
                    Float salinity = keyNode.child(SALINITY).getValue(Float.class);
                    long timestamp = keyNode.child(TIMESTAMP).getValue(long.class);
                    newData.setSensorValue(salinity);
                    newData.setTimestamp(timestamp);
                    dataStatus.onSuccess(newData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error);
                dataStatus.onFailure();

            }
        });
    }


    public void verifyHardwareId(String hardwareId, final FirebaseCallbackVerifyId result){
        mDatabase.getReference().child(hardwareId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                result.onSuccess(snapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.onFailure();
            }
        });
    }

    public void removeListeners(){
        if(latestTempQuery != null && latestTempsListener != null){
            latestTempQuery.removeEventListener(latestTempsListener);
        }
        if(allTempsQuery != null && allTempsListener != null) {
            allTempsQuery.removeEventListener(allTempsListener);
        }
        if(allPhQuery != null && allPhListener != null){
            allPhQuery.removeEventListener(allPhListener);
        }
        if(latestPhQuery != null && latestPhListener != null){
            latestPhQuery.removeEventListener(latestPhListener);
        }
        if(allDataQuery != null && allDataListener != null){
            allDataQuery.removeEventListener(allDataListener);
        }
        if(allSalinityListener != null && allSalinityQuery != null){
            allSalinityQuery.removeEventListener(allSalinityListener);
        }
        if(latestSalinityListener != null && latestSalinityQuery != null){
            latestSalinityQuery.removeEventListener(latestSalinityListener);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void generateGraph(TreeMap<LocalDateTime, Float> ht, LineGraphSeries<DataPoint> series, GraphView tempGraph) {
        if(!ht.isEmpty()) {
            series = new LineGraphSeries<>();
            Set<LocalDateTime> timestamps = ht.keySet();
            for (LocalDateTime ts : timestamps) {
                ZonedDateTime zdt = ts.atZone(ZoneId.systemDefault());
                long x = zdt.toInstant().toEpochMilli();
                float y = ht.get(ts);
                series.appendData(new DataPoint(x, y), true, timestamps.size());
            }
            tempGraph.addSeries(series);
            tempGraph.getGridLabelRenderer().setPadding(30);
            tempGraph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        }
    }

}
