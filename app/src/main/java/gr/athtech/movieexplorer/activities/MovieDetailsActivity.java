package gr.athtech.movieexplorer.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import gr.athtech.movieexplorer.R;
import gr.athtech.movieexplorer.adapters.CastAdapter;
import gr.athtech.movieexplorer.data.appInterface.TMDbApiInterface;
import gr.athtech.movieexplorer.data.client.TMDbAPIClient;
import gr.athtech.movieexplorer.data.models.Movie;
import gr.athtech.movieexplorer.data.models.MovieDetails;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends NetworkCheck {

    private NetworkChangeReceiver networkChangeReceiver;

    private ImageView ivHorizontalPoster, ivVerticalPoster, ivProfile;
    private TextView tvTitle, tvOverview, tvGenres, tvPopularity, tvReleaseDate, tvBudget, tvRuntime, tvRating, tvCharacter, tvName;
    private ToggleButton toggleButtonFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Initialize views
        ivHorizontalPoster = findViewById(R.id.ivHorizontalPoster);
        ivVerticalPoster = findViewById(R.id.ivVerticalPoster);
        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        tvGenres = findViewById(R.id.tvGenres);
        tvPopularity = findViewById(R.id.tvPopularity);
        tvReleaseDate = findViewById(R.id.tvReleaseDate);
        tvBudget = findViewById(R.id.tvBudget);
        toggleButtonFavorite = (ToggleButton) findViewById(R.id.toggleButtonFavorite);


        // Get the movie id from the intent
        int movieId = getIntent().getIntExtra("movie_id", 0);

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.NetworkChangeReceiver");
        Intent intent = new Intent("android.intent.action.NetworkChangeReceiver");
        intent.putExtra("movie_id", movieId);
        registerReceiver(networkChangeReceiver, filter, String.valueOf(intent), null);


        // Call API to get movie details
        TMDbApiInterface apiInterface = TMDbAPIClient.getClient();
        Call<MovieDetails> movieDetailsCall = apiInterface.getMovieDetails(movieId);

        movieDetailsCall.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                // Check if the response is successful
                if (response.isSuccessful()) {
                    MovieDetails movieDetails = response.body();
                    RecyclerView rvCast = findViewById(R.id.rvCast);

                    rvCast.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    rvCast.setAdapter(new CastAdapter(MovieDetailsActivity.this, movieDetails.getCast()));
                    if (movieDetails != null) {
                        // Set the movie data
                        tvTitle.setText(movieDetails.getTitle());
                        tvOverview.setText(movieDetails.getOverview());
                        tvPopularity.setText("Popularity: " + movieDetails.getPopularity());
                        tvReleaseDate.setText("Release date: " + movieDetails.getRelease_date());

//                        for some reason rating and runtime crash the app when being set to the textview, but work fine when being displayed in an alert dialog wtf?
//                        tvRating.setText("Rating: " + movieDetails.getVote_average() + "/10");
//                        tvRuntime.setText("Runtime: " + movieDetails.getRuntime() + " minutes");

                        new AlertDialog.Builder(MovieDetailsActivity.this)
                                .setTitle("Rating & runtime")
                                .setMessage("Rating: " + movieDetails.getVote_average() + "\n" + "Runtime: " + movieDetails.getRuntime() + " minutes")
                                .setPositiveButton(android.R.string.ok, null)
                                .show();

                        // Format the genres as a string
                        MovieDetails.Genres[] genres = movieDetails.getGenres();
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < genres.length; i++) {
                            sb.append(genres[i]);
                            if (i < genres.length - 1) {
                                sb.append(", ");
                            }
                        }
                        tvGenres.setText("Genres: " + sb.toString());

                        // Format budget as group of 3 digits
                        String budget = movieDetails.getBudget();
                        StringBuilder budgetBuilder = new StringBuilder();
                        for (int i = 0; i < budget.length(); i++) {
                            budgetBuilder.append(budget.charAt(i));
                            if ((budget.length() - i) % 3 == 1 && i < budget.length() - 1) {
                                budgetBuilder.append(",");
                            }
                        }
                        if (budget == null || budget.equals("0")) {
                            tvBudget.setText("Budget: N/A");
                        } else {
                            tvBudget.setText("Budget: $" + budgetBuilder.toString());
                        }
                        // Load the movie posters
                        Glide.with(MovieDetailsActivity.this)
                                .load(movieDetails.getPoster_path())
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(ivVerticalPoster);

                        Glide.with(MovieDetailsActivity.this)
                                .load(movieDetails.getBackdrop_path())
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(ivHorizontalPoster);

                        Movie currentMovie = new Movie(movieDetails.getId(), movieDetails.getTitle(), movieDetails.getPoster_path(), false);

                        if (isFavorite(currentMovie.getId())) {
                            toggleButtonFavorite.setChecked(true);
                            toggleButtonFavorite.setCompoundDrawablesWithIntrinsicBounds(0, android.R.drawable.star_big_on, 0, 0);
                        } else {
                            toggleButtonFavorite.setChecked(false);
                            toggleButtonFavorite.setCompoundDrawablesWithIntrinsicBounds(0, android.R.drawable.star_big_off, 0, 0);
                        }


                        toggleButtonFavorite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean isFavorite = !isFavorite(currentMovie.getId());
                                currentMovie.setIsFavorite(isFavorite);
                                saveMovieAsFavorite(currentMovie.getId(), isFavorite);

                                toggleButtonFavorite.setChecked(isFavorite);

                                Toast.makeText(MovieDetailsActivity.this, isFavorite ? "Added to Favorites" : "Removed from Favorites", Toast.LENGTH_SHORT).show();
                            }
                        });

                        toggleButtonFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    // The toggle is enabled
                                    toggleButtonFavorite.setCompoundDrawablesWithIntrinsicBounds(0, android.R.drawable.star_big_on, 0, 0);
                                } else {
                                    // The toggle is disabled
                                    toggleButtonFavorite.setCompoundDrawablesWithIntrinsicBounds(0, android.R.drawable.star_big_off, 0, 0);
                                }
                            }
                        });




                    } else {
                        // Handle the case when the movie details are null
                        Toast.makeText(MovieDetailsActivity.this, "No details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case when the response is not successful
                    new AlertDialog.Builder(MovieDetailsActivity.this)
                            .setTitle("Error")
                            .setMessage("Error: " + response)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }


            }

            private boolean isFavorite(int movieId) {
                SharedPreferences sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE);
                return sharedPreferences.getBoolean(String.valueOf(movieId), false);
            }

            private void saveMovieAsFavorite(int movieId, boolean isFavorite) {
                SharedPreferences sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(String.valueOf(movieId), isFavorite);
                editor.apply();
            }


            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                // Handle failure
new AlertDialog.Builder(MovieDetailsActivity.this)
                        .setTitle("Network Error")
                        .setMessage("Network Error: " + t.getMessage())
                        .setPositiveButton(android.R.string.ok, null)
                        .show();            }
        });
    }
}