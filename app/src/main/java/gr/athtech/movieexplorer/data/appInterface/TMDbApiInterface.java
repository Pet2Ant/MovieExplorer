package gr.athtech.movieexplorer.data.appInterface;

import gr.athtech.movieexplorer.data.models.MovieDetails;
import gr.athtech.movieexplorer.data.models.MovieResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface TMDbApiInterface {
    @Headers("Content-Type: application/json")
    //get movies
    @GET("movies/")
    Call<MovieResponse> getMovies();


    // get movie details
    @GET("movies/{movie_id}")
    Call<MovieDetails> getMovieDetails(
            @Path("movie_id") int movieId
    );
}