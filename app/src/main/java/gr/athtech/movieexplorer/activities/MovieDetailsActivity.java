package gr.athtech.movieexplorer.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import gr.athtech.movieexplorer.R; // Replace with your actual package name
import gr.athtech.movieexplorer.data.appInterface.TMDbApiInterface;
import gr.athtech.movieexplorer.data.client.TMDbAPIClient;
import gr.athtech.movieexplorer.data.models.Movie;
import gr.athtech.movieexplorer.data.models.MovieDetails;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    private RecyclerView rvCast;
    private RecyclerView rvRecommendContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Initialize views
        rvCast = findViewById(R.id.rvCast);
        rvRecommendContents = findViewById(R.id.rvRecommendContents);

        // Get movie details
        int movieId = getIntent().getIntExtra("movie_id", 0);

        // Call TMDb API to get movie details
        TMDbApiInterface apiInterface = TMDbAPIClient.getClient();
        Call<Movie> movieDetailsCall = apiInterface.getMovieDetails(movieId, TMDbAPIClient.getApiKey());

        movieDetailsCall.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                // Handle the response and update the RecyclerView adapter
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                // Handle failure
            }
        });
    }
}
