package com.example.aquariumapp.View.Settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aquariumapp.FirebaseHelper;
import com.example.aquariumapp.Model.UserSettings.SharedPreferenceHelper;
import com.example.aquariumapp.Model.UserSettings.UserSettings;
import com.example.aquariumapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private TextInputLayout hardwareIdTextInput;
    private TextInputLayout aquariumNameTextInput;
    private TextInputLayout minTempTextInput;
    private TextInputLayout maxTempTextInput;
    private TextInputLayout minPhTextInput;
    private TextInputLayout maxPhTextInput;
    private TextInputLayout minSalinityTextInput;
    private TextInputLayout maxSalinityTextInput;
    private Button saveSettingsBtn;
    private Button deleteSettingsBtn;
    private Spinner tempUnitSpinner;
    private Switch notifSwitch;
    private SharedPreferenceHelper sharedPrefHelper;
    private FirebaseHelper fb;
    //this is used to disable the edit mode menu item while in edit mode
    private boolean editMenuItemEnabled;
    private static String TAG = "SettingsActivity";
    private String hardwareIdIntent;
    private boolean isFirstTimeIntent;
    private Activity activity;
    private boolean sameAquarium;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPrefHelper = new SharedPreferenceHelper(SettingsActivity.this);
        fb = new FirebaseHelper();
        editMenuItemEnabled = true;
        setupUI();
        Intent intent = getIntent();
        hardwareIdIntent = intent.getStringExtra("hardwareId");
        isFirstTimeIntent = intent.getBooleanExtra("isFirstTime", false);
        activity = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //populate fields from shared pref
        sameAquarium = false;
        populateFieldsFromSharedPref();
        //if no settings saved, then hardware id will be empty string, with length 0
        //if true, we want to be in edit mode by default
        if(hardwareIdTextInput.getEditText().getText().toString().trim().length() == 0){
            //in edit mode set save button to visible
            saveSettingsBtn.setVisibility(View.VISIBLE);
           // deleteSettingsBtn.setVisibility(View.VISIBLE);
            //addAquariumBtn.setVisibility(View.VISIBLE);
            toggleEnableEditTexts(true);
            editMenuItemEnabled = false;
            invalidateOptionsMenu();
        }
        else{
            toggleEnableEditTexts(false);
            editMenuItemEnabled = true;
            invalidateOptionsMenu();
            saveSettingsBtn.setVisibility(View.INVISIBLE);
            deleteSettingsBtn.setVisibility(View.INVISIBLE);
        }
    }

    //fills the fields with values saved into SharedPreferences
    private void populateFieldsFromSharedPref() {
        if(isFirstTimeIntent){
            hardwareIdTextInput.getEditText().setText("");
            aquariumNameTextInput.getEditText().setText("");
            minTempTextInput.getEditText().setText("");
            maxPhTextInput.getEditText().setText("");
            minPhTextInput.getEditText().setText("");
            maxPhTextInput.getEditText().setText("");
            minSalinityTextInput.getEditText().setText("");
            maxSalinityTextInput.getEditText().setText("");
            notifSwitch.setChecked(false);
            deleteSettingsBtn.setVisibility(View.INVISIBLE);
        }
        else{
            hardwareIdTextInput.getEditText().setText(sharedPrefHelper.getHardwareId());
            aquariumNameTextInput.getEditText().setText(sharedPrefHelper.getAquariumName());
            minTempTextInput.getEditText().setText(String.valueOf(sharedPrefHelper.getMinTemp()));
            maxTempTextInput.getEditText().setText(String.valueOf(sharedPrefHelper.getMaxTemp()));
            minPhTextInput.getEditText().setText(String.valueOf(sharedPrefHelper.getMinPh()));
            maxPhTextInput.getEditText().setText(String.valueOf(sharedPrefHelper.getMaxPh()));
            minSalinityTextInput.getEditText().setText(String.valueOf(sharedPrefHelper.getMinSalinity()));
            maxSalinityTextInput.getEditText().setText(String.valueOf(sharedPrefHelper.getMaxSalinity()));
            if(sharedPrefHelper.getTempUnit().equals("F")){
                tempUnitSpinner.setSelection(0);
            }
            else{
                tempUnitSpinner.setSelection(1);
            }
            notifSwitch.setChecked(sharedPrefHelper.getIsNotifEnabled());
        }
        /*hardwareIdTextInput.getEditText().setText(sharedPrefHelper.getHardwareId());
        aquariumNameTextInput.getEditText().setText(sharedPrefHelper.getAquariumName());
        //if its the default value (-1) set to empty string
        if(sharedPrefHelper.getMinTemp() == -1){
            minTempTextInput.getEditText().setText("");
        }
        else{
            minTempTextInput.getEditText().setText(String.valueOf(sharedPrefHelper.getMinTemp()));
        }
        if(sharedPrefHelper.getMaxTemp() == -1){
            maxTempTextInput.getEditText().setText("");
        }
        else{
            maxTempTextInput.getEditText().setText(String.valueOf(sharedPrefHelper.getMaxTemp()));
        }
        if(sharedPrefHelper.getMinPh() == -1){
            minPhTextInput.getEditText().setText("");
        }
        else{
            minPhTextInput.getEditText().setText(String.valueOf(sharedPrefHelper.getMinPh()));
        }
        if(sharedPrefHelper.getMaxPh() == -1){
            maxPhTextInput.getEditText().setText("");
        }
        else{
            maxPhTextInput.getEditText().setText(String.valueOf(sharedPrefHelper.getMaxPh()));
        }
        if(sharedPrefHelper.getTempUnit() != null){
            if(sharedPrefHelper.getTempUnit().equals("F")){
                tempUnitSpinner.setSelection(0);
            }
            else{
                tempUnitSpinner.setSelection(1);
            }
        }*/
       // notifSwitch.setChecked(sharedPrefHelper.getIsNotifEnabled());
    }

    private void setupUI() {

        hardwareIdTextInput = findViewById(R.id.hardwareIdTextInput);
        aquariumNameTextInput = findViewById(R.id.aquariumNameTextInput);
        minTempTextInput = findViewById(R.id.minTempTextInput);
        maxTempTextInput = findViewById(R.id.maxTempTextInput);
        saveSettingsBtn = findViewById(R.id.saveSettingsBtn);
        deleteSettingsBtn = findViewById(R.id.deleteSettingsBtn);
        //addAquariumBtn = findViewById(R.id.addAquariumButton);
        tempUnitSpinner = findViewById(R.id.tempUnitSpinner);
        notifSwitch = findViewById(R.id.notifSwitch);
        minPhTextInput = findViewById(R.id.minPhTextInput);
        maxPhTextInput = findViewById(R.id.maxPhTextInput);
        minSalinityTextInput = findViewById(R.id.minSalinityTextInput);
        maxSalinityTextInput = findViewById(R.id.maxSalinityTextInput);

        //addAquariumBtn.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
                //Creates new edit text

       //     }
       // });
        saveSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInput();
            }
        });

        deleteSettingsBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                deleteAquarium();
                activity.finish();
            }
        });

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Aquarium Settings");

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.temp_units, R.layout.spinner_item);
        tempUnitSpinner.setAdapter(spinnerAdapter);
 /*       tempUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getSelectedItem().toString().startsWith("F")){
                    userSettings.setTempUnit("F");
                }
                else
                    userSettings.setTempUnit("C");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        notifSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    userSettings.setIsNotifEnabled(true);
                else
                    userSettings.setIsNotifEnabled(false);
            }
        });*/

    }

    private void deleteAquarium() {
        List<UserSettings> aquariums = sharedPrefHelper.getList();
        if(aquariums != null){
            for(int i = 0; i < aquariums.size(); i++){
                if(aquariums.get(i).getAquariumName().equals(sharedPrefHelper.getSelectedAquarium())){
                    aquariums.remove(i);
                    break;
                }
            }
            sharedPrefHelper.setList(aquariums);
            sharedPrefHelper.setSelectedAquarium(null);
        }
    }

    //saves user input to sharedPreferences if input is valid
    private void saveInput() {
        String hardwareId = hardwareIdTextInput.getEditText().getText().toString().trim();
        String aquariumName = aquariumNameTextInput.getEditText().getText().toString().trim();
        String minTemp = minTempTextInput.getEditText().getText().toString();
        String maxTemp = maxTempTextInput.getEditText().getText().toString();
        String minPh = minPhTextInput.getEditText().getText().toString();
        String maxPh = maxPhTextInput.getEditText().getText().toString();
        String minSalinity = minSalinityTextInput.getEditText().getText().toString();
        String maxSalinity = maxSalinityTextInput.getEditText().getText().toString();

        float minTempLimit;
        float maxTempLimit;
        if(tempUnitSpinner.getSelectedItem().toString().startsWith("F")){
            minTempLimit = 32;
            maxTempLimit = 140;
        }
        else{
            minTempLimit = 0;
            maxTempLimit = 60;
        }
        //check the input is not empty
        if(!(hardwareId.isEmpty() || aquariumName.isEmpty() || minTemp.isEmpty() || maxTemp.isEmpty() ||
                minPh.isEmpty() || maxPh.isEmpty() ||minSalinity.isEmpty() || maxSalinity.isEmpty())){
            float minTempFloat = Float.parseFloat(minTemp);
            float maxTempFloat = Float.parseFloat(maxTemp);
            float minPhFloat = Float.parseFloat(minPh);
            float maxPhFloat = Float.parseFloat(maxPh);
            float minSalinityFloat = Float.parseFloat(minSalinity);
            float maxSalinityFloat = Float.parseFloat(maxSalinity);

            fb.verifyHardwareId(hardwareId, new FirebaseHelper.FirebaseCallbackVerifyId() {
                @Override
                public void onSuccess(Boolean valid) {
                    List<UserSettings> aquariums = sharedPrefHelper.getList();
                    boolean isNameTaken = false;
                    boolean isIdTaken = false;
                    if(aquariums != null) {
                        for (int i = 0; i < aquariums.size(); i++) {
                            String x = sharedPrefHelper.getSelectedAquarium();
                            if (aquariums.get(i).getAquariumName().equals(aquariumName) &&
                                    (!aquariumName.equals(sharedPrefHelper.getSelectedAquarium()) || isFirstTimeIntent)) {
                                isNameTaken = true;
                            }
                            if(aquariums.get(i).getHardwareId().equals(hardwareId) &&
                                    !aquariums.get(i).getAquariumName().equals(sharedPrefHelper.getSelectedAquarium())){
                                isIdTaken = true;
                            }
                        }
                    }
                    if(!valid){
                        Toast.makeText(getApplicationContext(), "Please enter a valid ID", Toast.LENGTH_LONG).show();
                    }
                    else if(isNameTaken){
                        Toast.makeText(getApplicationContext(), "This aquarium name is already in use, please pick a different one", Toast.LENGTH_LONG).show();
                    }
                    else if(isIdTaken){
                        Toast.makeText(getApplicationContext(), "This hardware ID is already in use for another aquarium", Toast.LENGTH_LONG).show();
                    }
                    else if(minTempFloat < minTempLimit || minTempFloat > maxTempLimit) {
                        if(tempUnitSpinner.getSelectedItem().toString().startsWith("F")){
                            Toast.makeText(getApplicationContext(), R.string.fahrenheit_min_temp_warning, Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), R.string.celsius_min_temp_warning, Toast.LENGTH_LONG).show();
                        }
                    }
                    else if((maxTempFloat < minTempLimit || maxTempFloat > maxTempLimit)){
                        if(tempUnitSpinner.getSelectedItem().toString().startsWith("F")){
                            Toast.makeText(getApplicationContext(), R.string.fahrenheit_max_temp_warning, Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), R.string.celsius_max_temp_warning, Toast.LENGTH_LONG).show();
                        }
                    }
                    else if(minTempFloat >= maxTempFloat){
                        Toast.makeText(getApplicationContext(), "Minimum temperature must be less than maximum temperature", Toast.LENGTH_LONG).show();
                    }
                    else if(minPhFloat >= maxPhFloat){
                        Toast.makeText(getApplicationContext(), "Minimum pH must be less than maximum pH", Toast.LENGTH_LONG).show();
                    }
                    else if(minPhFloat > 14 || maxPhFloat > 14){
                        Toast.makeText(getApplicationContext(), "Please enter pH values between 0 and 14", Toast.LENGTH_LONG).show();
                    }
                    else if(minSalinityFloat >= maxSalinityFloat){
                        Toast.makeText(getApplicationContext(), "Minimum salinity must be less than maximum salinity", Toast.LENGTH_LONG).show();
                    }
                    else if(minSalinityFloat > 1500 || maxSalinityFloat > 1500){
                        Toast.makeText(getApplicationContext(), "Please enter salinity values between 0 and 1500", Toast.LENGTH_LONG).show();
                    }
                    //otherwise save the inputted settings
                    else {
                        boolean isNotifEnabled;
                        String tempUnit;
                        if(tempUnitSpinner.getSelectedItem().toString().startsWith("F")){
                            tempUnit = "F";
                        }
                        else {
                            tempUnit = "C";
                        }
                        if(notifSwitch.isChecked()) {
                            isNotifEnabled = true;
                        }
                        else {
                            isNotifEnabled = false;
                        }
                        //if a hardwareID is already saved, unsubscribe from old hardwareID topic
                       /* if(sharedPrefHelper.getHardwareId() != null){
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(sharedPrefHelper.getHardwareId());
                        }*/

                        if(aquariums == null){
                            aquariums = new ArrayList<>();
                        }
                        int index = -1;
                        int selectedAquariumIndex = -1;
                        for(int i = 0; i < aquariums.size(); i++){
                            if(aquariums.get(i).getAquariumName().equals(aquariumName)){
                                index = i;
                            }
                            if(aquariums.get(i).getAquariumName().equals(sharedPrefHelper.getSelectedAquarium())){
                                selectedAquariumIndex = i;
                            }
                        }
                        UserSettings userSettings = new UserSettings(hardwareId, aquariumName,
                                minTempFloat, maxTempFloat, minPhFloat, maxPhFloat, minSalinityFloat,
                                maxSalinityFloat, tempUnit, isNotifEnabled);
                        //index != -1 means its already been in aquarium list
                        //index = -1 means its a new aquarium
                        if(!isFirstTimeIntent){
                            sameAquarium = true;
                        }
                        if(index != -1){
                            aquariums.add(index, userSettings);
                            aquariums.remove(index+1);
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(sharedPrefHelper.getHardwareId());
                        }
                        else if(sameAquarium && selectedAquariumIndex != -1){
                            aquariums.add(selectedAquariumIndex, userSettings);
                            aquariums.remove(selectedAquariumIndex +1);
                        }
                        else {
                            aquariums.add(userSettings);
                        }
                        sharedPrefHelper.setList(aquariums);
                       /* sharedPrefHelper.saveUserSettings(new UserSettings(hardwareId, aquariumName,
                                minTempInt, maxTempInt, minPhInt, maxPhInt, tempUnit, isNotifEnabled));*/
                        saveSettingsBtn.setVisibility(View.INVISIBLE);
                        deleteSettingsBtn.setVisibility(View.INVISIBLE);
                        isFirstTimeIntent = false;
                        sameAquarium = true;
                        sharedPrefHelper.setSelectedAquarium(aquariumName);
                        //addAquariumBtn.setVisibility(View.INVISIBLE);
                        toggleEnableEditTexts(false);
                        editMenuItemEnabled = true;
                        invalidateOptionsMenu();

                        Log.d(TAG, "onSuccess: Saved settings");
                        FirebaseMessaging.getInstance().subscribeToTopic(hardwareId)
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

                @Override
                public void onFailure() {
                    Toast.makeText(getApplicationContext(), "Error connecting to database", Toast.LENGTH_LONG).show();
                }
            });
            //check the temperature values are within the acceptable range
            /*if((minTempInt < 10 || minTempInt > 50) || (maxTempInt < 10 || maxTempInt > 50)) {
                Toast.makeText(getApplicationContext(), "Please enter a temperature between 10°C and 50°C", Toast.LENGTH_LONG).show();
            }
            //otherwise save the inputted settings
            else {
                sharedPrefHelper.saveUserSettings(new UserSettings(hardwareId, aquariumName, minTempInt, maxTempInt));
                saveSettingsBtn.setVisibility(View.INVISIBLE);
                toggleEnableEditTexts(false);
            }*/
        }
        else{
            Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_LONG).show();
        }
    }

    //edit texts only enabled while in edit mode
    private void toggleEnableEditTexts(boolean enabled) {
        hardwareIdTextInput.getEditText().setEnabled(enabled);
        aquariumNameTextInput.getEditText().setEnabled(enabled);
        minTempTextInput.getEditText().setEnabled(enabled);
        maxTempTextInput.getEditText().setEnabled(enabled);
        tempUnitSpinner.setEnabled(enabled);
        notifSwitch.setEnabled(enabled);
        maxPhTextInput.getEditText().setEnabled(enabled);
        minPhTextInput.getEditText().setEnabled(enabled);
        minSalinityTextInput.getEditText().setEnabled(enabled);
        maxSalinityTextInput.getEditText().setEnabled(enabled);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_action_bar_item, menu);
        getMenuInflater().inflate(R.menu.main_activity_help_action_button, menu);
        return true;
    }

    public void OnHelpActionButtonClick(MenuItem item){
        Intent intent = new Intent(this, SettingsHelpActivity.class);
        startActivity(intent);
    }

    //disable edit mode menu item while in edit mode.. maybe dont need
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_switch_modes);
        item.setEnabled(editMenuItemEnabled);
        item.setVisible(editMenuItemEnabled);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //switch modes from edit mode to view mode
        if(item.getItemId() == R.id.action_switch_modes){
            toggleEnableEditTexts(true);
            saveSettingsBtn.setVisibility(View.VISIBLE);
            deleteSettingsBtn.setVisibility(View.VISIBLE);
            //addAquariumBtn.setVisibility(View.VISIBLE);
            editMenuItemEnabled = !editMenuItemEnabled;
            invalidateOptionsMenu();
            return true;
        }
        //go back to MainActivity
        else if(item.getItemId() == android.R.id.home){
            this.finish();
            return true;
        }
        else{
            return false;
        }
    }

}