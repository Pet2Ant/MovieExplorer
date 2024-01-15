package gr.athtech.movieexplorer.data.client;

import androidx.annotation.NonNull;

import java.io.IOException;

import gr.athtech.movieexplorer.data.appInterface.TMDbApiInterface;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TMDbAPIClient {

    private static final String BASE_URL = "https://app-vpigadas.herokuapp.com/api/";
    private static TMDbApiInterface apiInterface;

    public static TMDbApiInterface getClient() {
        if (apiInterface == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @NonNull
                        @Override
                        public Response intercept(@NonNull Chain chain) throws IOException {
                            Request originalRequest = chain.request();

                            Request.Builder builder = originalRequest.newBuilder()
                                    .header("Content-Type", "application/json");

                            Request newRequest = builder.build();
                            return chain.proceed(newRequest);
                        }
                    })
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiInterface = retrofit.create(TMDbApiInterface.class);
        }
        return apiInterface;
    }


}
