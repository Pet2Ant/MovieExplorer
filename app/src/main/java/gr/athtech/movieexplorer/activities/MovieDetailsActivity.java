package gr.athtech.movieexplorer.activities;

import android.annotation.SuppressLint;
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

import java.text.DecimalFormat;

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

    private ImageView ivHorizontalPoster, ivVerticalPoster, ivProfile;
    private TextView tvTitle, tvOverview, tvGenres, tvPopularity, tvReleaseDate, tvBudget, tvRuntime, tvRating, tvCharacter, tvName;
    private ToggleButton toggleButtonFavorite, toggleButtonShare;

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
        toggleButtonFavorite = findViewById(R.id.toggleButtonFavorite);
        toggleButtonShare = findViewById(R.id.toggleButtonShare);
        tvRuntime = findViewById(R.id.tvRuntime);
        tvRating = findViewById(R.id.tvRating);

        // Get the movie id from the intent
        int movieId = getIntent().getIntExtra("movie_id", 0);

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.NetworkChangeReceiver");
        Intent intent = new Intent("android.intent.action.NetworkChangeReceiver");
        intent.putExtra("movie_id", movieId);


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

                        Movie currentMovie = new Movie(movieDetails.getId(), movieDetails.getTitle(), movieDetails.getPoster_path(), false);

                        formatMovieDetails(movieDetails);
                        favoriteMovie(currentMovie);
                        shareMovie(movieDetails);

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

            private void favoriteMovie(Movie currentMovie){
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
            }

            private void shareMovie(MovieDetails movieDetails){
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this movie: " + movieDetails.getTitle() + "\n" + "https://www.themoviedb.org/movie/" + movieDetails.getId());
                shareIntent.setType("text/plain");
                toggleButtonShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(Intent.createChooser(shareIntent, "Share via"));
                    }
                });
            }
            private void formatBudget(String budget) {
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
            }

            private void formatRuntime(int runtime) {
                int hours = runtime / 60;
                int minutes = runtime % 60;
                String hourUnit = (hours == 1) ? " hour " : " hours ";
                String minuteUnit = (minutes == 1) ? " minute" : " minutes";

                if (runtime == 0) {
                    tvRuntime.setText("Runtime: N/A");
                } else if (hours == 0) {
                    tvRuntime.setText("Runtime: " + minutes + minuteUnit);
                } else if (minutes == 0) {
                    tvRuntime.setText("Runtime: " + hours + hourUnit);
                } else {
                    tvRuntime.setText("Runtime: " + hours + hourUnit + minutes + minuteUnit);
                }
            }

            DecimalFormat df = new DecimalFormat("#.#");

            private void formatRating(double rating) {
                String ratingFormatted = df.format(rating);
                tvRating.setText("Rating: " + ratingFormatted + "/10");
            }

            private void formatPopularity(double popularity) {
                String popularityFormatted = df.format(popularity);
                tvPopularity.setText("Popularity: " + popularityFormatted);
            }

            private void formatReleaseDate(String releaseDate) {
                String[] releaseDateArray = releaseDate.split("-");
                String year = releaseDateArray[0];
                String month = releaseDateArray[1];
                String day = releaseDateArray[2];
                tvReleaseDate.setText("Release date: " + month + "/" + day + "/" + year);
            }

            private void formatGenres(MovieDetails.Genres[] genres) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < genres.length; i++) {
                    sb.append(genres[i]);
                    if (i < genres.length - 1) {
                        sb.append(", ");
                    }
                }
                tvGenres.setText("Genres: " + sb.toString());
            }

            private void formatOverview(String overview) {
                tvOverview.setText("Overview: " + checkNullOrEmpty(overview));
            }

            private void formatTitle(String title) {
                tvTitle.setText("Title: " + checkNullOrEmpty(title));
            }

            private void formatPosterPath(String posterPath) {
                loadImage(posterPath, ivVerticalPoster);
            }

            private void formatBackdropPath(String backdropPath) {
                loadImage(backdropPath, ivHorizontalPoster);
            }


            private void formatMovieDetails(MovieDetails movieDetails) {
                formatTitle(movieDetails.getTitle());
                formatOverview(movieDetails.getOverview());
                formatGenres(movieDetails.getGenres());
                formatPopularity(movieDetails.getPopularity());
                formatReleaseDate(movieDetails.getRelease_date());
                formatBudget(movieDetails.getBudget());
                formatRuntime(movieDetails.getRuntime());
                formatRating(movieDetails.getVote_average());
                formatPosterPath(movieDetails.getPoster_path());
                formatBackdropPath(movieDetails.getBackdrop_path());
            }

            private String checkNullOrEmpty(String str) {
                return (str == null || str.equals("")) ? "N/A" : str;
            }

            private void loadImage(String path, ImageView imageView) {
                if (checkNullOrEmpty(path).equals("N/A")) {
                    Glide.with(MovieDetailsActivity.this)
                            .load(R.drawable.ic_launcher_background)
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(imageView);
                } else {
                    Glide.with(MovieDetailsActivity.this)
                            .load(path)
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(imageView);
                }
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                // Handle failure
new AlertDialog.Builder(MovieDetailsActivity.this)
                        .setTitle("Network Error")
                        .setMessage("Network Error: " + t.getMessage())
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        });
    }
}