package gr.athtech.movieexplorer.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;

import gr.athtech.movieexplorer.R;
import gr.athtech.movieexplorer.adapters.CastAdapter;
import gr.athtech.movieexplorer.adapters.CrewAdapter;
import gr.athtech.movieexplorer.data.appInterface.TMDbApiInterface;
import gr.athtech.movieexplorer.data.client.TMDbAPIClient;
import gr.athtech.movieexplorer.data.models.Movie;
import gr.athtech.movieexplorer.data.models.MovieDetails;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends NetworkCheck {

//    Declare views

    private ImageView ivHorizontalPoster, ivVerticalPoster, ivProfile, iv_goBack;
    private TextView tvTitle, tvOverview, tvGenres, tvPopularity, tvReleaseDate, tvBudget, tvRuntime, tvRating, tvCharacter, tvName, toolbarTitle;
    private ToggleButton toggleButtonFavorite, toggleButtonShare;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // initialize views
        ivHorizontalPoster = findViewById(R.id.ivHorizontalPoster);
        ivVerticalPoster = findViewById(R.id.ivVerticalPoster);
        iv_goBack = findViewById(R.id.iv_goBack);
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
        toolbarTitle = findViewById(R.id.toolbarTitle);
        progressBar = findViewById(R.id.progressBar1);


//      Go back to previous activity when pressing the back button on toolbar
        iv_goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Get the movie id from the intent
        int movieId = getIntent().getIntExtra("movie_id", 0);

        // Call API to get movie details
        TMDbApiInterface apiInterface = TMDbAPIClient.getClient();
        Call<MovieDetails> movieDetailsCall = apiInterface.getMovieDetails(movieId);
        progressBar.setVisibility(View.VISIBLE);

        movieDetailsCall.enqueue(new Callback<MovieDetails>() {

            @Override
            public void onResponse(@NonNull Call<MovieDetails> call, @NonNull Response<MovieDetails> response) {
                progressBar.setVisibility(View.GONE);
                // Check if the response is successful
                if (response.isSuccessful()) {
                    MovieDetails movieDetails = response.body();
                    RecyclerView rvCast = findViewById(R.id.rvCast);
                    RecyclerView rvCrew = findViewById(R.id.rvCrew);

                    rvCast.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    rvCast.setAdapter(new CastAdapter(MovieDetailsActivity.this, movieDetails.getCast()));

                    rvCrew.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    rvCrew.setAdapter(new CrewAdapter(MovieDetailsActivity.this, movieDetails.getCrew()));

                    Movie currentMovie = new Movie(movieDetails.getId(), movieDetails.getTitle(), movieDetails.getPoster_path(), false);

                    formatMovieDetails(movieDetails);
                    favoriteMovie(currentMovie);
                    shareMovie(movieDetails);

                } else {
                    // Handle the case when the response is not successful
                    Toast.makeText(MovieDetailsActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            //            get a boolean value from shared preferences, that signifies if the movie is favorite or not
            private boolean isFavorite(int movieId) {
                SharedPreferences sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE);
                return sharedPreferences.getBoolean(String.valueOf(movieId), false);
            }

            //            save the movie as favorite in shared preferences
            private void saveMovieAsFavorite(int movieId, boolean isFavorite) {
                SharedPreferences sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(String.valueOf(movieId), isFavorite);
                editor.apply();
            }

            //            set the favorite button to checked or unchecked, depending on the value of isFavorite
            private void favoriteMovie(Movie currentMovie) {
                if (isFavorite(currentMovie.getId())) {
                    toggleButtonFavorite.setChecked(true);
                    toggleButtonFavorite.setCompoundDrawablesWithIntrinsicBounds(0, android.R.drawable.star_big_on, 0, 0);
                } else {
                    toggleButtonFavorite.setChecked(false);
                    toggleButtonFavorite.setCompoundDrawablesWithIntrinsicBounds(0, android.R.drawable.star_big_off, 0, 0);
                }

//                when the favorite button is clicked, save the movie as favorite in shared preferences
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

//                check or uncheck the favorite button, depending on the value of isFavorite
                toggleButtonFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        toggleButtonFavorite.setCompoundDrawablesWithIntrinsicBounds(0, isChecked ? android.R.drawable.star_big_on : android.R.drawable.star_big_off, 0, 0);
                    }
                });
            }

            //            method to share the movie when the button is clicked
            private void shareMovie(MovieDetails movieDetails) {
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

            //            format budget to add commas in the right places
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

            //            format runtime to display hours and minutes depending on the value
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

            //            format rating to display stars
            private void formatRating(double rating) {
                int numStars = (int) Math.round(rating);
                RatingBar ratingBar = findViewById(R.id.ratingBar);
                ratingBar.setNumStars(10);
                ratingBar.setStepSize(1.0f);

                // Set the rating
                ratingBar.setRating(numStars);
            }


            //           format popularity to display only one decimal
            private void formatPopularity(double popularity) {
                DecimalFormat df = new DecimalFormat("#.#");
                String popularityFormatted = df.format(popularity);
                tvPopularity.setText("Popularity: " + popularityFormatted);
            }

            //            format release date to display day/month/year
            private void formatReleaseDate(String releaseDate) {
                String[] releaseDateArray = releaseDate.split("-");
                String year = releaseDateArray[0];
                String month = releaseDateArray[1];
                String day = releaseDateArray[2];
                tvReleaseDate.setText("Release date: " + day + "/" + month + "/" + year);
            }

            //            format genres to display them in a comma separated list
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

            //            format overview to display N/A if it is null or empty
            private void formatOverview(String overview) {
                if (overview == null || overview.equals("")) {
                    tvOverview.setText("N/A");
                } else {
                    tvOverview.setText(overview);
                }
            }

            //            format title to display N/A if it is null or empty
            private void formatTitle(String title) {
                if (title == null || title.equals("")) {
                    tvTitle.setText("N/A");
                } else {
                    tvTitle.setText(title);
                }
            }

            //            format poster path to display placeholder if it is null or empty
            private void formatPosterPath(String posterPath) {
                if (posterPath == null || posterPath.equals("")) {
                    Glide.with(MovieDetailsActivity.this)
                            .load(R.drawable.placeholder)
                            .into(ivVerticalPoster);
                } else {
                    Glide.with(MovieDetailsActivity.this)
                            .load(posterPath)
                            .placeholder(R.drawable.placeholder)
                            .into(ivVerticalPoster);
                }
            }

            //            format backdrop path to display placeholder if it is null or empty
            private void formatBackdropPath(String backdropPath) {
                if (backdropPath == null || backdropPath.equals("")) {
                    Glide.with(MovieDetailsActivity.this)
                            .load(R.drawable.placeholder)
                            .into(ivHorizontalPoster);
                } else {
                    Glide.with(MovieDetailsActivity.this)
                            .load(backdropPath)
                            .placeholder(R.drawable.placeholder)
                            .into(ivHorizontalPoster);
                }
            }


            //            format all of the movie details using previously defined methods
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
                toolbarTitle.setText(movieDetails.getTitle());
            }

            //            method to check if a string is null or empty and return N/A if it is
            private String checkNullOrEmpty(String str) {
                return (str == null || str.equals("")) ? "N/A" : str;
            }

            private void loadImage(String path, ImageView imageView) {
                if (checkNullOrEmpty(path).equals("N/A")) {
                    Glide.with(MovieDetailsActivity.this)
                            .load(R.drawable.placeholder)
                            .into(imageView);
                } else {
                    Glide.with(MovieDetailsActivity.this)
                            .load(path)
                            .placeholder(R.drawable.placeholder)
                            .into(imageView);
                }
            }


            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                // Handle failure
                Toast.makeText(MovieDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}