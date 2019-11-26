package com.agungsubastian.proyekakhir.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agungsubastian.proyekakhir.DetailTVActivity;
import com.agungsubastian.proyekakhir.R;
import com.agungsubastian.proyekakhir.adapter.TVAdapter;
import com.agungsubastian.proyekakhir.helper.ApiClient;
import com.agungsubastian.proyekakhir.model.ResultItemTV;
import com.agungsubastian.proyekakhir.model.TVModel;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class TVFragment extends Fragment {

    private ProgressBar progressBar;
    private TVAdapter adapter;
    private ApiClient apiClient = new ApiClient();

    public TVFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tv, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        RecyclerView rv_tv = view.findViewById(R.id.rv_tv);

        adapter = new TVAdapter();
        rv_tv.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rv_tv.setAdapter(adapter);
        if(savedInstanceState != null){
            ArrayList<ResultItemTV> list;
            list = savedInstanceState.getParcelableArrayList("tv");
            adapter.setTVResult(list);
            rv_tv.setAdapter(adapter);
        }else{
            getDataTV();
        }
        adapter.setOnItemClickCallback(new TVAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(ResultItemTV item) {
                Intent intent = new Intent(getContext(), DetailTVActivity.class);
                intent.putExtra(DetailTVActivity.EXTRA_DATA, item);
                startActivity(intent);
            }
        });
        return view;
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("tv", new ArrayList<>(adapter.getList()));
    }

    private void getDataTV(){
        showLoading();
        Call<TVModel> apiCall = apiClient.getService().getTV();
        apiCall.enqueue(new Callback<TVModel>() {
            @Override
            public void onResponse(@NonNull Call<TVModel> call, @NonNull Response<TVModel> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    adapter.replaceAll(response.body().getResults());
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
}
