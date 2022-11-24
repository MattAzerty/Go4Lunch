package fr.melanoxy.go4lunch.data.repositories;

//This repository will be used by the ViewModel to interact with the Google Places API (web services)

import static fr.melanoxy.go4lunch.BuildConfig.MAPS_API_KEY;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.melanoxy.go4lunch.data.RestaurantSearchService;
import fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search.RestaurantsNearbyResponse;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_autocomplete.PlaceAutocompleteResponse;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_autocomplete.Prediction;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.PlaceIdDetailsResponse;
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
    private MutableLiveData<PlaceIdDetailsResponse> placeIdDetailsResponseMutableLiveData;
    private MutableLiveData<Integer> autocompleteSizeResponseMutableLiveData;

    private MutableLiveData<List<PlaceIdDetailsResponse>> predictionsDetailstMutableLiveData = new MutableLiveData<>();
    private List<PlaceIdDetailsResponse> mListDetails = new ArrayList<>();

    public RestaurantRepository() {
        restaurantNearbyMutableLiveData = new MutableLiveData<>();
        placeIdDetailsResponseMutableLiveData = new MutableLiveData<>();
        autocompleteSizeResponseMutableLiveData= new MutableLiveData<>();
//for easier retrofit debug
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
//Nearby
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
//Details
    public void searchPlaceIdDetails(String place_id, String fields, String apiKey) {
        restaurantSearchService.searchPlaceIdDetails(place_id, fields, Locale.getDefault().getLanguage(), apiKey)
                .enqueue(new Callback<PlaceIdDetailsResponse>() {
                    @Override
                    public void onResponse(Call<PlaceIdDetailsResponse> call, Response<PlaceIdDetailsResponse> response) {
                        if (response.body() != null) {
                            placeIdDetailsResponseMutableLiveData.setValue(response.body());
                            mListDetails.add(response.body());
                            predictionsDetailstMutableLiveData.setValue(mListDetails);
                        }
                    }
                    @Override
                    public void onFailure(Call<PlaceIdDetailsResponse> call, Throwable t) {
                        placeIdDetailsResponseMutableLiveData.setValue(null);
                    }
                });
    }
//Autocomplete
    public void searchFromQueryPlaces(String input, String types, String location, String radius, String apiKey) {
        restaurantSearchService.searchFromQueryPlaces(input, types, location, radius, apiKey)
                .enqueue(new Callback<PlaceAutocompleteResponse>() {
                    @Override
                    public void onResponse(Call<PlaceAutocompleteResponse> call, Response<PlaceAutocompleteResponse> response) {
                        if (response.body() != null) {
                            mListDetails = new ArrayList<>();//erase previous list of details
                            predictionsDetailstMutableLiveData.setValue(mListDetails);
                            autocompleteSizeResponseMutableLiveData.postValue(response.body().getPredictions().size());
                            for (Prediction prediction : response.body().getPredictions()) {
                                searchPlaceIdDetails(prediction.getPlaceId(), "opening_hours,place_id,geometry,name,formatted_address,rating,photo", MAPS_API_KEY);
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<PlaceAutocompleteResponse> call, Throwable t) {
                        autocompleteSizeResponseMutableLiveData.postValue(null);
                        mListDetails = new ArrayList<>();//erase previous list of details
                    }
                });
    }

    public LiveData<RestaurantsNearbyResponse> getRestaurantNearbyResponseLiveData() {
        return restaurantNearbyMutableLiveData;
    }

    public LiveData<PlaceIdDetailsResponse> getPlaceIdDetailsResponseLiveData() {
        return placeIdDetailsResponseMutableLiveData;
    }

    public LiveData<List<PlaceIdDetailsResponse>> getPredictionsDetailsLiveData() {
        return predictionsDetailstMutableLiveData;
    }

    public LiveData<Integer> getRestaurantsByQueryLiveData() {
        return autocompleteSizeResponseMutableLiveData;
    }
}
