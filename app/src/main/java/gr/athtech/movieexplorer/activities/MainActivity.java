package gr.athtech.movieexplorer.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private RecyclerView recyclerViewAllMovies, recyclerViewtopMovies, recyclerViewFavorites, getRecyclerViewPopularMovies;
    private TextView allMovies, topMovies, noFavorites, popularMovies;
    private Button randomButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        allMovies = findViewById(R.id.topRatedMovies);
        topMovies = findViewById(R.id.popMovies);
        noFavorites = findViewById(R.id.noFavorites);
        popularMovies = findViewById(R.id.allMovies);
        randomButton = findViewById(R.id.randomButton);

        recyclerViewAllMovies = findViewById(R.id.recyclerViewAllMovies);
        recyclerViewtopMovies = findViewById(R.id.recyclerViewTopMovies);
        recyclerViewFavorites = findViewById(R.id.recyclerViewFavorites);
        getRecyclerViewPopularMovies = findViewById(R.id.recyclerViewPopularMovies);

        recyclerViewAllMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewtopMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        getRecyclerViewPopularMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Add swipe to refresh
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        final ScrollView scrollView = findViewById(R.id.scrollView);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                    fetchMovies();
                pullToRefresh.setRefreshing(false);
            }
        });
//        fetch movies
        fetchMovies();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchMovies();
    }

    private void fetchMovies() {
        // Call API to get all movies
        TMDbApiInterface apiInterface = TMDbAPIClient.getClient();
        Call<MovieResponse> moviesCall = apiInterface.getMovies();

        moviesCall.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {

                // Check if the response is successful
                List<Movie> movies;
                List<Movie> topMovies = null;
                List<Movie> popularMovies = null;
                if (response.isSuccessful()) {
                    MovieResponse movieResponse = response.body();
                    if (movieResponse != null && movieResponse.getResults() != null) {
                        movies = Arrays.asList(movieResponse.getResults());
                        topMovies = Arrays.asList(movieResponse.getResults());
                        popularMovies = Arrays.asList(movieResponse.getResults());
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

                getFavoriteMovies();
                getTopMovies(topMovies);
                getPopularMovies(popularMovies);

                randomButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getRandomMovie();
                    }
                });
            }

            // method to get top rated movies
            private void getTopMovies(List<Movie> topMovies){
                List<Movie> topRatedMovies = new ArrayList<>(topMovies);
                Collections.sort(topRatedMovies, new Comparator<Movie>() {
                    @Override
                    public int compare(Movie m1, Movie m2) {
                        return Double.compare(m2.getVote_average(), m1.getVote_average());
                    }
                });
                MovieAdapter topMovieAdapter = new MovieAdapter(topRatedMovies, MainActivity.this);
                recyclerViewtopMovies.setAdapter(topMovieAdapter);
            }

            // method to get popular movies
            private void getPopularMovies(List<Movie> popularMovies){
                List<Movie> popularMoviesList = new ArrayList<>(popularMovies);
                Collections.sort(popularMoviesList, new Comparator<Movie>() {
                    @Override
                    public int compare(Movie m1, Movie m2) {
                        return Double.compare(m2.getPopularity(), m1.getPopularity());
                    }
                });
                MovieAdapter popularMovieAdapter = new MovieAdapter(popularMoviesList, MainActivity.this);
                getRecyclerViewPopularMovies.setAdapter(popularMovieAdapter);
            }


//            method to get favorite movies
            private void getFavoriteMovies(){
                List<Movie> favoriteMovies = new ArrayList<>();
                Map sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE).getAll();
                for (Object key : sharedPreferences.keySet()) {
                    if (sharedPreferences.get(key).equals(true)) {
                        System.out.println(key + " " + sharedPreferences.get(key));
                        int movieId = Integer.parseInt(key.toString());
                        Call<MovieDetails> movieDetailsCall = apiInterface.getMovieDetails(movieId);
                        List<Movie> finalFavoriteMovies = favoriteMovies;
                        movieDetailsCall.enqueue(new Callback<MovieDetails>() {
                            @Override
                            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {

                                if (response.isSuccessful()) {
                                    MovieDetails movieDetails = response.body();
                                    if (movieDetails != null) {
                                        String title = movieDetails.getTitle();
                                        String posterPath = movieDetails.getPoster_path();
                                        finalFavoriteMovies.add(new Movie(movieId, title, posterPath, true));


                                        MovieAdapter favoriteMovieAdapter = new MovieAdapter(finalFavoriteMovies, MainActivity.this);
                                        recyclerViewFavorites.setAdapter(favoriteMovieAdapter);

                                        if (finalFavoriteMovies.size() == 0) {
                                            System.out.println("no favorites");
                                            noFavorites.setVisibility(View.VISIBLE);
                                        } else if (finalFavoriteMovies.size() > 0) {
                                            System.out.println("favorites");
                                            noFavorites.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }
                            @Override
                            public void onFailure(Call<MovieDetails> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                if (favoriteMovies.size() == 0) {
                    noFavorites.setVisibility(View.VISIBLE);
                    recyclerViewFavorites.setAdapter(null);
                }
            }

            // method to get random movie
            private void getRandomMovie() {
                final int[] randomMovie = {(int) (Math.random() * 10000)};
                TMDbApiInterface apiInterface = TMDbAPIClient.getClient();
                Call<MovieDetails> movieDetailsCall = apiInterface.getMovieDetails(randomMovie[0]);
                movieDetailsCall.enqueue(new Callback<MovieDetails>() {
                    @Override
                    public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                        if (response.isSuccessful()) {
                            MovieDetails movieDetails = response.body();
                            if (movieDetails != null) {
                                for (int i : randomMovie) {
                                    if (i == movieDetails.getId()) {
                                        Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                                        intent.putExtra("movie_id", randomMovie[0]);
                                        startActivity(intent);
                                    }
                                }
                            }
                        } else {
                            getRandomMovie();
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieDetails> call, Throwable t) {
                        getRandomMovie();
                    }
                });
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
