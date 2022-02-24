package com.example.aquariumapp.View.Aquarium;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.aquariumapp.Model.UserSettings.SharedPreferenceHelper;
import com.example.aquariumapp.Model.UserSettings.UserSettings;
import com.example.aquariumapp.R;
import com.example.aquariumapp.View.Main.MainActivity;
import com.example.aquariumapp.View.Main.MainHelpActivity;
import com.example.aquariumapp.View.Settings.SettingsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AquariumsActivity extends AppCompatActivity implements AquariumAdapter.OnItemListener {

    private TextView aquariumWelcomeTextView;
    private TextView aquariumPromptTextView;
    private RecyclerView aquariumsRecyclerView;
    private FloatingActionButton addAquariumFab;
    private AquariumAdapter adapter;
    private SharedPreferenceHelper sharedPrefHelper;
    private Button addAquariumButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aquariums);
        sharedPrefHelper = new SharedPreferenceHelper(AquariumsActivity.this);
        setupUI();
       // adapter = new Adapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupUI() {
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("YOUR AQUARIUMS");

     //   aquariumPromptTextView = findViewById(R.id.aquariumPromptTextView);
     //   aquariumWelcomeTextView = findViewById(R.id.aquariumPromptTextView);
        aquariumsRecyclerView = findViewById(R.id.aquariumsRecyclerView);
        aquariumsRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((this));
        aquariumsRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(aquariumsRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        aquariumsRecyclerView.addItemDecoration(divider);
        addAquariumButton = findViewById(R.id.addAquariumButton);


       // addAquariumFab = findViewById(R.id.addAquariumFab);
        addAquariumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AquariumsActivity.this, SettingsActivity.class);
                intent.putExtra("isFirstTime", true);
                startActivity(intent);
            }
        });
    }

    protected void loadRecyclerView(){
        List<UserSettings> usersAquariums = sharedPrefHelper.getList();
        if(usersAquariums != null){
            adapter = new AquariumAdapter(usersAquariums, this, this);
            aquariumsRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    //on courseRecyclerView item click
    public void onItemClick(int position) {
        //get selected course
        UserSettings userSettings = adapter.getAquariumsList().get(position);
        sharedPrefHelper.setSelectedAquarium(userSettings.getAquariumName());
        Intent intent = new Intent(this, MainActivity.class);
        String hardwareId = sharedPrefHelper.getHardwareId();
        //pass courseID to assignmentActivity through intent
        intent.putExtra("hardwareId", userSettings.getHardwareId());
        intent.putExtra("isFirstTime", false);
        //Start assignment activity
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_help_action_button, menu);
        return true;
    }

    public void OnHelpActionButtonClick(MenuItem item) {
        Intent intent = new Intent(this, AquariumHelpActivity.class);
        startActivity(intent);
    }
}