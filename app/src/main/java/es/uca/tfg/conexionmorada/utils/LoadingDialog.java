package es.uca.tfg.conexionmorada.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import es.uca.tfg.conexionmorada.R;

public class LoadingDialog {

    private Activity activity;
    public AlertDialog alertDialog;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }

    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.loading_layout, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void dismissDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }
}