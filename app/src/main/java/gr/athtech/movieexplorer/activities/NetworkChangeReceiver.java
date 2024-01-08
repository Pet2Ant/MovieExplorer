package gr.athtech.movieexplorer.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import gr.athtech.movieexplorer.data.appInterface.TMDbApiInterface;
import gr.athtech.movieexplorer.data.client.TMDbAPIClient;

public abstract class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (isOnline(context)) {
            int movieId = intent.getIntExtra("movie_id", 0);
            TMDbApiInterface apiInterface = TMDbAPIClient.getClient();
            apiInterface.getMovies();
            apiInterface.getMovieDetails(movieId);
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return (netInfo != null);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}
