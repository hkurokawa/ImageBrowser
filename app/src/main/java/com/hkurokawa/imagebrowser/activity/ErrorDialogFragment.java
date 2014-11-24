package com.hkurokawa.imagebrowser.activity;

import com.hkurokawa.imagebrowser.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class ErrorDialogFragment extends DialogFragment {
   public static String ARGUMENT_ERROR_MSG = ErrorDialogFragment.class.getName() + ".errorMsg";
   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {
      final Bundle bundle = this.getArguments();
      final String msg = bundle.getString(ARGUMENT_ERROR_MSG);

      final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
      builder
      .setTitle(R.string.title_dialog_error)
      .setMessage(msg)
      .setNeutralButton(R.string.button_ok, new OnClickListener(){
         @Override
         public void onClick(DialogInterface dialog, int which) {
         }
      });

      return builder.create();
   }
}
