package com.example.aquariumapp.View.Aquarium;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aquariumapp.R;

import java.util.ArrayList;

public class AquariumListActivity extends AppCompatActivity {

    private static ArrayList<android.view.View> buttons = new ArrayList<android.view.View>();
    //private TextInputLayout aquariumNameButton;
    //private SharedPreferenceHelper sharedPrefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);

        int defaultBackgroundColor = Color.parseColor("#0072BC");

        SharedPreferences.Editor editor = sharedPref.edit();

        if(!sharedPref.contains("current_aquarium"))
        {
            editor.putInt("current_aquarium",0);
        }


       // sharedPrefHelper = new SharedPreferenceHelper(AquariumListActivity.this);
       // aquariumNameButton.getEditText().setText(sharedPrefHelper.getAquariumName());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aquarium_list);

        int[] buttonIDs = {
                R.id.aquarium1_button,
                R.id.aquarium2_button,
                R.id.aquarium3_button,
                R.id.aquarium4_button,
                R.id.aquarium5_button,
        };

        int current_aquarium = buttonIDs[0];
        current_aquarium = sharedPref.getInt("current_aquarium", current_aquarium);
        Button current_aquarium_button = findViewById(current_aquarium);
        current_aquarium_button.setText(sharedPref.getString("aquariumName", "AQUARIUM_1"));


        for(int i = 0; i < buttonIDs.length; i++)
        {
            Button activity_Select = findViewById(buttonIDs[i]);
            buttons.add(activity_Select);
            int index = buttonIDs[i];
            activity_Select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity_Select.setBackgroundColor(Color.GRAY);
                    for(android.view.View b : buttons)
                    {
                        if(b == activity_Select)
                            continue;
                        b.setBackgroundColor(defaultBackgroundColor);
                        editor.putInt("current_aquarium", index);
                        editor.putString("aquariumName", activity_Select.getText().toString());
                        editor.commit();
//                        System.out.println("current aquarium at button press: " + sharedPref.getInt("current_aquarium", 0));
                    }
                }
            });
            if(current_aquarium == index)
            {
                activity_Select.performClick();
            }
        }
        setupActionbar();
    }

    private void setupActionbar() {
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Aquariums");
    }


}