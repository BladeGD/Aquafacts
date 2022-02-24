package com.example.aquariumapp.View.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aquariumapp.View.Aquarium.AquariumListActivity;
import com.example.aquariumapp.FirebaseData;
import com.example.aquariumapp.FirebaseHelper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aquariumapp.R;
import com.example.aquariumapp.Model.UserSettings.SharedPreferenceHelper;
import com.example.aquariumapp.View.PH.pHActivity;
import com.example.aquariumapp.View.Salinity.SalinityActivity;
import com.example.aquariumapp.View.Settings.SettingsActivity;
import com.example.aquariumapp.View.Temperature.TempActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeMessage;
    private Button statusButton;
    private Button tempButton;
    private Button phButton;
    private Button salinityButton;

    private FirebaseHelper fb;
    private String hardwareId;// = "UniqueID"; //we probably are going to get this from local db but for now its hardcoded
    private SharedPreferenceHelper sharedPrefHelper;
    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPrefHelper = new SharedPreferenceHelper(MainActivity.this);
        fb = new FirebaseHelper();
        Intent intent = getIntent();
        if(intent != null && intent.getStringExtra("aquariumWithNotif") != null){
            sharedPrefHelper.setSelectedAquarium(intent.getStringExtra("aquariumWithNotif"));
        }
        setupUI();
        //if we have a hardware id, subscribe to topic, otherwise we do it in settingsActivity
        if(sharedPrefHelper.getHardwareId() != null){
            FirebaseMessaging.getInstance().subscribeToTopic(sharedPrefHelper.getHardwareId())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                Log.d(TAG, "onComplete: unsuccesful");
                            }
                            else{
                                Log.d(TAG, "onComplete: success");
                            }
                        }
                    });
        }
    }

    private void setupUI() {
        /*assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("HOME");*/
        welcomeMessage = findViewById(R.id.WelcomeMessage);
        statusButton = findViewById(R.id.button_status);
        statusButton.setTextColor(Color.GREEN);
       // statusButton.setText(getString(R.string.status, "..."));
        tempButton = findViewById(R.id.button_temp);
        tempButton.setTextColor(Color.GREEN);
        tempButton.setText(getString(R.string.temperature_button_c, "..."));
        phButton = findViewById(R.id.button_ph);
        phButton.setText(getString(R.string.ph_button, "..."));
        salinityButton = findViewById(R.id.button_salinity);
        salinityButton.setText(getString(R.string.salinity_button, "..."));
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTempActivity();
            }
        });
        phButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openpHActivity();
            }
        });
        salinityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSalinityActivity();
            }
        });
        //for now we hardcode the color to green cuz we want happy thoughts
        phButton.setTextColor(Color.GREEN);
        salinityButton.setTextColor(Color.GREEN);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("PARAMETERS");
    }

    protected void onStart() {
        super.onStart();
        //check for userSettings onStart
        boolean activityFinished = false;
        if(sharedPrefHelper.getSelectedAquarium() == null){
            this.finish();
            activityFinished = true;
        }
        if(!activityFinished) {
            checkForUserSettings();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(hardwareId != null) {
            fb.readAllLatestValues(new FirebaseHelper.FirebaseCallBackAllLatestValue() {
                @Override
                //the list holds the data in the order: temp, ph, salinity
                public void onSuccess(List<FirebaseData> latestValues) {
                    boolean isHealthy = true;
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(1);
                    float temp = latestValues.get(0).getSensorValue();
                    float ph = latestValues.get(1).getSensorValue();
                    float salinity = latestValues.get(2).getSensorValue();
                    if (sharedPrefHelper.getTempUnit().equals("F")) {
                        temp = latestValues.get(0).tempToFahrenheit();
                    }
                    //check if its in the right range, if so --> healthy
                    if (temp <= sharedPrefHelper.getMaxTemp() && temp >= sharedPrefHelper.getMinTemp()) {
                        tempButton.setTextColor(Color.GREEN);
                    } else {
                        isHealthy = false;
                        tempButton.setTextColor(Color.RED);
                    }
                    if (sharedPrefHelper.getTempUnit().equals("F")) {
                        tempButton.setText(getString(R.string.temperature_button_f, df.format(temp)));
                    } else {
                        tempButton.setText(getString(R.string.temperature_button_c, df.format(temp)));
                    }
                    phButton.setText(getString(R.string.ph_button, df.format(ph)));
                    //if its out of range, set isHealthy to false
                    if (ph > sharedPrefHelper.getMaxPh() || ph < sharedPrefHelper.getMinPh()) {
                        isHealthy = false;
                        phButton.setTextColor(Color.RED);
                    } else {
                        phButton.setTextColor(Color.GREEN);
                    }
                    salinityButton.setText(getString(R.string.salinity_button, df.format(salinity)));
                    if(salinity > sharedPrefHelper.getMaxSalinity() || salinity < sharedPrefHelper.getMinSalinity()){
                        isHealthy = false;
                        salinityButton.setTextColor(Color.RED);
                    } else{
                        salinityButton.setTextColor(Color.GREEN);
                    }
                    setStatusColor(isHealthy);
                }

                @Override
                public void onFailure() {
                    Toast.makeText(getApplicationContext(), "Error displaying temperature", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //https://stackoverflow.com/questions/47159707/how-to-remove-listener-from-firebase-realtime-database/53616431#:~:text=The%20correct%20way%20to%20remove,to%20remove%20it%20in%20onStop%20.
        //Note that, if you have added the listener in onStart you have to remove it in onStop.
        // If you have added the listener in onResume you have to remove it in onPause. If you have
        // added the listener in onCreate you have to remove it in onDestroy.
        fb.removeListeners();
    }

    //check there are user settings, if not go to SettingsActivity
    private void checkForUserSettings() {
        //if nothing saved to sharedPref then hardwareID will be null
        if(sharedPrefHelper.getHardwareId() == null){
            goToSettingsActivity();
            //HERE GOES CODE TO LAUNCH AQUARIUMS THEN SETTINGS IN THE RIGHT ORDER IF NON-EXISTANT
        }
        else{
            hardwareId = sharedPrefHelper.getHardwareId();
            fb.setHardwareId(sharedPrefHelper.getHardwareId());
            welcomeMessage.setText(getString(R.string.welcome_message, sharedPrefHelper.getAquariumName()));
        }
    }

    private void goToSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void goToAquariumsActivity(){
        Intent intent = new Intent(this, AquariumListActivity.class);
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_action_bar_item, menu);
        getMenuInflater().inflate(R.menu.main_activity_help_action_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.go_to_settings){
            goToSettingsActivity();
            return true;
        }
       /* else if(item.getItemId() == R.id.go_to_aquariums){
            goToAquariumsActivity();
            return true;
        }*/
        else if(item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        else
            return false;
    }

    public void setStatusColor(boolean isHealthy){
        if(isHealthy){
            statusButton.setText(R.string.status_healthy);
            statusButton.setTextColor(Color.GREEN);
        }
        else{
            statusButton.setText(R.string.status_warning);
            statusButton.setTextColor(Color.RED);
        }
    }

    public void openTempActivity(){
        Intent intent = new Intent(this, TempActivity.class);
        startActivity(intent);
    }

    public void openpHActivity() {
        Intent intent = new Intent(this, pHActivity.class);
        startActivity(intent);
    }

    public void openSalinityActivity() {
        Intent intent = new Intent(this, SalinityActivity.class);
        startActivity(intent);
    }


    public void OnStatusButtonClick(View view){
    }

    public void OnTemperatureButtonClick(View view){

    }

    public void OnPhButtonClick(View view){
    }

    public void OnSalinityButtonClick(View view){
        // TO COMPLETE
    }


    public void OnHelpActionButtonClick(MenuItem item) {
        Intent intent = new Intent(this, MainHelpActivity.class);
        startActivity(intent);
    }
}