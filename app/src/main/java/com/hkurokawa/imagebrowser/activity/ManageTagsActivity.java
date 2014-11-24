package com.hkurokawa.imagebrowser.activity;

import com.hkurokawa.imagebrowser.R;
import com.hkurokawa.imagebrowser.data.DuplicateTagNameException;
import com.hkurokawa.imagebrowser.data.ImageResource;
import com.hkurokawa.imagebrowser.data.ImageResourceFactory;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ManageTagsActivity extends Activity implements CreateNewTagDialogFragment.CreateNewTagDialogListener, RemoveTagDialogFragment.RemoveTagDialogListener {
   private final static String activityName = "ManageTagsActivity";
   private ImageResource resource;
   private ArrayAdapter<String> mTagsArrayAdapter;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      this.setContentView(R.layout.activity_manage_tags);
      
      // Connect to database
      final ImageResource imageResource = ImageResourceFactory.getImageResource(this);
      imageResource.open();
      this.resource = imageResource;
      
      // Tags list
      final ListView tagsList = (ListView) this.findViewById(R.id.manage_tags_list_tags);
      final ArrayAdapter<String> tagsArrayAdapter = new ArrayAdapter<String>(this, R.layout.manage_tags_list_item_layout, this.resource.listTags());
      tagsList.setAdapter(tagsArrayAdapter);
      this.mTagsArrayAdapter = tagsArrayAdapter;
      
      // Launch remove dialog when an item is long-pressed
      tagsList.setOnItemClickListener(new OnItemClickListener(){
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ManageTagsActivity.this.showRemoveTagDialog(position);
         }
      });
      
      // Show the Up button in the action bar.
      this.getActionBar().setDisplayHomeAsUpEnabled(true);
   }

   public void onClickCreateNewTag(@SuppressWarnings("unused") final View view) {
      this.showCreateNewDialog();
   }

   private void showCreateNewDialog() {
      final DialogFragment dialog = new CreateNewTagDialogFragment();
      dialog.show(this.getFragmentManager(), "CreateNewTagDialogFragment");
   }

   private void showRemoveTagDialog(final int position) {
      final RemoveTagDialogFragment removeDialog = new RemoveTagDialogFragment();
      final String tagName = this.mTagsArrayAdapter.getItem(position);
      final Bundle arguments = new Bundle();
      arguments.putString(RemoveTagDialogFragment.ARGUMENT_TAG_NAME, tagName);
      removeDialog.setArguments(arguments);
      
      removeDialog.show(this.getFragmentManager(), "RemoveTagDialogFragment");
   }
   @Override
   protected void onResume() {
      this.resource.open();
      super.onResume();
   }

   @Override
   protected void onPause() {
      this.resource.close();
      super.onPause();
   }

   @Override
   public void onCreateNewTagDialogPositiveClick(DialogFragment dialog, String tagName) {
      try {
         this.resource.addTag(tagName);
      } catch (DuplicateTagNameException e) {
         if (Log.isLoggable(activityName, Log.ERROR)) {
            Log.e(activityName, "Failed to create a new tag: [" + tagName + "].", e);
         }
         this.displayErrorDialog(this.getResources().getString(R.string.msg_error_duplicate_tag_name, tagName));
      }
      this.refreshTagsArrayAdapter();
   }

   @Override
   public void onCreateNewTagDialogNegativeClick(DialogFragment dialog) {
      // do nothing
   }

   @Override
   public void onRemoveTagDialogPositiveClick(DialogFragment dialog, String tagName) {
      this.resource.removeTag(tagName);
      this.refreshTagsArrayAdapter();
   }

   @Override
   public void onRemoveTagDialogNegativeClick(DialogFragment dialog) {
      // do nothing
   }

   private void refreshTagsArrayAdapter() {
      this.mTagsArrayAdapter.clear();
      this.mTagsArrayAdapter.addAll(this.resource.listTags());
      this.mTagsArrayAdapter.notifyDataSetChanged();
   }

   private void displayErrorDialog(final String msg) {
      final ErrorDialogFragment errorDialog = new ErrorDialogFragment();
      final Bundle bundle = new Bundle();
      bundle.putString(ErrorDialogFragment.ARGUMENT_ERROR_MSG, msg);
      errorDialog.setArguments(bundle);
      
      errorDialog.show(this.getFragmentManager(), "ErrorDialogFragment");
   }
}
