package com.agungsubastian.proyekakhir.favoritemovie;

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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agungsubastian.proyekakhir.favoritemovie.adapter.FavoriteAdapter;
import com.agungsubastian.proyekakhir.favoritemovie.model.FavoriteModel;

import java.util.ArrayList;
import java.util.List;

import static com.agungsubastian.proyekakhir.favoritemovie.database.DatabaseContract.CONTENT_URI;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private FavoriteAdapter adapter;
    private RecyclerView rv_favorite;
    private loadDataAsync loadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        rv_favorite = findViewById(R.id.rv_favorite);
        setupList();
        showLoading();

        HandlerThread handlerThread = new HandlerThread("DataObserver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        DataObserver myObserver = new DataObserver(handler, MainActivity.this);
        getContentResolver().registerContentObserver(CONTENT_URI, true, myObserver);

        loadData = new loadDataAsync();
        loadData.execute();
    }

    private void setupList() {
        adapter = new FavoriteAdapter();
        rv_favorite.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        rv_favorite.setAdapter(adapter);
        adapter.setOnItemClickCallback(new FavoriteAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(FavoriteModel data) {
                Intent intent = new Intent(MainActivity.this, DetailFavoriteActivity.class);
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
            return getContentResolver().query(
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
            if(items.size() == 0){
                Toast.makeText(getBaseContext(), R.string.empty, Toast.LENGTH_SHORT).show();
            }
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
            loadData = new loadDataAsync();
            loadData.execute();
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }
}
