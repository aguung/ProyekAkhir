package com.agungsubastian.proyekakhir.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {

    static String TABLE_FAVORITE = "FAVORITE";
    static final String AUTHORITY = "com.agungsubastian.proyekakhir.database";
    private static final String SCHEME = "content";

    public static class FavoriteColumns implements BaseColumns {
        public static String ID = "id";
        public static String TITLE = "title";
        public static String DESCRIPTION = "description";
        public static String DATE = "date";
        public static String SCORE = "score";
        public static String IMAGE = "image";

    }
    public static final Uri CONTENT_URI = new Uri.Builder().scheme(SCHEME)
            .authority(AUTHORITY)
            .appendPath(TABLE_FAVORITE)
            .build();
}