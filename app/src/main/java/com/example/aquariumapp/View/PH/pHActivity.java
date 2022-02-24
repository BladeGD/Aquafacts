package com.example.aquariumapp.View.PH;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.aquariumapp.FirebaseData;
import com.example.aquariumapp.FirebaseHelper;
import com.example.aquariumapp.Model.UserSettings.SharedPreferenceHelper;
import com.example.aquariumapp.R;
import com.example.aquariumapp.View.Main.MainActivity;
import com.example.aquariumapp.View.Settings.SettingsHelpActivity;
import com.example.aquariumapp.View.Salinity.SalinityActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.aquariumapp.FirebaseHelper.generateGraph;
import static java.lang.String.valueOf;

public class pHActivity extends AppCompatActivity {

    private SharedPreferenceHelper sharedPrefHelper;
    private FirebaseHelper fb;
    private TextView currentpH;
    private List<Pair<String, Float>> pHTableData = new ArrayList<>();
    private LineChart pHLineChart;
    private LineDataSet pHLineDataSet;
    private List<String> xDates = new ArrayList<>();
    private List<Entry> plotEntries = new ArrayList<>();
    private LineData lineData;
    private TableLayout pHTable;
    private boolean tableVisible;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ph);
        getSupportActionBar().setTitle("pH Activity");
        sharedPrefHelper = new SharedPreferenceHelper(pHActivity.this);
        fb = new FirebaseHelper(sharedPrefHelper.getHardwareId());
        setupUI();
        fb.readAllPh(new FirebaseHelper.FirebaseCallbackAllValues() {
            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            @Override
            public void onSuccess(List<FirebaseData> firebaseData, List<String> keys) {
                plotEntries.clear();
                pHTableData.clear();
                for(int i = 0; i < firebaseData.size(); i++){
                    float pH = firebaseData.get(i).getSensorValue();
                    c.setTimeInMillis(firebaseData.get(i).getTimestamp());
                    SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat fmtTable = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
                    String date = fmt.format(c.getTime());
                    String dateForTable = fmtTable.format(c.getTime());
                    Log.d("GRAPH", "Date " + date);
                    xDates.add(date);
                    plotEntries.add(new Entry(i, pH));
                    pHTableData.add(new Pair<>(dateForTable, pH));
                }
                createGraph();
                pHTable.removeAllViews();
                addHeaders();
                addData();
            }

            @Override
            public void onFailure() {

            }
        });
        //addHeaders();
    }

    private void setupUI() {
        currentpH = findViewById(R.id.currentpHTextView);
        currentpH.setText(getString(R.string.current_pH, "..."));
        // pHGraph = findViewById(R.id.pHGraph);
        pHLineChart = findViewById(R.id.pHLineChart);
        pHLineChart.setNoDataText("Loading data...");
        pHLineChart.setBackgroundColor(Color.WHITE);
        pHTable = findViewById(R.id.dataTable2);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("pH DETAILS");
    }

    protected void onStart() {
        super.onStart();
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        fb.readLatestPh(new FirebaseHelper.FirebaseCallBackLatestValue() {
            @Override
            public void onSuccess(FirebaseData latestValue) {
                currentpH.setText(getString(R.string.current_pH, df.format(latestValue.getSensorValue())));
            }

            @Override
            public void onFailure() {

            }
        });
    }

    public void createGraph(){
        pHLineDataSet = new LineDataSet(plotEntries, "pH");
        lineData = new LineData(pHLineDataSet);
        pHLineChart.setData(lineData);
        pHLineChart.invalidate();
        pHLineChart.refreshDrawableState();
        pHLineChart.getDescription().setEnabled(false);
        pHLineChart.getXAxis().setDrawGridLines(false);
        pHLineChart.getAxisLeft().setDrawGridLines(false);
        pHLineChart.getAxisRight().setDrawGridLines(false);
        pHLineChart.setExtraBottomOffset(40f);

        pHLineDataSet.setColors(ColorTemplate.rgb("800000"));
        pHLineDataSet.setValueTextColor(Color.BLACK);
        pHLineDataSet.setValueTextSize(12f);
        pHLineChart.setVisibleXRangeMaximum(6);
        pHLineChart.setScaleEnabled(false);
        pHLineChart.setPinchZoom(false);
       // pHLineChart.setTouchEnabled(false);

        XAxis xaxis = pHLineChart.getXAxis();
        xaxis.setSpaceMin(1.5f);
        xaxis.setCenterAxisLabels(false);
        xaxis.setLabelRotationAngle(300);
        // xaxis.setLabelCount(6, true); //forces xaxis to freeze with 6 values and only move the line graph
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xaxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if(index < 0 || index >= xDates.size()){
                    return "";
                }
                else{
                    return xDates.get(index);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_help_action_button, menu);
        return true;
    }

    public void OnHelpActionButtonClick(MenuItem item){
        Intent intent = new Intent(this, PHHelpActivity.class);
        startActivity(intent);
    }

    private TextView getTextView(int id, String title, int color, int typeface, int bgColor) {
        TextView tv = new TextView(this);
        tv.setId(id);
        tv.setText(title.toUpperCase());
        tv.setTextColor(color);
        tv.setPadding(40, 40, 40, 40);
        tv.setTypeface(Typeface.DEFAULT, typeface);
        tv.setBackgroundColor(bgColor);
        tv.setLayoutParams(getLayoutParams());
        return tv;
    }

    @NonNull
    private LayoutParams getLayoutParams() {
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.setMargins(2, 0, 0, 2);
        return params;
    }

    @NonNull
    private TableLayout.LayoutParams getTblLayoutParams() {
        return new TableLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
    }

    public void addHeaders() {
        //TableLayout tl = findViewById(R.id.dataTable);
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(getLayoutParams());
        tr.addView(getTextView(0, "Timestamps", Color.WHITE, Typeface.BOLD, Color.parseColor("#0072bc")));
        tr.addView(getTextView(0, "pH", Color.WHITE, Typeface.BOLD, Color.parseColor("#0072bc")));
        pHTable.addView(tr, getTblLayoutParams());
    }

    /**
     * This function add the data to the table
     **/
    public void addData() {

        int size = pHTableData.size();
        //     TableLayout tl = findViewById(R.id.dataTable);
        for(int i = 0; i < pHTableData.size(); i++){
            Pair<String, Float> pair = pHTableData.get(i); //pair.first is date, pair.second is value
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(1);
            String ph = df.format(pair.second);
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(getLayoutParams());
            tr.addView(getTextView(i + 1, String.valueOf(pair.first), Color.WHITE, Typeface.NORMAL, Color.parseColor("#0072bc")));
            tr.addView(getTextView(i + size, ph, Color.WHITE, Typeface.NORMAL, Color.parseColor("#0072bc")));
            pHTable.addView(tr, getTblLayoutParams());
        }
    }

    public void onClickShowTable(View view) {
        TableLayout tl = findViewById(R.id.dataTable2);
        Button button = findViewById(R.id.pHTable);
        if (!tableVisible) {
            tl.setVisibility(View.VISIBLE);
            tableVisible = true;
            button.setText("Hide Log");
        }
        else {
            tl.setVisibility(View.GONE);
            tableVisible = false;
            button.setText("Show Log");
        }
    }
}