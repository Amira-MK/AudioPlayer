package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "audio.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_FAVORITES = "favorites";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_AUDIO = "audio";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the favorites table
        String createFavoritesTable = "CREATE TABLE " + TABLE_FAVORITES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_AUDIO + " TEXT" +
                ")";
        db.execSQL(createFavoritesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implement if needed
    }

    public boolean addFavorite(String audio) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AUDIO, audio);
        long result = db.insert(TABLE_FAVORITES, null, values);
        return result != -1;
    }

    public List<String> getFavorites() {
        List<String> favoritesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVORITES, null);
        if (cursor.moveToFirst()) {
            int audioColumnIndex = cursor.getColumnIndex(COLUMN_AUDIO);
            do {
                if (audioColumnIndex != -1) {
                    String audio = cursor.getString(audioColumnIndex);
                    favoritesList.add(audio);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favoritesList;
    }


    // Add other database operations as needed

}
