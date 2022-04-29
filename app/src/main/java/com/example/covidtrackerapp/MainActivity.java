package com.example.covidtrackerapp;

import static android.content.ContentValues.TAG;
import static java.util.Collections.addAll;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    CountryCodePicker countryCodePicker;
    TextView mtodaytotal,mtotal,mactive,mtodayactive,mrecovered,mtodayrecovered,mdeaths,mtodaydeaths;

    String country;
    TextView mfilter;
    Spinner spinner;
    String[] types = {"cases","deaths","recovered","active"};

     private List<ModelClass> ModelClassList;
     private List<ModelClass> ModelClassList2;
    PieChart pieChart;
    private RecyclerView recyclerView;
    com.example.covidtrackerapp.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ModelClassList = new ArrayList<>();
        ModelClassList2 = new ArrayList<>();

        countryCodePicker = findViewById(R.id.ccg);
        mtotal = findViewById(R.id.totalCases);
        mtodaytotal = findViewById(R.id.todayTotal);
        mactive = findViewById(R.id.activeCases);
        mtodayactive= findViewById(R.id.todayActive);
        mrecovered = findViewById(R.id.totalRecovered);
        mtodayrecovered= findViewById(R.id.todayRecovered);
        mdeaths = findViewById(R.id.totalDeaths);
        mtodaydeaths= findViewById(R.id.todayDeaths);
         pieChart = findViewById(R.id.pie_chart);
         spinner = findViewById(R.id.spinner);
         mfilter = findViewById(R.id.filter);
         recyclerView = findViewById(R.id.recyclerView);



        spinner.setOnItemSelectedListener(this);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        ApiUtilities.getApiInterface().getcountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                ModelClassList2.addAll(response.body());
               // Log.d(TAG ,"message1" +ModelClassList2.addAll(response.body()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });

        adapter = new Adapter(getApplicationContext(),ModelClassList2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        countryCodePicker.setAutoDetectedCountry(true);
        country = countryCodePicker.getSelectedCountryName();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country = countryCodePicker.getSelectedCountryName();
                fetchData();
            }
        });

        fetchData();





    }

    private void fetchData() {
        ApiUtilities.getApiInterface().getcountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                ModelClassList.addAll(response.body());
                //Log.d(TAG,"my error"+ModelClassList.addAll(response.body()));
                for (int i=0;i< ModelClassList.size();i++){
                    if (ModelClassList.get(i).getCountry().equals(country)){
                        mactive.setText((ModelClassList.get(i).getActive()));
                        mtodaydeaths.setText((ModelClassList.get(i).getTodayDeaths()));
                        mtodayrecovered.setText((ModelClassList.get(i).getTodayRecovered()));
                        mtodaytotal.setText((ModelClassList.get(i).getTodayCases()));
                        mtotal.setText((ModelClassList.get(i).getCases()));
                        mdeaths.setText((ModelClassList.get(i).getDeaths()));
                        mrecovered.setText((ModelClassList.get(i).getRecovered()));

                        int active, total, recovered,deaths;
                        active= Integer.parseInt(ModelClassList.get(i).getActive());
                        total= Integer.parseInt(ModelClassList.get(i).getCases());
                        recovered= Integer.parseInt(ModelClassList.get(i).getRecovered());
                        deaths = Integer.parseInt(ModelClassList.get(i).getDeaths());



                        updategraph(active,total,recovered,deaths);



                    }
                }
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });


    }

    private void updategraph(int active, int total, int recovered, int deaths) {
        pieChart.clearChart();
        pieChart.addPieSlice(new PieModel("Confirm",total,Color.parseColor("#FFB701")));
        pieChart.addPieSlice(new PieModel("Active",active,Color.parseColor("#FF4caf50")));
        pieChart.addPieSlice(new PieModel("Recovered",recovered,Color.parseColor("#38ACCD")));
        pieChart.addPieSlice(new PieModel("Deaths",deaths,Color.parseColor("#F55c47")));
        pieChart.startAnimation();

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = types[i];
        mfilter.setText(item);
        adapter.filter(item);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}