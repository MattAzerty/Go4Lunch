package fr.melanoxy.go4lunch.data;

//Retrofit2 will use annotations when communicating with the real HTTP API.

import fr.melanoxy.go4lunch.data.models.RestaurantsNearbyResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestaurantSearchService {

    @GET("maps/api/place/textsearch/json")
    Call<RestaurantsNearbyResponse> searchNearbyRestaurants(
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("type") String type,
            @Query("key") String apiKey
    );
}

