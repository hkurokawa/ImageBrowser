package com.hkurokawa.imagebrowser.util;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;

/**
 * A base implementation of {@link SystemUiHider}. Uses APIs available in all
 * API levels to show and hide the status bar.
 */
public class SystemUiHiderBase extends SystemUiHider {
   /**
    * Whether or not the system UI is currently visible. This is a cached value
    * from calls to {@link #hide()} and {@link #show()}.
    */
   private boolean mVisible = true;

   /**
    * Constructor not intended to be called by clients. Use
    * {@link SystemUiHider#getInstance} to obtain an instance.
    */
   protected SystemUiHiderBase(Activity activity, View anchorView, int flags) {
      super(activity, anchorView, flags);
   }

   @Override
   public void setup() {
      if ((this.mFlags & FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES) == 0) {
         this.mActivity.getWindow().setFlags(
               WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                     | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
               WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                     | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
      }
   }

   @Override
   public boolean isVisible() {
      return this.mVisible;
   }

   @Override
   public void hide() {
      if ((this.mFlags & FLAG_FULLSCREEN) != 0) {
         this.mActivity.getWindow().setFlags(
               WindowManager.LayoutParams.FLAG_FULLSCREEN,
               WindowManager.LayoutParams.FLAG_FULLSCREEN);
      }
      this.mOnVisibilityChangeListener.onVisibilityChange(false);
      this.mVisible = false;
   }

   @Override
   public void show() {
      if ((this.mFlags & FLAG_FULLSCREEN) != 0) {
         this.mActivity.getWindow().setFlags(0,
               WindowManager.LayoutParams.FLAG_FULLSCREEN);
      }
      this.mOnVisibilityChangeListener.onVisibilityChange(true);
      this.mVisible = true;
   }
}
