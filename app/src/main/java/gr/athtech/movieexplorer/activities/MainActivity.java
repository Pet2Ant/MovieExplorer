package gr.athtech.movieexplorer.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import gr.athtech.movieexplorer.R;
import gr.athtech.movieexplorer.adapters.MovieAdapter;
import gr.athtech.movieexplorer.data.appInterface.TMDbApiInterface;
import gr.athtech.movieexplorer.data.client.TMDbAPIClient;
import gr.athtech.movieexplorer.data.models.Movie;
import gr.athtech.movieexplorer.data.models.MovieDetails;
import gr.athtech.movieexplorer.data.models.MovieResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends NetworkCheck {
    private RecyclerView recyclerViewAllMovies, recyclerViewPopMovies, recyclerViewFavorites;
    private TextView allMovies, popMovies, noFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        allMovies = findViewById(R.id.allMovies);
        popMovies = findViewById(R.id.popMovies);
        noFavorites = findViewById(R.id.noFavorites);

        recyclerViewAllMovies = findViewById(R.id.recyclerViewAllMovies);
        recyclerViewPopMovies = findViewById(R.id.recyclerViewPopMovies);
        recyclerViewFavorites = findViewById(R.id.recyclerViewFavorites);

        recyclerViewAllMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewPopMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        fetchMovies();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchMovies();
    }

    private void fetchMovies() {
        // Call TMDb API to get all movies
        TMDbApiInterface apiInterface = TMDbAPIClient.getClient();
        Call<MovieResponse> moviesCall = apiInterface.getMovies();

        moviesCall.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                // Check if the response is successful
                List<Movie> movies = null;
                List<Movie> popMovies = null;
                if (response.isSuccessful()) {
                    MovieResponse movieResponse = response.body();
                    if (movieResponse != null && movieResponse.getResults() != null) {
                        movies = Arrays.asList(movieResponse.getResults());
                        popMovies = Arrays.asList(movieResponse.getResults());
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

                List<Movie> favoriteMovies = new ArrayList<>();
                for (Movie movie : movies) {
                    if (isFavorite(movie.getId())) {
                        favoriteMovies.add(movie);
                    }
                }
                MovieAdapter movieAdapter = new MovieAdapter(favoriteMovies, MainActivity.this);
                recyclerViewFavorites.setAdapter(movieAdapter);

                //show noFavorites text view if there are no favorites
                if (favoriteMovies.size() == 0) {
                    noFavorites.setVisibility(View.VISIBLE);
                } else {
                    noFavorites.setVisibility(View.GONE);
                }

                List<Movie> popularMovies = new ArrayList<>(popMovies);
                Collections.sort(popMovies, new Comparator<Movie>() {
                    @Override
                    public int compare(Movie m1, Movie m2) {
                        return Double.compare(m2.getVote_average(), m1.getVote_average());
                    }
                });
                MovieAdapter popMovieAdapter = new MovieAdapter(popularMovies, MainActivity.this);
                recyclerViewPopMovies.setAdapter(popMovieAdapter);
            }


            private boolean isFavorite(int movieId) {
                SharedPreferences sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE);
                return sharedPreferences.getBoolean(String.valueOf(movieId), false);
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
