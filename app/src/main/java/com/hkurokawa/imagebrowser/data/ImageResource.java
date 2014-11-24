package com.hkurokawa.imagebrowser.data;

import java.util.List;
import java.util.Set;

import android.graphics.Bitmap;

public interface ImageResource {
   public void open() throws ImageResourceException;
   public void close() throws ImageResourceException;
   public List<String> listImages() throws ImageResourceException;
   public List<String> listImages(final String tag) throws NoSuchTagNameException, ImageResourceException;
   public Bitmap getImage(final String uri) throws ImageResourceException;
   public void setImageTag(final String imageUri, final Set<String> tags) throws NoSuchTagNameException, ImageResourceException;
   public Set<String> getImageTag(final String imageUri) throws ImageResourceException;
   public List<String> listTags() throws ImageResourceException;
   public void addTag(final String tag) throws DuplicateTagNameException, ImageResourceException;
   public void removeTag(final String tag) throws NoSuchTagNameException, ImageResourceException;
}
