package com.agungsubastian.proyekakhir.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agungsubastian.proyekakhir.DetailMovieActivity;
import com.agungsubastian.proyekakhir.R;
import com.agungsubastian.proyekakhir.adapter.MoviesAdapter;
import com.agungsubastian.proyekakhir.helper.ApiClient;
import com.agungsubastian.proyekakhir.model.MoviesModel;
import com.agungsubastian.proyekakhir.model.ResultItemMovies;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment {

    private ProgressBar progressBar;
    private MoviesAdapter adapter;
    private List<ResultItemMovies> itemMovies;
    private int item_per_display = 6;
    private ApiClient apiClient = new ApiClient();

    public MovieFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        RecyclerView rv_movies = view.findViewById(R.id.rv_movie);

        adapter = new MoviesAdapter(item_per_display);
        rv_movies.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rv_movies.setAdapter(adapter);

        if (savedInstanceState != null) {
            ArrayList<ResultItemMovies> list;
            list = savedInstanceState.getParcelableArrayList("movies");
            adapter.setMovieResult(list);
            rv_movies.setAdapter(adapter);
        } else {
            getDataMovie();
        }
        adapter.setOnItemClickCallback(new MoviesAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(ResultItemMovies item) {
                Intent intent = new Intent(getContext(), DetailMovieActivity.class);
                intent.putExtra(DetailMovieActivity.EXTRA_DATA, item);
                startActivity(intent);
            }
        });
        adapter.setOnLoadMoreListener(new MoviesAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int current_page) {
                Log.e("MOVIE", " " + current_page);
                loadNextDataMovies(current_page);
            }
        });
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movies", new ArrayList<>(adapter.getList()));
    }

    private void getDataMovie() {
        showLoading();
        Call<MoviesModel> apiCall = apiClient.getService().getMovies();
        apiCall.enqueue(new Callback<MoviesModel>() {
            @Override
            public void onResponse(@NonNull Call<MoviesModel> call, @NonNull Response<MoviesModel> response) {
                hideLoading();
                System.out.println(response);
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    itemMovies = response.body().getResults();
                    adapter.insertData(generateListItemMovies(item_per_display, 0));
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

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private List<ResultItemMovies> generateListItemMovies(int count, int page) {
        int first = page == 0 ? 0 : count * page;
        return itemMovies.subList(first, count + first);
    }

    private void loadNextDataMovies(final int current_page) {
        int max = item_per_display * (current_page + 1);
        if (max <= itemMovies.size()) {
            adapter.setLoading();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.insertData(generateListItemMovies(item_per_display, current_page));
                }
            }, 1500);
        }
    }
}
