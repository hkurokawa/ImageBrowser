package com.hkurokawa.imagebrowser.data.local;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ImageDatabaseOpenHelper extends SQLiteOpenHelper implements Constants {
   private static final String className = ImageDatabaseOpenHelper.class.getName();
   
   private static final String DATABASE_NAME = "imageBrowser.db";
   private static final int DATABASE_VERSION = 1;

   private static final String QUERY_CREATE_TABLE_IMAGES = "create table " + TABLE_IMAGES + " (" + COLUMN_IMAGES_URI + " text primary key);";
   private static final String QUERY_CREATE_TABLE_IMAGES_TAGS = "create table " + TABLE_IMAGES_TAGS + " (" + COLUMN_IMAGES_TAGS_TAG_NAME + " text, " + COLUMN_IMAGES_TAGS_IMAGE_URI + " text);";
   private static final String QUERY_CREATE_TABLE_TAGS = "create table " + TABLE_TAGS + " (" + COLUMN_TAGS_NAME + " text primary key);";

   public ImageDatabaseOpenHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
   }

   @Override
   public void onCreate(SQLiteDatabase db) {
      db.execSQL(QUERY_CREATE_TABLE_IMAGES);
      db.execSQL(QUERY_CREATE_TABLE_TAGS);
      db.execSQL(QUERY_CREATE_TABLE_IMAGES_TAGS);
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      if (Log.isLoggable(className, Log.INFO)) {
         Log.i(className, "Upgrading database from version " + oldVersion + " to " + newVersion);
      }
      db.beginTransaction();
      try {
         db.execSQL("drop table if exists " + TABLE_IMAGES);
         db.execSQL("drop table if exists " + TABLE_IMAGES_TAGS);
         db.setTransactionSuccessful();
      } catch (SQLException e) {
         Log.w(className, "Failed to drop existing tables.", e);
      } finally {
         db.endTransaction();
      }
      
      this.onCreate(db);
   }

}
