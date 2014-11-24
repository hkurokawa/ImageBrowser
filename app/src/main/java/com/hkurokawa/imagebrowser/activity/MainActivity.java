package com.hkurokawa.imagebrowser.activity;

import com.hkurokawa.imagebrowser.R;
import com.hkurokawa.imagebrowser.data.ImageResource;
import com.hkurokawa.imagebrowser.data.ImageResourceFactory;
import com.hkurokawa.imagebrowser.util.SystemUiHider;

import java.util.List;
import java.util.Set;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MainActivity extends Activity implements AddTagDialogFragment.AddTagDialogListener {
   @SuppressWarnings("unused")
   private static final String activityName = "FullScreenActivity";

   /**
    * Whether or not the system UI should be auto-hidden after
    * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
    */
   private static final boolean AUTO_HIDE = true;

   /**
    * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
    * user interaction before hiding the system UI.
    */
   private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

   /**
    * The flags to pass to {@link SystemUiHider#getInstance}.
    */
   private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

   /**
    * The instance of the {@link SystemUiHider} for this activity.
    */
   private SystemUiHider mSystemUiHider;

   private Handler mHideHandler = new Handler();
   private Runnable mHideRunnable = new Runnable() {
      @Override
      public void run() {
         MainActivity.this.mSystemUiHider.hide();
      }
   };

   private ViewPager mViewPager;

   private FullscreenImagePagerAdapter mPagerAdapter;

   private TagsArrayAdapter mTagsArrayAdapter;

   private ActionBarDrawerToggle mTagsDrawerToggle;

   private ImageResource resource;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      this.setContentView(R.layout.activity_main);

      final View contentView = this.findViewById(R.id.content_frame);
      final View footerView = this.findViewById(R.id.footer_area);
      this.setupFullScreenSetting(contentView, footerView);

      // Photo image resource
      final ImageResource imageResource = ImageResourceFactory.getImageResource(this);
      imageResource.open();
      this.resource = imageResource;
      
      // Photo viewer
      final ViewPager viewPager = (ViewPager) this.findViewById(R.id.fullscreen_image_pager);
      final FullscreenImagePagerAdapter viewPagerAdapter = new FullscreenImagePagerAdapter(this.getApplicationContext(), imageResource);
      viewPager.setAdapter(viewPagerAdapter);
      this.mViewPager = viewPager;
      this.mPagerAdapter = viewPagerAdapter;

      // Display tags for the current displayed image
      this.renderTagLabels();
      
      // Set an event handler for image switching
      viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
         @Override
         public void onPageSelected(int position) {
            MainActivity.this.renderTagLabels();
         }
      });
      
      // Tags drawer
      final DrawerLayout drawerLayout = (DrawerLayout) this.findViewById(R.id.drawer_layout);
      final ListView drawerList = (ListView) this.findViewById(R.id.left_drawer);
      final TagsArrayAdapter tagsAdapter = new TagsArrayAdapter(this, R.layout.drawer_item_layout, imageResource);
      drawerList.setAdapter(tagsAdapter);
      drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MainActivity.this.selectTag(position);
            drawerList.setItemChecked(position, true);
            drawerLayout.closeDrawer(drawerList);
         }
      });
      this.mTagsArrayAdapter = tagsAdapter;
      
      // At the first time, "All images" should be selected
      drawerList.setItemChecked(tagsAdapter.getTagAllPosition(), true);

      // Action bar icon to open/close the drawer
      this.mTagsDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);
      drawerLayout.setDrawerListener(this.mTagsDrawerToggle);
      
      this.getActionBar().setDisplayHomeAsUpEnabled(true);
      this.getActionBar().setHomeButtonEnabled(true);
   }

   private void setupFullScreenSetting(final View contentView, final View footerView) {
      // Set up an instance of SystemUiHider to control the system UI for
      // this activity.
      this.mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
      this.mSystemUiHider.setup();
      this.mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
         // Cached values.
         private int mControlsHeight;
         private int mShortAnimTime;

         @Override
         @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
         public void onVisibilityChange(boolean visible) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
               // If the ViewPropertyAnimator API is available
               // (Honeycomb MR2 and later), use it to animate the
               // in-layout UI controls at the bottom of the
               // screen.
               if (this.mControlsHeight == 0) {
                  this.mControlsHeight = footerView.getHeight();
               }
               if (this.mShortAnimTime == 0) {
                  this.mShortAnimTime = MainActivity.this.getResources().getInteger(android.R.integer.config_shortAnimTime);
               }
               footerView.animate().translationY(visible ? 0 : this.mControlsHeight).setDuration(this.mShortAnimTime);
            } else {
               // If the ViewPropertyAnimator APIs aren't
               // available, simply show or hide the in-layout UI
               // controls.
               footerView.setVisibility(visible ? View.VISIBLE : View.GONE);
            }

            // Show/hide the action bar
            if (visible) {
               MainActivity.this.getActionBar().show();
            } else {
               MainActivity.this.getActionBar().hide();
            }

            if (visible && AUTO_HIDE) {
               // Schedule a hide().
               MainActivity.this.delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
         }
      });
   }

   private void renderTagLabels() {
      final int position = this.mViewPager.getCurrentItem();
      final TextView textView = (TextView) MainActivity.this.findViewById(R.id.fullscreen_footer_tags_list);
      final String uri = this.mPagerAdapter.getImageUri(position);
      final StringBuffer sb = new StringBuffer();
      if (uri != null) {
         final Set<String> tags = this.resource.getImageTag(uri);
         for (String t : tags) {
            sb.append(t).append(", ");
         }
         if (!tags.isEmpty()) {
            sb.setLength(sb.length() - 2);
         }
      }
      textView.setText(sb.toString());
   }

   private void selectTag(final int position) {
      if (position == this.mTagsArrayAdapter.getTagAllPosition()) {
         // The item "All images" is selected
         MainActivity.this.mPagerAdapter.clearTagFilter();
      } else {
         final String tagName = this.mTagsArrayAdapter.getItem(position);
         MainActivity.this.mPagerAdapter.setTagFilter(tagName);
      }
      MainActivity.this.mPagerAdapter.notifyDataSetChanged();
      
      // Display tags for the current displayed image
      MainActivity.this.renderTagLabels();
   }

   @Override
   protected void onPostCreate(Bundle savedInstanceState) {
      super.onPostCreate(savedInstanceState);

      // Sync the toggle state after onRestoreInstanceState has occurred.
      this.mTagsDrawerToggle.syncState();

      // Trigger the initial hide() shortly after the activity has been
      // created, to briefly hint to the user that UI controls
      // are available.
      delayedHide(100);
   }

   @Override
   public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      this.mTagsDrawerToggle.onConfigurationChanged(newConfig);
   }

   @Override
   protected void onResume() {
      super.onResume();
      this.resource.open();
      this.mPagerAdapter.clearTagFilter();
      this.mPagerAdapter.notifyDataSetChanged();
      this.mTagsArrayAdapter.refreshTags();
   }

   @Override
   protected void onPause() {
      super.onPause();
      this.resource.close();
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      final MenuInflater inflator = this.getMenuInflater();
      inflator.inflate(R.menu.fullscreen_activity_actions, menu);
      
      return super.onCreateOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      // Try to pass the event to the action bar drawer toggle button.
      // If it returns true, it means the event has been handled by the button.
      if (this.mTagsDrawerToggle.onOptionsItemSelected(item)) {
         return true;
      } else {
         switch (item.getItemId()) {
         case R.id.action_add_tag:
            this.showAddTagsDialog();
            return true;
         case R.id.action_manage_labels:
            final Intent intent = new Intent(this, ManageTagsActivity.class);
            this.startActivity(intent);
            return true;
         }
         return super.onOptionsItemSelected(item);
      }
   }

   private void showAddTagsDialog() {
      final int position = this.mViewPager.getCurrentItem();
      final String uri = this.mPagerAdapter.getImageUri(position);
      final List<String> availableTags = this.resource.listTags();
      final Set<String> selectedTags = this.resource.getImageTag(uri);
      
      final AddTagDialogFragment addTagDialog = new AddTagDialogFragment();
      addTagDialog.setTags(availableTags, selectedTags);
      addTagDialog.show(this.getFragmentManager(), "addTagDialog");
   }

   @Override
   public void onAddTagDialogPositiveClick(AddTagDialogFragment dialog) {
      final int position = this.mViewPager.getCurrentItem();
      final String uri = this.mPagerAdapter.getImageUri(position);
      
      this.resource.setImageTag(uri, dialog.getSelectedTags());
      MainActivity.this.renderTagLabels();
   }

   @Override
   public void onAddTagDialogNegativeClick(AddTagDialogFragment dialog) {
      // do nothing
   }

   /**
    * Schedules a call to hide() in [delay] milliseconds, canceling any
    * previously scheduled calls.
    */
   private void delayedHide(int delayMillis) {
      this.mHideHandler.removeCallbacks(this.mHideRunnable);
      this.mHideHandler.postDelayed(this.mHideRunnable, delayMillis);
   }
}
