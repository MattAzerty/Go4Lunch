package fr.melanoxy.go4lunch.ui.RestaurantDetailsActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.PlaceIdDetailsResponse;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.Result;
import fr.melanoxy.go4lunch.data.repositories.RestaurantRepository;

public class RestaurantDetailsViewModel extends ViewModel {

    @NonNull
    private final RestaurantRepository restaurantRepository;
    private LiveData<PlaceIdDetailsResponse> placeIdDetailsResponseLiveData;
    Result restaurantDetails;
    private final MediatorLiveData<Result> restaurantDetailsMediatorLiveData = new MediatorLiveData<>();

    public RestaurantDetailsViewModel(
            @NonNull RestaurantRepository restaurantRepository
    ){
        this.restaurantRepository = restaurantRepository;
        placeIdDetailsResponseLiveData = restaurantRepository.getPlaceIdDetailsResponseLiveData();

        restaurantDetailsMediatorLiveData.addSource(placeIdDetailsResponseLiveData, placeIdDetailsResponse ->
                combine(placeIdDetailsResponse));
    }

    private void combine(@Nullable final PlaceIdDetailsResponse placeIdDetailsResponse) {
        restaurantDetails = placeIdDetailsResponse.getResult();
        restaurantDetailsMediatorLiveData.setValue(restaurantDetails);
    }

    public void searchPlaceIdDetails(String place_id, String fields, String apiKey) {
        restaurantRepository.searchPlaceIdDetails(place_id,fields,apiKey);
    }

    public LiveData<Result> getRestaurantDetailsResults() {
        return restaurantDetailsMediatorLiveData;

    }


}//END of RestaurantDetailsViewModel
