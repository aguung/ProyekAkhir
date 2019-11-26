package com.agungsubastian.proyekakhir.helper;

import com.agungsubastian.proyekakhir.model.MoviesModel;
import com.agungsubastian.proyekakhir.model.TVModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("search/movie")
    Call<MoviesModel> getMovies(@Query("query") String judul);

    @GET("search/tv")
    Call<TVModel> getTV(@Query("query") String judul);

    @GET("discover/movie")
    Call<MoviesModel> getReleaseToday(@Query("primary_release_date.gte") String date1, @Query("primary_release_date.lte") String date2);

    @GET("discover/movie")
    Call<MoviesModel> getMovies();

    @GET("discover/tv")
    Call<TVModel> getTV();
}
