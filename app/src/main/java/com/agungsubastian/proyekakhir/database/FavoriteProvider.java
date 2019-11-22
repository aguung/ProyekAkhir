package com.agungsubastian.proyekakhir.database;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import java.util.Objects;

import static com.agungsubastian.proyekakhir.database.DatabaseContract.AUTHORITY;
import static com.agungsubastian.proyekakhir.database.DatabaseContract.CONTENT_URI;
import static com.agungsubastian.proyekakhir.database.DatabaseContract.TABLE_FAVORITE;

public class FavoriteProvider extends ContentProvider {

    private static final int FAVORITE = 1;
    private static final int FAVORITE_ID = 2;
    private FavoriteHelper favoriteHelper;

    public FavoriteProvider() {}

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, TABLE_FAVORITE, FAVORITE);
        sUriMatcher.addURI(AUTHORITY,
                TABLE_FAVORITE + "/#",
                FAVORITE_ID);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int deleted;
        if (sUriMatcher.match(uri) == FAVORITE_ID) {
            deleted = favoriteHelper.deleteById(uri.getLastPathSegment());
        } else {
            deleted = 0;
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(CONTENT_URI, null);
        return deleted;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long added;
        if (sUriMatcher.match(uri) == FAVORITE) {
            added = favoriteHelper.insert(values);
        } else {
            added = 0;
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(CONTENT_URI, null);
        return Uri.parse(CONTENT_URI + "/" + added);
    }

    @Override
    public boolean onCreate() {
        favoriteHelper = FavoriteHelper.getInstance(getContext());
        favoriteHelper.open();
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case FAVORITE:
                cursor = favoriteHelper.queryAll();
                break;
            case FAVORITE_ID:
                cursor = favoriteHelper.queryById(uri.getLastPathSegment());
                break;
            default:
                cursor = null;
                favoriteHelper.close();
                break;
        }
        return cursor;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int updated;
        if (sUriMatcher.match(uri) == FAVORITE_ID) {
            updated = favoriteHelper.update(uri.getLastPathSegment(), values);
        } else {
            updated = 0;
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(CONTENT_URI, null);
        return updated;
    }
}
