package com.example.aquariumapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.aquariumapp.Model.UserSettings.SharedPreferenceHelper;
import com.example.aquariumapp.Model.UserSettings.UserSettings;
import com.example.aquariumapp.View.Main.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FirebaseService extends FirebaseMessagingService {

    public static final String TAG = "FirebaseService";
    public SharedPreferenceHelper sharedPrefHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPrefHelper = new SharedPreferenceHelper(this);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "onNewToken: ");
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: ");
        String topic = remoteMessage.getFrom();
        int userNotifId = -1;
        String selectedAquarium = sharedPrefHelper.getSelectedAquarium(); //save selected aquarium to a variable
        List<UserSettings> aquariumsList = sharedPrefHelper.getList(); //fetch the list of aquariums from shared pref
        if(aquariumsList != null && topic != null) {
            for(int i = 0; i < aquariumsList.size(); i++){
                //check if any of the aquariums are using the hardwareID that is the topic
                if(aquariumsList.get(i).getHardwareId().equals(topic.substring(8))){
                    UserSettings userSettings = aquariumsList.get(i);
                    if(sharedPrefHelper.getIsNotifEnabled()){
                        sharedPrefHelper.setSelectedAquarium(aquariumsList.get(i).getAquariumName());
                    }
                }
            }
            if (sharedPrefHelper.getIsNotifEnabled()) {
                userNotifId = sharedPrefHelper.getUserNotifId();
                List<String> warnings = new ArrayList<>();
                float temp = Float.parseFloat(remoteMessage.getData().get("temp"));
                float ph = Float.parseFloat(remoteMessage.getData().get("ph"));
                float salinity = Float.parseFloat(remoteMessage.getData().get("salinity"));
                if (sharedPrefHelper.getTempUnit().equals("F")) {
                    temp = tempToFahrenheit(temp);
                }
                if (temp < sharedPrefHelper.getMinTemp() || temp > sharedPrefHelper.getMaxTemp()) {
                    warnings.add("temperature");
                   // showNotification(remoteMessage.getData(), "temperature", sharedPrefHelper.getAquariumName());
                }
                if (ph > sharedPrefHelper.getMaxPh() || ph < sharedPrefHelper.getMinPh()) {
                    warnings.add("pH");
                  //  showNotification(remoteMessage.getData(), "pH", sharedPrefHelper.getAquariumName());
                }
                if(salinity > sharedPrefHelper.getMaxSalinity() || salinity < sharedPrefHelper.getMinSalinity()){
                    warnings.add("salinity");
                }
                if(!warnings.isEmpty()) {
                    showNotification(remoteMessage.getData(), warnings,
                            sharedPrefHelper.getAquariumName(), userNotifId);
                }
            }
            sharedPrefHelper.setSelectedAquarium(selectedAquarium);
        }
    }

    private void showNotification(Map<String, String> data, List<String> type, String aquariumName,
                                  int userNotifId) {

        String title = getString(R.string.warning_title);
        String body = "";

        if(type.size() == 1){
            body = "The " + type.get(0) + " for your aquarium \"" +aquariumName+ "\" is out of range";
        }
        else if(type.size() == 2){
            body = "The " + type.get(0) + " and " + type.get(1) + " for your aquarium \"" +aquariumName+ "\" are out of range";
        }
        else if(type.size() == 3){
            body = "The " + type.get(0) + ", " + type.get(1) + ", and " + type.get(2) + " for your aquarium \"" +aquariumName+ "\" are out of range";
        }
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID ="test notif";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Temp channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{1,1000,500,1000});
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("aquariumWithNotif", aquariumName);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info")
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle());

        notificationManager.notify(userNotifId, notificationBuilder.build());
    }

    public Float tempToFahrenheit(float celsiusValue){
        return ((celsiusValue *9)/5 + 32);
    }
}
