package com.hkurokawa.imagebrowser.data;

import com.hkurokawa.imagebrowser.data.local.LocalPhotoResource;
import android.content.Context;

public abstract class ImageResourceFactory {
   public static ImageResource getImageResource(final Context context) {
      return new LocalPhotoResource(context);
   }
}
