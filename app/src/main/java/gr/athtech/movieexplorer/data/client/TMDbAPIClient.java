package gr.athtech.movieexplorer.data.client;

import gr.athtech.movieexplorer.data.appInterface.TMDbApiInterface;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TMDbAPIClient {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1ODIzMzFlMDUyNDJjMzJkMGE5NzZkMGM4ZjczODAxMCIsInN1YiI6IjY1NjYxOTYwM2Q3NDU0MDBlYTI2ZGIxNCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.Fih1Lp7jCklUM2xlYdsE5fgZag2pvher72FoDkyTN0k";
    private static TMDbApiInterface apiInterface;

    public static TMDbApiInterface getClient() {
        if (apiInterface == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiInterface = retrofit.create(TMDbApiInterface.class);
        }
        return apiInterface;
    }

    public static String getApiKey() {
        return API_KEY;
    }
}
