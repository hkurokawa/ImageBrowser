package com.hkurokawa.imagebrowser.activity;

import com.hkurokawa.imagebrowser.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class CreateNewTagDialogFragment extends DialogFragment {
   private CreateNewTagDialogListener listener;

   @Override
   public void onAttach(Activity activity) {
      super.onAttach(activity);
      try {
         this.listener = (CreateNewTagDialogListener) activity;
      } catch (ClassCastException e) {
         // The activity doesn't implement the interface, throw exception
         throw new ClassCastException(activity.toString()
               + " must implement CreateNewTagDialogListener");
      }
   }

   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {
      final LayoutInflater inflater = this.getActivity().getLayoutInflater();
      final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
      final View dialogView = inflater.inflate(R.layout.manage_tags_create_tag_dialog_layout, null);
      builder.setView(dialogView)
      .setTitle(R.string.title_dialog_create_tag)
      .setPositiveButton(R.string.button_ok, new OnClickListener(){
         @Override
         public void onClick(DialogInterface dialog, int which) {
            final EditText editText = (EditText) dialogView.findViewById(R.id.create_tag_dialog_tag_name);
            final String tagName = editText.getText().toString();
            final CreateNewTagDialogFragment thisFragment = CreateNewTagDialogFragment.this;
            thisFragment.listener.onCreateNewTagDialogPositiveClick(thisFragment, tagName);
         }
      })
      .setNegativeButton(R.string.button_cancel, new OnClickListener(){
         @Override
         public void onClick(DialogInterface dialog, int which) {
            final EditText editText = (EditText) dialogView.findViewById(R.id.create_tag_dialog_tag_name);
            editText.clearComposingText();
            final CreateNewTagDialogFragment thisFragment = CreateNewTagDialogFragment.this;
            thisFragment.listener.onCreateNewTagDialogNegativeClick(thisFragment);
            
            thisFragment.getDialog().cancel();
         }
      });
      return builder.create();
   }
   
   public static interface CreateNewTagDialogListener {
      public void onCreateNewTagDialogPositiveClick(DialogFragment dialog, String tagName);
      public void onCreateNewTagDialogNegativeClick(DialogFragment dialog);
   }
}
