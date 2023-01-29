package es.uca.tfg.conexionmorada.verify;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;

import androidx.fragment.app.FragmentActivity;

import es.uca.tfg.conexionmorada.ui.HomeFragment;

public class VerifyDialog {
    private Activity activity;


    public VerifyDialog(Activity activity) {
        this.activity = activity;
    }

    public void startDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Verificación de cuenta");
        builder.setMessage("Su cuenta no ha sido verificada.Por favor verificalo ahora para poder utilizar la aplicación.");
        builder.setCancelable(false);
        builder.setPositiveButton("Sí", (dialog, which) -> {
            Intent intent = new Intent(activity, VerifyActivity.class);
            activity.startActivity(intent);
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            //navegate to homefragment
            activity.finishAffinity();
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
