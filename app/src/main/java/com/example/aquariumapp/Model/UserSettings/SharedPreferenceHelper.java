package com.example.aquariumapp.Model.UserSettings;

/*Helper class to access users locally saved data*/
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SharedPreferenceHelper {

    private SharedPreferences sharedPref;

    public SharedPreferenceHelper(Context context){
        sharedPref = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
    }

    //saves user settings into shared preferences
    public void saveUserSettings(UserSettings userSettings){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("hardwareId", userSettings.getHardwareId());
        editor.putString("aquariumName", userSettings.getAquariumName());
        editor.putFloat("minTemp", userSettings.getMinTemp());
        editor.putFloat("maxTemp", userSettings.getMaxTemp());
        editor.putString("tempUnit", userSettings.getTempUnit());
        editor.putFloat("minPh", userSettings.getMinPh());
        editor.putFloat("maxPh", userSettings.getMaxPh());
        editor.putFloat("minSalinity", userSettings.getMinSalinity());
        editor.putFloat("maxSalinity", userSettings.getMaxSalinity());
        editor.putBoolean("notifs", userSettings.getIsNotifEnabled());
        editor.apply();
    }

    //get and set user settings to and from SharedPreferences
    public String getHardwareId() {
        List<UserSettings> aquariums = getList();
        UserSettings userSettings;
        String selectedAquarium = getSelectedAquarium();
        if (aquariums != null) {
            for (int i = 0; i < aquariums.size(); i++) {
                userSettings = aquariums.get(i);
                if (userSettings.getAquariumName().equals(selectedAquarium)) {
                    return userSettings.getHardwareId();
                }
            }
        }
        return null;
    }

    public String getSelectedAquarium() {
        return sharedPref.getString("selectedAquarium", null);
    }

    public void setSelectedAquarium(String name){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("selectedAquarium", name);
        editor.apply();
    }

    public String getAquariumName(){
        List<UserSettings> aquariums = getList();
        UserSettings userSettings;
        String selectedAquarium = getSelectedAquarium();
        if (aquariums != null) {
            for (int i = 0; i < aquariums.size(); i++) {
                userSettings = aquariums.get(i);
                if (userSettings.getAquariumName().equals(selectedAquarium)) {
                    return userSettings.getAquariumName();
                }
            }
        }
        return null;
    }

    public float getMinTemp(){
        List<UserSettings> aquariums = getList();
        UserSettings userSettings;
        String selectedAquarium = getSelectedAquarium();
        if (aquariums != null) {
            for (int i = 0; i < aquariums.size(); i++) {
                userSettings = aquariums.get(i);
                if (userSettings.getAquariumName().equals(selectedAquarium)) {
                    return userSettings.getMinTemp();
                }
            }
        }
        return -1;
    }

    public float getMaxTemp(){
        List<UserSettings> aquariums = getList();
        UserSettings userSettings;
        String selectedAquarium = getSelectedAquarium();
        if (aquariums != null) {
            for (int i = 0; i < aquariums.size(); i++) {
                userSettings = aquariums.get(i);
                if (userSettings.getAquariumName().equals(selectedAquarium)) {
                    return userSettings.getMaxTemp();
                }
            }
        }
        return -1;
    }

    public String getTempUnit() {
        List<UserSettings> aquariums = getList();
        UserSettings userSettings;
        String selectedAquarium = getSelectedAquarium();
        if (aquariums != null) {
            for (int i = 0; i < aquariums.size(); i++) {
                userSettings = aquariums.get(i);
                if (userSettings.getAquariumName().equals(selectedAquarium)) {
                    return userSettings.getTempUnit();
                }
            }
        }
        return null;
    }

    public float getMinPh() {
        List<UserSettings> aquariums = getList();
        UserSettings userSettings;
        String selectedAquarium = getSelectedAquarium();
        if (aquariums != null) {
            for (int i = 0; i < aquariums.size(); i++) {
                userSettings = aquariums.get(i);
                if (userSettings.getAquariumName().equals(selectedAquarium)) {
                    return userSettings.getMinPh();
                }
            }
        }
        return -1;
    }

    public float getMaxPh() {
        List<UserSettings> aquariums = getList();
        UserSettings userSettings;
        String selectedAquarium = getSelectedAquarium();
        if (aquariums != null) {
            for (int i = 0; i < aquariums.size(); i++) {
                userSettings = aquariums.get(i);
                if (userSettings.getAquariumName().equals(selectedAquarium)) {
                    return userSettings.getMaxPh();
                }
            }
        }
        return -1;
    }

    public float getMinSalinity(){
        List<UserSettings> aquariums = getList();
        UserSettings userSettings;
        String selectedAquarium = getSelectedAquarium();
        if (aquariums != null) {
            for (int i = 0; i < aquariums.size(); i++) {
                userSettings = aquariums.get(i);
                if (userSettings.getAquariumName().equals(selectedAquarium)) {
                    return userSettings.getMinSalinity();
                }
            }
        }
        return -1;
    }

    public float getMaxSalinity(){
        List<UserSettings> aquariums = getList();
        UserSettings userSettings;
        String selectedAquarium = getSelectedAquarium();
        if (aquariums != null) {
            for (int i = 0; i < aquariums.size(); i++) {
                userSettings = aquariums.get(i);
                if (userSettings.getAquariumName().equals(selectedAquarium)) {
                    return userSettings.getMaxSalinity();
                }
            }
        }
        return -1;
    }

    public boolean getIsNotifEnabled() {
        List<UserSettings> aquariums = getList();
        UserSettings userSettings;
        String selectedAquarium = getSelectedAquarium();
        if (aquariums != null) {
            for (int i = 0; i < aquariums.size(); i++) {
                userSettings = aquariums.get(i);
                if (userSettings.getAquariumName().equals(selectedAquarium)) {
                    return userSettings.getIsNotifEnabled();
                }
            }
        }
        return false;
    }

    public int getUserNotifId(){
        List<UserSettings> aquariums = getList();
        UserSettings userSettings;
        String selectedAquarium = getSelectedAquarium();
        if (aquariums != null) {
            for (int i = 0; i < aquariums.size(); i++) {
                userSettings = aquariums.get(i);
                if (userSettings.getAquariumName().equals(selectedAquarium)) {
                    return userSettings.getUserNotifId();
                }
            }
        }
        return -1;
    }
   /* public String getAquariumName(){ return sharedPref.getString("aquariumName", null);}

    public int getMinTemp(){return sharedPref.getInt("minTemp", -1); }

    public int getMaxTemp(){ return sharedPref.getInt("maxTemp", -1); }

    public String getTempUnit(){ return sharedPref.getString("tempUnit", null); }

    public int getMinPh(){ return sharedPref.getInt("minPh", -1);}

    public int getMaxPh(){ return sharedPref.getInt("maxPh", -1);}

    public boolean getIsNotifEnabled() { return sharedPref.getBoolean("notifs", false); }*/
//public <UserSettings> void setList(List<UserSettings> aquariumList)
    public void setList(List<UserSettings> aquariumList){
        Gson gson = new Gson();
        String json = gson.toJson(aquariumList);
        set("aquariums", json);
    }

    public void set(String key, String value){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public List<UserSettings> getList(){
        List<UserSettings> aquariumsList;
        String serializedObject = sharedPref.getString("aquariums", null);
        if(serializedObject != null){
            Gson gson = new Gson();
            Type type = new TypeToken<List<UserSettings>>(){}.getType();
            aquariumsList = gson.fromJson(serializedObject, type);
            return aquariumsList;
        }
        return null;
    }

    /*public SharedPreferences getSharedPreferences() { return sharedPref; }*/
}
