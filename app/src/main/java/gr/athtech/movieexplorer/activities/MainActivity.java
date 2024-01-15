package gr.athtech.movieexplorer.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
//    declare views
    private RecyclerView recyclerViewAllMovies, recyclerViewtopMovies, recyclerViewFavorites, getRecyclerViewPopularMovies;
    private TextView allMovies, topMovies, noFavorites, popularMovies;
    private Button randomButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        set dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);

        // initialize views
        allMovies = findViewById(R.id.topRatedMovies);
        topMovies = findViewById(R.id.popMovies);
        noFavorites = findViewById(R.id.noFavorites);
        popularMovies = findViewById(R.id.allMovies);
        randomButton = findViewById(R.id.randomButton);
        progressBar = findViewById(R.id.progressBar1);

        recyclerViewAllMovies = findViewById(R.id.recyclerViewAllMovies);
        recyclerViewtopMovies = findViewById(R.id.recyclerViewTopMovies);
        recyclerViewFavorites = findViewById(R.id.recyclerViewFavorites);
        getRecyclerViewPopularMovies = findViewById(R.id.recyclerViewPopularMovies);

        // set layout manager
        recyclerViewAllMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewtopMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        getRecyclerViewPopularMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Add swipe to refresh
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
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
        //    deletes memory cache after a limit is reached
        Context context = getApplicationContext();
        File cacheDir = context.getCacheDir();
        long size = getFolderSize(cacheDir);

        long limit = 50 * 1024 * 1024;
        if (size > limit) {
            deleteDir(cacheDir);
        }


        // Call API to get all movies
        TMDbApiInterface apiInterface = TMDbAPIClient.getClient();
        Call<MovieResponse> moviesCall = apiInterface.getMovies();
        progressBar.setVisibility(View.VISIBLE);


//        call to get movies
        moviesCall.enqueue(new Callback<MovieResponse>() {

            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                progressBar.setVisibility(View.GONE);


//                initialize required lists
                List<Movie> movies;
                List<Movie> topMovies = null;
                List<Movie> popularMovies = null;

                // Check if the response is successful
                if (response.isSuccessful()) {
//                    Get the response body
                    MovieResponse movieResponse = response.body();

//                    check if the response has results
                    if (movieResponse != null && movieResponse.getResults() != null) {
//                      fill the movies list with the results
                        movies = Arrays.asList(movieResponse.getResults());
                        topMovies = Arrays.asList(movieResponse.getResults());
                        popularMovies = Arrays.asList(movieResponse.getResults());

//                        shuffle the movies
                        Collections.shuffle(movies);
//                        set the adapter
                        MovieAdapter movieAdapter = new MovieAdapter(movies, MainActivity.this);
                        recyclerViewAllMovies.setAdapter(movieAdapter);

                    } else {
                        // Handle the case when the movie response is null or has no results
                        Toast.makeText(MainActivity.this, "No results", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case when the response is not successful
                    Toast.makeText(MainActivity.this, "Network Error: " + response.message(), Toast.LENGTH_SHORT).show();
                    recyclerViewAllMovies.setAdapter(null);
                    recyclerViewAllMovies.setVisibility(View.GONE);
                }

                // get favorite movies
                getFavoriteMovies();

//                get top and popular movies
                getTopMovies(topMovies);
                getPopularMovies(popularMovies);

//                get random movie when button is clicked
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

//                sort the movies by vote average
                Collections.sort(topRatedMovies, new Comparator<Movie>() {
                    @Override
                    public int compare(Movie m1, Movie m2) {
                        return Double.compare(m2.getVote_average(), m1.getVote_average());
                    }
                });

//                set the adapter
                MovieAdapter topMovieAdapter = new MovieAdapter(topRatedMovies, MainActivity.this);
                recyclerViewtopMovies.setAdapter(topMovieAdapter);
            }

            // method to get popular movies
            private void getPopularMovies(List<Movie> popularMovies){
                List<Movie> popularMoviesList = new ArrayList<>(popularMovies);

//                sort the movies by popularity
                Collections.sort(popularMoviesList, new Comparator<Movie>() {
                    @Override
                    public int compare(Movie m1, Movie m2) {
                        return Double.compare(m2.getPopularity(), m1.getPopularity());
                    }
                });
//                set the adapter
                MovieAdapter popularMovieAdapter = new MovieAdapter(popularMoviesList, MainActivity.this);
                getRecyclerViewPopularMovies.setAdapter(popularMovieAdapter);
            }


//            method to get favorite movies
            private void getFavoriteMovies(){
                List<Movie> favoriteMovies = new ArrayList<>();

                Map sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE).getAll();
//                get the ID of the movie from shared preferences if key is true
                for (Object key : sharedPreferences.keySet()) {
                    if (Objects.equals(sharedPreferences.get(key), true)) {
                        System.out.println(key + " " + sharedPreferences.get(key));
                        int movieId = Integer.parseInt(key.toString());

//                        call to get movie details for each favorited movie
                        Call<MovieDetails> movieDetailsCall = apiInterface.getMovieDetails(movieId);
                        movieDetailsCall.enqueue(new Callback<MovieDetails>() {
                            @Override
                            public void onResponse(@NonNull Call<MovieDetails> call, @NonNull Response<MovieDetails> response) {

                                if (response.isSuccessful()) {
                                    MovieDetails movieDetails = response.body();
                                    if (movieDetails != null) {
//                                        if response is valid, get the movie details
                                        String title = movieDetails.getTitle();
                                        String posterPath = movieDetails.getPoster_path();

//                                        add the movie to the favorite movies list
                                        favoriteMovies.add(new Movie(movieId, title, posterPath, true));

//                                        set the adapter
                                        MovieAdapter favoriteMovieAdapter = new MovieAdapter(favoriteMovies, MainActivity.this);
                                        recyclerViewFavorites.setAdapter(favoriteMovieAdapter);

//                                        if there are no favorites, show the message that there are no favorites
                                        if (favoriteMovies.size() == 0) {
                                            System.out.println("no favorites");
                                            noFavorites.setVisibility(View.VISIBLE);
                                        } else if (favoriteMovies.size() > 0) {
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
//                get random int and check if it is a valid movie id, and if it is, start the movie details activity
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

//    method to get the size of the cache
    public static long getFolderSize(File dir) {
        long size = 0;
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isFile()) {
                size += file.length();
            }
            else
                size += getFolderSize(file);
        }
        return size;
    }

//    method to delete the cache
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < Objects.requireNonNull(children).length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        assert dir != null;
        return dir.delete();
    }

}
