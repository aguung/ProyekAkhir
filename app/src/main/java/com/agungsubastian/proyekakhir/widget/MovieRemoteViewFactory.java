package com.agungsubastian.proyekakhir.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.agungsubastian.proyekakhir.BuildConfig;
import com.agungsubastian.proyekakhir.R;
import com.agungsubastian.proyekakhir.database.DatabaseContract;
import com.agungsubastian.proyekakhir.model.FavoriteModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.util.concurrent.ExecutionException;

public class MovieRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Cursor cursor;

    MovieRemoteViewFactory(Context applicationContext) {
        mContext = applicationContext;
        //int mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    private FavoriteModel getFav(int position) {
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Position invalid!");
        }

        return new FavoriteModel(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavoriteColumns.ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavoriteColumns.IMAGE)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavoriteColumns.TITLE)));
    }


    @Override
    public void onCreate() {
        cursor = mContext.getContentResolver().query(
                DatabaseContract.CONTENT_URI,
                null,
                null,
                null,
                null
        );

    }

    @Override
    public void onDataSetChanged() {
        if (cursor != null) {
            cursor.close();
        }
        final long identityToken = Binder.clearCallingIdentity();
        cursor = mContext.getContentResolver().query(
                DatabaseContract.CONTENT_URI, null, null, null, null);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        FavoriteModel movieFavorite = getFav(position);
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.movie_widget_item);
        try {
            Bitmap bmp = Glide.with(mContext)
                    .asBitmap()
                    .load(BuildConfig.BASE_URL_IMG+"w500"+movieFavorite.getImage())
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
            rv.setImageViewBitmap(R.id.img_widget,bmp);
            rv.setTextViewText(R.id.tv_movie_title, movieFavorite.getName());
        }catch (InterruptedException | ExecutionException e){
            Log.e("Widget Load Error","error");
        }
        Bundle extras = new Bundle();
        extras.putInt(MovieWidget.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        rv.setOnClickFillInIntent(R.id.img_widget, fillInIntent);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return cursor.moveToPosition(position) ? cursor.getLong(0) : position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
