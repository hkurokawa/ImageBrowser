package com.hkurokawa.imagebrowser.activity;

import com.hkurokawa.imagebrowser.R;
import com.hkurokawa.imagebrowser.data.ImageResource;
import com.squareup.picasso.Picasso;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FullscreenImagePagerAdapter extends PagerAdapter {
    private static final String TAG_NAME = "FIPagerAdapter";
    private final Context appContext;
   private final ImageResource imageResource;
   private List<String> imageUris;

   public FullscreenImagePagerAdapter(final Context appContext, final ImageResource imageResource) {
      this.appContext = appContext;
      this.imageResource = imageResource;
      this.imageUris = imageResource.listImages();
   }

   public void setTagFilter(final String tagName) {
      this.imageUris = this.imageResource.listImages(tagName);
   }

   public void clearTagFilter() {
      this.imageUris = this.imageResource.listImages();
   }

   /**
    * Returns the URI of the image placed at the given position
    * @param position the position of the image to return the URI of
    * @return <code>null</code> if the specified position is invalid, or the URI of the image
    */
   public String getImageUri(final int position) {
      if (position < 0 || position >= this.getCount()) {
         return null;
      } else {
         return this.imageUris.get(position);
      }
   }

   @Override
   public int getCount() {
      return this.imageUris.size();
   }

   @Override
   public boolean isViewFromObject(View view, Object object) {
      return view == (View) object;
   }

   @Override
   public Object instantiateItem(ViewGroup container, int position) {
      final String uri = this.imageUris.get(position);
      if (Log.isLoggable(TAG_NAME, Log.INFO)) {
          Log.i(TAG_NAME, "Loading " + uri);
      }
      final View layout = View.inflate(this.appContext, R.layout.fullscreen_image_layout, null);
      final ImageView imgView = (ImageView) layout.findViewById(R.id.fullscreen_image_display);
      Picasso.with(this.appContext).load(uri).into(imgView);
      container.addView(layout);
      
      layout.setTag(uri);
      return layout;
   }

   @Override
   public void destroyItem(ViewGroup container, int position, Object object) {
      container.removeView((View)object);
   }

   @Override
   public int getItemPosition(Object object) {
      final String uri = (String) ((View)object).getTag();
      final int index = this.imageUris.indexOf(uri);
      if (index < 0) {
         return POSITION_NONE;
      } else {
         return index;
      }
   }
}
