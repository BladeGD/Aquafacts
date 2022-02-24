package com.example.aquariumapp.Model.UserSettings;

/*Class to model the user settings*/
public class UserSettings {

    private String hardwareId;
    private String aquariumName;
    private float minTemp;
    private float maxTemp;
    private float minPh;
    private float maxPh;
    private float minSalinity;
    private float maxSalinity;
    private String tempUnit; //will be F or C
    private boolean isNotifEnabled;
    private static int notifId =0;
    private int usersNotifId;
    //  private boolean tempNotifications;

    public UserSettings(String hardwareId, String aquariumName, float minTemp, float maxTemp, float minPh,
                        float maxPh, float minSalinity, float maxSalinity, String tempUnit,
                        boolean isNotifEnabled) {
        this.hardwareId = hardwareId;
        this.aquariumName = aquariumName;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minPh = minPh;
        this.maxPh = maxPh;
        this.minSalinity = minSalinity;
        this.maxSalinity = maxSalinity;
        this.tempUnit = tempUnit;
        this.isNotifEnabled = isNotifEnabled;
        usersNotifId = notifId++;
    }

    public String getTempUnit() {
        return tempUnit;
    }

    public String getHardwareId() {
        return hardwareId;
    }

    public String getAquariumName() {
        return aquariumName;
    }

    public float getMinTemp() {
        return minTemp;
    }

    public float getMaxTemp() {
        return maxTemp;
    }

    public float getMinPh() {
        return this.minPh;
    }

    public float getMaxPh() {
        return this.maxPh;
    }

    public boolean getIsNotifEnabled() {
        return this.isNotifEnabled;
    }

    public float getMinSalinity() {
        return this.minSalinity;
    }

    public float getMaxSalinity() {
        return this.maxSalinity;
    }

    public int getUserNotifId(){
        return usersNotifId;
    }
}
