package com.example.pcg;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.Layout;
import android.view.LayoutInflater;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog alertDialog;

    LoadingDialog(Activity myActivity){
        activity = myActivity;
    }

    public void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.prgress_dialog, null));
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void dismissDialog(){
        alertDialog.dismiss();
    }
}