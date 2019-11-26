package com.agungsubastian.proyekakhir.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agungsubastian.proyekakhir.DetailFavoriteActivity;
import com.agungsubastian.proyekakhir.R;
import com.agungsubastian.proyekakhir.adapter.FavoriteAdapter;
import com.agungsubastian.proyekakhir.model.FavoriteModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.agungsubastian.proyekakhir.database.DatabaseContract.CONTENT_URI;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {

    private ProgressBar progressBar;
    private FavoriteAdapter adapter;
    private RecyclerView rv_favorite;

    public FavoriteFragment() {}


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        rv_favorite = view.findViewById(R.id.rv_favorite);
        setupList();

        showLoading();

        HandlerThread handlerThread = new HandlerThread("DataObserver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        DataObserver myObserver = new DataObserver(handler, getContext());
        Objects.requireNonNull(getContext()).getContentResolver().registerContentObserver(CONTENT_URI, true, myObserver);
        if (savedInstanceState == null) {
            new loadDataAsync().execute();
        } else {
            ArrayList<FavoriteModel> list;
            list = savedInstanceState.getParcelableArrayList("favorite");
            adapter.setTVResult(list);
            rv_favorite.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("favorite", new ArrayList<>(adapter.getList()));
    }

    private void setupList() {
        adapter = new FavoriteAdapter();
        rv_favorite.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_favorite.setAdapter(adapter);
        adapter.setOnItemClickCallback(new FavoriteAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(FavoriteModel data) {
                Intent intent = new Intent(getContext(), DetailFavoriteActivity.class);
                intent.putExtra(DetailFavoriteActivity.EXTRA_DATA, data);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class loadDataAsync extends AsyncTask<Void, Void, Cursor> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Cursor doInBackground(Void... voids) {
            return Objects.requireNonNull(getContext()).getContentResolver().query(
                    CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);

            List<FavoriteModel> items = new ArrayList<>();
            while (cursor.moveToNext()) {
                FavoriteModel item = new FavoriteModel();
                item.setId(cursor.getString(0));
                item.setName(cursor.getString(1));
                item.setDate(cursor.getString(2));
                item.setDescription(cursor.getString(3));
                item.setImage(cursor.getString(4));
                item.setVote(String.valueOf(Float.parseFloat(cursor.getString(5))));
                items.add(item);
            }
            cursor.close();
            adapter.replaceAll(items);
            hideLoading();
        }
    }

    public class DataObserver extends ContentObserver {
        final Context context;

        DataObserver(Handler handler, Context context) {
            super(handler);
            this.context = context;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new loadDataAsync().execute();
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }
}
