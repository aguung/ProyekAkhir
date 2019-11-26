package com.agungsubastian.proyekakhir;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.agungsubastian.proyekakhir.database.DatabaseContract;
import com.agungsubastian.proyekakhir.database.FavoriteHelper;
import com.agungsubastian.proyekakhir.model.ResultItemMovies;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import static com.agungsubastian.proyekakhir.database.DatabaseContract.CONTENT_URI;

public class DetailMovieActivity extends AppCompatActivity {

    public static String EXTRA_DATA = "extra_data";
    private boolean isFavorite = false;
    private FloatingActionButton favorite;
    private ResultItemMovies item;
    private FavoriteHelper favoriteHelper;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        item = getIntent().getParcelableExtra(EXTRA_DATA);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tv_date = findViewById(R.id.date);
        TextView tv_score = findViewById(R.id.score);
        TextView tv_description = findViewById(R.id.description);
        ImageView iv_image = findViewById(R.id.img_photo);
        favorite = findViewById(R.id.favorite);

        toolbar.setTitle(item.getOriginalTitle());
        tv_date.setText(item.getReleaseDate());
        tv_score.setText(String.valueOf(item.getVoteAverage()));
        tv_description.setText(item.getOverview());
        Glide.with(DetailMovieActivity.this)
                .load(BuildConfig.BASE_URL_IMG+"w500"+item.getPosterPath())
                .error(R.drawable.ic_error)
                .centerInside()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(iv_image);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        loadDataSQLite();

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorite) FavoriteRemove();
                else FavoriteSave();

                isFavorite = !isFavorite;
                setFavorite();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        if (favoriteHelper != null) favoriteHelper.close();
        super.onDestroy();
    }

    private void setFavorite(){
        if (isFavorite) favorite.setImageResource(R.drawable.ic_favorite);
        else favorite.setImageResource(R.drawable.ic_unfavorite);
    }

    private void loadDataSQLite(){
        favoriteHelper = new FavoriteHelper(this);
        favoriteHelper = FavoriteHelper.getInstance(this);
        favoriteHelper.open();

        Cursor cursor = getContentResolver().query(
                Uri.parse(CONTENT_URI + "/" + item.getId()),
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) isFavorite = true;
            cursor.close();
        }
        setFavorite();
    }

    private void FavoriteSave() {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.FavoriteColumns.ID, item.getId());
        cv.put(DatabaseContract.FavoriteColumns.TITLE, item.getOriginalTitle());
        cv.put(DatabaseContract.FavoriteColumns.DESCRIPTION, item.getOverview());
        cv.put(DatabaseContract.FavoriteColumns.DATE, item.getReleaseDate());
        cv.put(DatabaseContract.FavoriteColumns.SCORE, item.getVoteAverage());
        cv.put(DatabaseContract.FavoriteColumns.IMAGE, item.getPosterPath());

        Uri data  = getContentResolver().insert(CONTENT_URI, cv);
        System.out.println("Uri insert "+data);

        Toast.makeText(this, R.string.add, Toast.LENGTH_SHORT).show();
    }

    private void FavoriteRemove() {
        int delete = getContentResolver().delete(
                Uri.parse(CONTENT_URI + "/" + item.getId()),
                null,
                null
        );
        System.out.println("INT DELETE "+delete);
        Toast.makeText(this, R.string.remove, Toast.LENGTH_SHORT).show();
    }
}
