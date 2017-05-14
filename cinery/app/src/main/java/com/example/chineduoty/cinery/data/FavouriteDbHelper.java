package com.example.chineduoty.cinery.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by chineduoty on 5/14/17.
 */

public class FavouriteDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";

    public static final int DATABASE_VERSION = 1;

    public FavouriteDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVOURITE_TABLE =
                "CREATE TABLE "+ FavouriteContract.FavouriteEntry.TABLE_NAME + "("+
                        FavouriteContract.FavouriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FavouriteContract.FavouriteEntry.MOVIE_ID + " INTEGER UNIQUE, " +
                        FavouriteContract.FavouriteEntry.ORIGINAL_TITLE + " TEXT NOT NULL, "+
                        FavouriteContract.FavouriteEntry.OVERVIEW + " TEXT NOT NULL, "+
                        FavouriteContract.FavouriteEntry.POSTER_PATH + " TEXT NOT NULL, "+
                        FavouriteContract.FavouriteEntry.BACKDROP_PATH + " TEXT NOT NULL, "+
                        FavouriteContract.FavouriteEntry.RELEASE_DATE + " TEXT NOT NULL, "+
                        FavouriteContract.FavouriteEntry.VOTE_AVERAGE + " REAL NOT NULL );";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ FavouriteContract.FavouriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
