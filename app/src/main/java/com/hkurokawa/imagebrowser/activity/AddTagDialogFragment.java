package com.hkurokawa.imagebrowser.activity;

import com.hkurokawa.imagebrowser.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class AddTagDialogFragment extends DialogFragment {
   private AddTagDialogListener listener;
   private String[] items;
   private boolean[] checkedItems;

   public void setTags(final List<String> availableTags, final Set<String> selectedTags) {
      final String[] tags = availableTags.toArray(new String[availableTags.size()]);
      final boolean[] checkedTags = new boolean[tags.length];
      for (int i=0; i<tags.length; i++) {
         if (selectedTags.contains(tags[i])) {
            checkedTags[i] = true;
         }
      }
      this.items = tags;
      this.checkedItems = checkedTags;
   }

   public Set<String> getSelectedTags() {
      final Set<String> selectedTags = new HashSet<String>();
      for (int i=0; i<AddTagDialogFragment.this.items.length; i++) {
         if (AddTagDialogFragment.this.checkedItems[i]) {
            selectedTags.add(AddTagDialogFragment.this.items[i]);
         }
      }
      return selectedTags;
   }

   @Override
   public void onAttach(Activity activity) {
      super.onAttach(activity);
      try {
         this.listener = (AddTagDialogListener) activity;
      } catch (ClassCastException e) {
         throw new ClassCastException(activity.toString() + " must implement AddTagDialogListener: " + e.getMessage());
      }
   }
   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {
      final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
      builder
      .setTitle(R.string.dialog_add_tags_title)
      .setMultiChoiceItems(this.items, this.checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            AddTagDialogFragment.this.checkedItems[which] = isChecked;
         }
      })
      .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            AddTagDialogFragment.this.listener.onAddTagDialogPositiveClick(AddTagDialogFragment.this);
         }
      })
      .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            AddTagDialogFragment.this.listener.onAddTagDialogNegativeClick(AddTagDialogFragment.this);
         }
      });
      
      return builder.create();
   }
   public static interface AddTagDialogListener {
      public void onAddTagDialogPositiveClick(final AddTagDialogFragment dialog);
      public void onAddTagDialogNegativeClick(final AddTagDialogFragment dialog);
   }
}
