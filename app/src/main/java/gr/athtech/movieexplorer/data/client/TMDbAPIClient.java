package gr.athtech.movieexplorer.data.client;

import gr.athtech.movieexplorer.data.appInterface.TMDbApiInterface;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TMDbAPIClient {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "582331e05242c32d0a976d0c8f738010";
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
