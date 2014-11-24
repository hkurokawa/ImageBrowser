package com.hkurokawa.imagebrowser.data.local;

import com.hkurokawa.imagebrowser.data.DuplicateTagNameException;
import com.hkurokawa.imagebrowser.data.ImageResource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

public class LocalPhotoResource implements ImageResource, Constants {
   private final ContentResolver contentResolver;
   private SQLiteDatabase database;
   private ImageDatabaseOpenHelper dbHelper;

   public LocalPhotoResource(final Context context) {
      this.contentResolver = context.getContentResolver();
      this.dbHelper = new ImageDatabaseOpenHelper(context);
   }

   @Override
   public void open() {
      this.database = this.dbHelper.getWritableDatabase();
      this.loadImages();
   }

   @Override
   public void close() {
      this.dbHelper.close();
   }

   private void loadImages() {
      // List the existing photos on the device and add new photos and remove already deleted photos
      final String[] projection = new String[] { MediaStore.Images.Media._ID };
      final Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
      final Cursor cursor = this.contentResolver.query(imageUri, projection, null, null, null);
      try {
         final int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
         final List<String> latestImageUris = new ArrayList<String>();
         while (cursor.moveToNext()) {
            final int imageID = cursor.getInt(index);
            final String uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(imageID)).toString();
            latestImageUris.add(uri);
         }

         final HashSet<String> storedImageUris = new HashSet<String>();
         storedImageUris.addAll(this.listImages());

         for (String uri : latestImageUris) {
            if (!storedImageUris.remove(uri)) {
               // insert the URI if it does not exist on the database
               this.addImage(uri);
            }
         }

         // remove URIs of already deleted images
         for (String uri : storedImageUris) {
            this.removeImage(uri);
         }
      } finally {
         cursor.close();
      }
   }

   private void addImage(final String uri) {
      final ContentValues values = new ContentValues();
      values.put(COLUMN_IMAGES_URI, uri);
      this.database.insert(TABLE_IMAGES, null, values);
   }

   private void removeImage(final String uri) {
      this.database.beginTransaction();
      try {
         this.database.delete(TABLE_IMAGES, COLUMN_IMAGES_URI + " = ?", new String[]{uri});
         this.database.delete(TABLE_IMAGES_TAGS, COLUMN_IMAGES_TAGS_IMAGE_URI + " = ?", new String[]{uri});
         this.database.setTransactionSuccessful();
      } finally {
         this.database.endTransaction();
      }
   }

   @Override
   public List<String> listImages() {
      final List<String> imageUris = new ArrayList<String>();
      final Cursor cursor = this.database.query(TABLE_IMAGES, new String[] { COLUMN_IMAGES_URI }, null, null, null,
            null, null);
      try {
         while (cursor.moveToNext()) {
            final String uri = cursor.getString(0);
            imageUris.add(uri);
         }
      } finally {
         cursor.close();
      }
      return imageUris;
   }

   @Override
   public List<String> listImages(final String tag) {
      final List<String> imageUris = new ArrayList<String>();
      final Cursor cursor = this.database.query(TABLE_IMAGES_TAGS, new String[]{COLUMN_IMAGES_TAGS_IMAGE_URI}, COLUMN_IMAGES_TAGS_TAG_NAME + "=?", new String[]{tag}, null, null, null);
      try {
         while (cursor.moveToNext()) {
            final String uri = cursor.getString(0);
            imageUris.add(uri);
         }
      } finally {
         cursor.close();
      }
      return imageUris;
   }

   @Override
   public void setImageTag(final String imageUri, final Set<String> tags) {
      this.database.beginTransaction();
      try {
         this.database.delete(TABLE_IMAGES_TAGS, COLUMN_IMAGES_TAGS_IMAGE_URI + " = ?", new String[]{imageUri});
         for (String t : tags) {
            final ContentValues values = new ContentValues();
            values.put(COLUMN_IMAGES_TAGS_IMAGE_URI, imageUri);
            values.put(COLUMN_IMAGES_TAGS_TAG_NAME, t);
            this.database.insert(TABLE_IMAGES_TAGS, null, values);
         }
         this.database.setTransactionSuccessful();
      } finally {
         this.database.endTransaction();
      }
   }

   @Override
   public Set<String> getImageTag(final String imageUri) {
      final Set<String> tags = new HashSet<String>();
      final Cursor cursor = this.database.query(TABLE_IMAGES_TAGS, new String[]{COLUMN_IMAGES_TAGS_TAG_NAME}, COLUMN_IMAGES_TAGS_IMAGE_URI + " = ?", new String[]{imageUri}, null, null, null, null);
      try {
         while (cursor.moveToNext()) {
            final String tag = cursor.getString(0);
            tags.add(tag);
         }
      } finally {
         cursor.close();
      }
      return tags;
   }

   @Override
   public List<String> listTags() {
      final List<String> tags = new ArrayList<String>();
      final Cursor cursor = this.database.query(TABLE_TAGS, new String[]{COLUMN_TAGS_NAME}, null, null, null, null, null);
      try {
         while (cursor.moveToNext()) {
            final String tag = cursor.getString(0);
            tags.add(tag);
         }
      } finally {
         cursor.close();
      }
      return tags;
   }

   @Override
   public void addTag(final String tag) throws DuplicateTagNameException {
      final ContentValues values = new ContentValues();
      values.put(COLUMN_TAGS_NAME, tag);
      try {
         this.database.insertOrThrow(TABLE_TAGS, null, values);
      } catch (SQLiteConstraintException e) {
         throw new DuplicateTagNameException(tag);
      }
   }

   @Override
   public void removeTag(final String tag) {
      this.database.beginTransaction();
      try {
         this.database.delete(TABLE_TAGS, COLUMN_TAGS_NAME + " = ?", new String[]{tag});
         this.database.delete(TABLE_IMAGES_TAGS, COLUMN_IMAGES_TAGS_TAG_NAME + " = ?", new String[]{tag});
         this.database.setTransactionSuccessful();
      } finally {
         this.database.endTransaction();
      }
   }
}
