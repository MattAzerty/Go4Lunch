package fr.melanoxy.go4lunch.ui.RestaurantDetailsActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import fr.melanoxy.go4lunch.R;
import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.PlaceIdDetailsResponse;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.DetailsResult;
import fr.melanoxy.go4lunch.data.repositories.RestaurantRepository;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;

public class RestaurantDetailsViewModel extends ViewModel {

    @NonNull
    private final UserRepository userRepository;
    @NonNull
    private final RestaurantRepository restaurantRepository;
    private LiveData<PlaceIdDetailsResponse> placeIdDetailsResponseLiveData;
    DetailsResult restaurantDetails;
    private final MediatorLiveData<DetailsResult> restaurantDetailsMediatorLiveData = new MediatorLiveData<>();

    public RestaurantDetailsViewModel(
            @NonNull UserRepository userRepository,
            @NonNull RestaurantRepository restaurantRepository
    ){
        this.userRepository = userRepository;
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

    public LiveData<DetailsResult> getRestaurantDetailsResults() {
        return restaurantDetailsMediatorLiveData;

    }

    public void onRestaurantForTodayClicked(String place_id, String place_name, String place_address, String place_pic_url) {
        userRepository.updateTodayRestaurantUser(place_id,place_name,place_address,place_pic_url);
    }

    public LiveData<User> getUserLiveData() {
        return userRepository.getConnectedUserLiveData();
    }

    public void onFavClicked(String place_id) {
        userRepository.updateFavList(place_id);
    }

    public LiveData<List<LunchmateStateItem>> getLunchmateStateItemsLiveData(String place_id) {
        return Transformations.map(userRepository.getLunchmates(place_id), lunchmates -> {
            List<LunchmateStateItem> lunchmateStateItems = new ArrayList<>();

            // mapping
            for (User lunchmate : lunchmates) {
                    lunchmateStateItems.add(
                            new LunchmateStateItem(
                                    lunchmate.getUid(),
                                    lunchmate.getUsername(),
                                    lunchmate.getUrlPicture()
                            )
                    );
            }
            return lunchmateStateItems;
        });
    }

    public void onBackPressed() {
        userRepository.onEndOfDetailsActivity();
    }
}//END of RestaurantDetailsViewModel
