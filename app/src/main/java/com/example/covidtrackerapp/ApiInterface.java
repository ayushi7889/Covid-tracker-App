package com.example.covidtrackerapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    String Base_URL = "https://disease.sh/v3/covid-19/";
    @GET("countries")
    Call<List<ModelClass>> getcountrydata();
}
