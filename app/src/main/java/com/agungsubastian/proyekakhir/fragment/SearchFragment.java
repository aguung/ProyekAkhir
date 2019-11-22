package com.agungsubastian.proyekakhir.fragment;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.agungsubastian.proyekakhir.adapter.MoviesAdapter;
import com.agungsubastian.proyekakhir.adapter.TVAdapter;
import com.agungsubastian.proyekakhir.DetailMovieActivity;
import com.agungsubastian.proyekakhir.DetailTVActivity;
import com.agungsubastian.proyekakhir.helper.ApiClient;
import com.agungsubastian.proyekakhir.MainActivity;
import com.agungsubastian.proyekakhir.model.MoviesModel;
import com.agungsubastian.proyekakhir.model.ResultItemMovies;
import com.agungsubastian.proyekakhir.model.ResultItemTV;
import com.agungsubastian.proyekakhir.model.TVModel;
import com.agungsubastian.proyekakhir.R;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private RecyclerView rv_data;
    private ProgressBar progressBar;
    private MoviesAdapter adapterMovie;
    private TVAdapter adapterTV;
    private ApiClient apiClient = new ApiClient();
    private SearchView searchView;
    public SearchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        rv_data = view.findViewById(R.id.rv_data);
        searchView = view.findViewById(R.id.search);

        getDataMovie("Avenger");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.onActionViewCollapsed();
                if(!query.isEmpty()){
                    if(((MainActivity) Objects.requireNonNull(getActivity())).type.equals("movie")){
                        getDataMovie(query);
                    }else{
                        getDataTV(query);
                    }
                }
                return false;
            }
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.isEmpty()){
                    if(((MainActivity) Objects.requireNonNull(getActivity())).type.equals("movie")){
                        getDataMovie(newText);
                    }else{
                        getDataTV(newText);
                    }
                }
                return false;
            }
        });

        return view;
    }

    private void getDataMovie(String title){
        showLoading();
        Call<MoviesModel> apiCall = apiClient.getService().getMovies(title);
        apiCall.enqueue(new Callback<MoviesModel>() {
            @Override
            public void onResponse(@NonNull Call<MoviesModel> call, @NonNull Response<MoviesModel> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    setMovies();
                    adapterMovie.replaceAll(response.body().getResults());
                } else {
                    Toast.makeText(getContext(), R.string.error_load, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MoviesModel> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(getContext(), R.string.time_out, Toast.LENGTH_SHORT).show();
                } else if (t instanceof UnknownHostException) {
                    Toast.makeText(getContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getDataTV(String title){
        showLoading();
        Call<TVModel> apiCall = apiClient.getService().getTV(title);
        apiCall.enqueue(new Callback<TVModel>() {
            @Override
            public void onResponse(@NonNull Call<TVModel> call, @NonNull Response<TVModel> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    setTV();
                    adapterTV.replaceAll(response.body().getResults());
                } else {
                    Toast.makeText(getContext(), R.string.error_load, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVModel> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(getContext(), R.string.time_out, Toast.LENGTH_SHORT).show();
                } else if (t instanceof UnknownHostException) {
                    Toast.makeText(getContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void showLoading(){
        progressBar.setVisibility(View.VISIBLE);
    }
    private void hideLoading(){
        progressBar.setVisibility(View.GONE);
    }

    private void setMovies(){
        adapterMovie = new MoviesAdapter();
        rv_data.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_data.setAdapter(adapterMovie);
        adapterMovie.setOnItemClickCallback(new MoviesAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(ResultItemMovies item) {
                if(searchView != null){
                    searchView.onActionViewCollapsed();
                }
                Intent intent = new Intent(getContext(), DetailMovieActivity.class);
                intent.putExtra(DetailMovieActivity.EXTRA_DATA, item);
                startActivity(intent);
            }
        });
    }

    private void setTV(){
        adapterTV = new TVAdapter();
        rv_data.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_data.setAdapter(adapterTV);
        adapterTV.setOnItemClickCallback(new TVAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(ResultItemTV item) {
                if(searchView != null){
                    searchView.onActionViewCollapsed();
                }
                Intent intent = new Intent(getContext(), DetailTVActivity.class);
                intent.putExtra(DetailTVActivity.EXTRA_DATA, item);
                startActivity(intent);
            }
        });
    }
}
