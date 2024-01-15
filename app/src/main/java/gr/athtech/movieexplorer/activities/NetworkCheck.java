package gr.athtech.movieexplorer.activities;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Objects;

public abstract class NetworkCheck extends AppCompatActivity {

    private boolean wasDisconnected = false;

    private final BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkConnected()) {
                // Network is available
                if (wasDisconnected) {
                    // Get the current activity
                    ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
                    List<ActivityManager.AppTask> taskInfo = am.getAppTasks();
                    String currentActivity = Objects.requireNonNull(taskInfo.get(0).getTaskInfo().topActivity).getClassName();
                    if (currentActivity.equals("gr.athtech.movieexplorer.activities.MainActivity")) {
                        // If MainActivity is on the screen, refresh it
                        Intent i = new Intent(context, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } else {
                        // If another activity is on the screen, refresh that
                        Intent i = new Intent(context, MovieDetailsActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("movie_id", getIntent().getIntExtra("movie_id", 0));
                        startActivity(i);
                    }
                    wasDisconnected = false;
                }

            } else {
                // Network is unavailable
                showNetworkAlert();
                wasDisconnected = true;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister BroadcastReceiver when app is in background.
        unregisterReceiver(networkChangeReceiver);
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
