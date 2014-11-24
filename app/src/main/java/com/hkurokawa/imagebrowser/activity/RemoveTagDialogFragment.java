package com.hkurokawa.imagebrowser.activity;

import com.hkurokawa.imagebrowser.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class RemoveTagDialogFragment extends DialogFragment {
   public static final String ARGUMENT_TAG_NAME = RemoveTagDialogFragment.class.getName() + ".tagName";
   private RemoveTagDialogListener listener;
   
   @Override
   public void onAttach(Activity activity) {
      super.onAttach(activity);
      try {
         this.listener = (RemoveTagDialogListener) activity;
      } catch (ClassCastException e) {
         // The activity doesn't implement the interface, throw exception
         throw new ClassCastException(activity.toString()
               + " must implement RemoveTagDialogListener");
      }
   }
   
   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {
      final Bundle bundle = this.getArguments();
      final String tagName = bundle.getString(ARGUMENT_TAG_NAME);
      
      final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
      builder
      .setTitle(this.getResources().getString(R.string.title_dialog_remove_tag, tagName))
      .setPositiveButton(R.string.button_ok, new OnClickListener(){
         @Override
         public void onClick(DialogInterface dialog, int which) {
            RemoveTagDialogFragment.this.listener.onRemoveTagDialogPositiveClick(RemoveTagDialogFragment.this, tagName);
         }
      }).setNegativeButton(R.string.button_cancel, new OnClickListener(){
         @Override
         public void onClick(DialogInterface dialog, int which) {
            RemoveTagDialogFragment.this.listener.onRemoveTagDialogNegativeClick(RemoveTagDialogFragment.this);
            dialog.cancel();
         }
      });
      
      return builder.create();
   }
   
   public interface RemoveTagDialogListener {
      public void onRemoveTagDialogPositiveClick(final DialogFragment dialog, final String tagName);
      public void onRemoveTagDialogNegativeClick(final DialogFragment dialog);
   }
}
