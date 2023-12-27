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

    //    get popular movies
//    @GET("movie/popular")
//    Call<MovieResponse> getPopularMovies(
//            @Query("api_key") String apiKey,
//            @Query("page") int page
//    );

//    get Top rated movies

//    @GET("movie/top_rated")
//    Call<MovieResponse> getTopRatedMovies(
//            @Query("api_key") String apiKey,
//            @Query("page") int page
//    );

    //    get cast
//    @GET("movie/{movie_id}/credits")
//    Call<CastResponse> getCastDetail(
//            @Path("movie_id") int movie_id,
//            @Query("api_key") String api_key
//    );
}