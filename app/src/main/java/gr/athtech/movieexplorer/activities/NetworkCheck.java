package gr.athtech.movieexplorer.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public abstract class NetworkCheck extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        if (!isNetworkConnected()) {
            showNetworkAlert();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void showNetworkAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("An internet connection is required.")
                .setCancelable(false)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!isNetworkConnected()) {
                            showNetworkAlert();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
