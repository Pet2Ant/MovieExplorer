package gr.athtech.movieexplorer.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import gr.athtech.movieexplorer.R;
import gr.athtech.movieexplorer.adapters.MovieAdapter;
import gr.athtech.movieexplorer.data.appInterface.TMDbApiInterface;
import gr.athtech.movieexplorer.data.client.TMDbAPIClient;
import gr.athtech.movieexplorer.data.models.Movie;
import gr.athtech.movieexplorer.data.models.MovieResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends NetworkCheck {
    private RecyclerView recyclerViewAllMovies;
    private TextView allMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        allMovies = findViewById(R.id.allMovies);

        recyclerViewAllMovies = findViewById(R.id.recyclerViewAllMovies);
        recyclerViewAllMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Call TMDb API to get all movies
        TMDbApiInterface apiInterface = TMDbAPIClient.getClient();
        Call<MovieResponse> moviesCall = apiInterface.getMovies();

        moviesCall.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                // Check if the response is successful
                if (response.isSuccessful()) {
                    MovieResponse movieResponse = response.body();
                    if (movieResponse != null && movieResponse.getResults() != null) {
                        List<Movie> movies = Arrays.asList(movieResponse.getResults());
                        MovieAdapter movieAdapter = new MovieAdapter(movies, MainActivity.this);
                        recyclerViewAllMovies.setAdapter(movieAdapter);
                    } else {
                        // Handle the case when the movie response is null or has no results
                        Toast.makeText(MainActivity.this, "No results", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case when the response is not successful
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Error")
                            .setMessage("Error: " + response)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                     recyclerViewAllMovies.setAdapter(null);
                    recyclerViewAllMovies.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                // Handle failure
                Toast.makeText(MainActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                recyclerViewAllMovies.setAdapter(null);
                recyclerViewAllMovies.setVisibility(View.GONE);
            }
        });
    }
}
