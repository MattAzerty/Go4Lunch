package fr.melanoxy.go4lunch.data;

//Retrofit2 will use annotations when communicating with the real HTTP API.

import fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search.RestaurantsNearbyResponse;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_autocomplete.PlaceAutocompleteResponse;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.PlaceIdDetailsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestaurantSearchService {
//Nearby
    @GET("maps/api/place/textsearch/json")
    Call<RestaurantsNearbyResponse> searchNearbyRestaurants(
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("type") String type,
            @Query("key") String apiKey
    );
//Details
    @GET("maps/api/place/details/json")
    Call<PlaceIdDetailsResponse> searchPlaceIdDetails(
            @Query("place_id") String place_id,
            @Query("fields") String fields,
            @Query("language") String language,
            @Query("key") String apiKey
    );
//Autocomplete
    @GET("maps/api/place/autocomplete/json")
    Call<PlaceAutocompleteResponse> searchFromQueryPlaces(
            @Query("input") String input,
            @Query("types") String types,
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("key") String apiKey
    );
}

