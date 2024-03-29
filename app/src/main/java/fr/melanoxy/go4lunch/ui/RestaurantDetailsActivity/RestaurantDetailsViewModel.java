package fr.melanoxy.go4lunch.ui.RestaurantDetailsActivity;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.DetailsResult;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.PlaceIdDetailsResponse;
import fr.melanoxy.go4lunch.data.repositories.RestaurantRepository;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;

public class RestaurantDetailsViewModel extends ViewModel {

    @NonNull
    private final UserRepository userRepository;
    @NonNull
    private final RestaurantRepository restaurantRepository;
    //DetailsResult restaurantDetails;
    private final MediatorLiveData<RestaurantDetailsStateItem> restaurantDetailsMediatorLiveData = new MediatorLiveData<>();

    public RestaurantDetailsViewModel(
            @NonNull UserRepository userRepository,
            @NonNull RestaurantRepository restaurantRepository
    ) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        LiveData<PlaceIdDetailsResponse> placeIdDetailsResponseLiveData = restaurantRepository.getPlaceIdDetailsResponseLiveData();

        restaurantDetailsMediatorLiveData.addSource(placeIdDetailsResponseLiveData, this::combine);
    }

    private void combine(@Nullable final PlaceIdDetailsResponse placeIdDetailsResponse) {

        String openingHours = "";
        String formattedPhoneNumber="";
        String website = "";

        if(placeIdDetailsResponse!=null){
            DetailsResult restaurantDetails = placeIdDetailsResponse.getResult();
            if(restaurantDetails.getOpeningHours()!=null){
                openingHours = TextUtils.join("\n",restaurantDetails.getOpeningHours().getWeekdayText());
            }
            if(restaurantDetails.getFormattedPhoneNumber()!=null){
                formattedPhoneNumber=restaurantDetails.getFormattedPhoneNumber();
            }
            if(restaurantDetails.getWebsite()!=null){
                website=restaurantDetails.getWebsite();
            }
        }

        RestaurantDetailsStateItem details = new RestaurantDetailsStateItem(
                openingHours,
                formattedPhoneNumber,
                website
        );

        restaurantDetailsMediatorLiveData.setValue(details);
    }

    public void searchPlaceIdDetails(String place_id, String fields, String apiKey) {
        restaurantRepository.searchPlaceIdDetails(place_id, fields, apiKey);
    }

    public LiveData<RestaurantDetailsStateItem> getRestaurantDetailsResults() {
        return restaurantDetailsMediatorLiveData;
    }

    public void onRestaurantForTodayClicked(String place_id, String place_name, String place_address, String place_pic_url) {
        userRepository.updateTodayRestaurantUser(place_id, place_name, place_address, place_pic_url);
    }

    public LiveData<User> getUserLiveData() {
        return userRepository.getConnectedUserLiveData();
    }

    public void onFavClicked(String place_id) {
        userRepository.updateFavList(place_id);
    }

    public LiveData<List<LunchmateStateItem>> getLunchmateStateItemsLiveData(String place_id) {
        return Transformations.map(userRepository.getLunchmatesLiveData(place_id), lunchmates -> {
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
