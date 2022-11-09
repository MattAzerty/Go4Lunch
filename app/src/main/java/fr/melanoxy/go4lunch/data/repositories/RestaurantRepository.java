package fr.melanoxy.go4lunch.data.repositories;

//This repository will be used by the ViewModel to interact with the Google Places API (web services)

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import fr.melanoxy.go4lunch.data.RestaurantSearchService;
import fr.melanoxy.go4lunch.data.models.RestaurantsNearbyResponse;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestaurantRepository {
    private static final String PLACES_SEARCH_SERVICE_BASE_URL = "https://maps.googleapis.com/";

    private RestaurantSearchService restaurantSearchService;
    private MutableLiveData<RestaurantsNearbyResponse> restaurantNearbyMutableLiveData;

    public RestaurantRepository() {
        restaurantNearbyMutableLiveData = new MutableLiveData<>();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        restaurantSearchService = new retrofit2.Retrofit.Builder()
                .baseUrl(PLACES_SEARCH_SERVICE_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RestaurantSearchService.class);

    }

    public void searchNearbyRestaurants(String location, String radius, String type, String apiKey) {
        restaurantSearchService.searchNearbyRestaurants(location, radius, type, apiKey)
                .enqueue(new Callback<RestaurantsNearbyResponse>() {
                    @Override
                    public void onResponse(Call<RestaurantsNearbyResponse> call, Response<RestaurantsNearbyResponse> response) {
                        if (response.body() != null) {
                            restaurantNearbyMutableLiveData.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<RestaurantsNearbyResponse> call, Throwable t) {
                        restaurantNearbyMutableLiveData.postValue(null);
                    }
                });
    }

    public LiveData<RestaurantsNearbyResponse> getRestaurantNearbyResponseLiveData() {
        return restaurantNearbyMutableLiveData;
    }

}
