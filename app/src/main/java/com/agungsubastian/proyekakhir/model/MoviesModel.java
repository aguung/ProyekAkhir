package com.agungsubastian.proyekakhir.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MoviesModel {

    @SerializedName("results")
    @Expose
    private List<ResultItemMovies> results = null;

    public List<ResultItemMovies> getResults() {
        return results;
    }

}