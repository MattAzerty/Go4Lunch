package fr.melanoxy.go4lunch.ui.ListView;

import static fr.melanoxy.go4lunch.BuildConfig.MAPS_API_KEY;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import fr.melanoxy.go4lunch.R;

import android.location.Location;

import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search.RestaurantsNearbyResponse;
import fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search.NearbyResult;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.DetailsResult;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.PlaceIdDetailsResponse;
import fr.melanoxy.go4lunch.data.repositories.LocationRepository;
import fr.melanoxy.go4lunch.data.repositories.RestaurantRepository;
import fr.melanoxy.go4lunch.data.repositories.SearchRepository;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;
import fr.melanoxy.go4lunch.utils.WorkmatesUtils;

public class ListViewViewModel extends ViewModel {

    @NonNull
    private final RestaurantRepository restaurantRepository;

    private String mPreviousQuery = null;

    @NonNull
    private final MediatorLiveData<List<RestaurantStateItem>> restaurantsMediatorLiveData = new MediatorLiveData<>();

    //CONSTRUCTOR
    public ListViewViewModel(
            @NonNull UserRepository userRepository,
            @NonNull LocationRepository locationRepository,
            @NonNull SearchRepository searchRepository,
            @NonNull RestaurantRepository restaurantRepository
    ) {
        this.restaurantRepository = restaurantRepository;

        LiveData<Location> userLocationLiveData = locationRepository.getLocationLiveData();
        LiveData<RestaurantsNearbyResponse> restaurantsLiveData = restaurantRepository.getRestaurantNearbyResponseLiveData();
        LiveData<String> queryLiveData = searchRepository.getSearchFieldLiveData();
        LiveData<Integer> sizeAutocompleteLiveData = restaurantRepository.getRestaurantsSizeByQueryLiveData();
        LiveData<List<PlaceIdDetailsResponse>> predictionsDetailsLiveData = restaurantRepository.getPredictionsDetailsLiveData();
        LiveData<List<User>> workmatesLiveData = userRepository.getWorkmates();

        //RestaurantStateItems creation (NearbyResults+UserLocation+QueryInput+AutocompleteResponseSize+predictionsDetails+lunchmates)
        restaurantsMediatorLiveData.addSource(restaurantsLiveData, restaurantsNearbyResponse ->
                combine(restaurantsNearbyResponse, userLocationLiveData.getValue(),
                        queryLiveData.getValue(), sizeAutocompleteLiveData.getValue(),
                        predictionsDetailsLiveData.getValue(), workmatesLiveData.getValue()));

        restaurantsMediatorLiveData.addSource(userLocationLiveData, userLocation ->
                combine(restaurantsLiveData.getValue(), userLocation,
                        queryLiveData.getValue(), sizeAutocompleteLiveData.getValue(),
                        predictionsDetailsLiveData.getValue(), workmatesLiveData.getValue()));

        restaurantsMediatorLiveData.addSource(queryLiveData, query ->
                combine(restaurantsLiveData.getValue(), userLocationLiveData.getValue(),
                        query, sizeAutocompleteLiveData.getValue(),
                        predictionsDetailsLiveData.getValue(), workmatesLiveData.getValue()));

        restaurantsMediatorLiveData.addSource(sizeAutocompleteLiveData, size ->
                combine(restaurantsLiveData.getValue(), userLocationLiveData.getValue(),
                        queryLiveData.getValue(), size,
                        predictionsDetailsLiveData.getValue(), workmatesLiveData.getValue()));

        restaurantsMediatorLiveData.addSource(predictionsDetailsLiveData, predictionsDetails ->
                combine(restaurantsLiveData.getValue(), userLocationLiveData.getValue(),
                        queryLiveData.getValue(), sizeAutocompleteLiveData.getValue(),
                        predictionsDetails, workmatesLiveData.getValue()));

        restaurantsMediatorLiveData.addSource(workmatesLiveData, workmates ->
                combine(restaurantsLiveData.getValue(), userLocationLiveData.getValue(),
                        queryLiveData.getValue(), sizeAutocompleteLiveData.getValue(),
                        predictionsDetailsLiveData.getValue(), workmates));
    }

    private void combine(
            @Nullable RestaurantsNearbyResponse restaurantsNearbyResponse,
            @Nullable Location userLocation,
            @Nullable String query,
            @Nullable Integer size,
            @Nullable List<PlaceIdDetailsResponse> predictionsDetails,
            List<User> workmates) {

        List<RestaurantStateItem> restaurantStateItems = new ArrayList<>();
//Case with NearbySearch
        if (restaurantsNearbyResponse != null && query == null && userLocation != null && workmates != null) {
            //getNearbyResult
            List<NearbyResult> restaurantsNearby = restaurantsNearbyResponse.getResults();
            // map on a ViewStateItem for rv
            for (NearbyResult result : restaurantsNearby) {
                restaurantStateItems.add(mapRestaurant(result, userLocation, workmates));
            }
//case autocomplete
        } else if (query != null && userLocation != null && !Objects.equals(query, mPreviousQuery)) {

            mPreviousQuery = query;
            //Ask for predictions
            String coordinate = userLocation.getLatitude() + "," + userLocation.getLongitude();
            restaurantRepository.searchFromQueryPlaces(query, "restaurant", coordinate, "500", MAPS_API_KEY);
            restaurantStateItems = new ArrayList<>();

        } else if (query != null && predictionsDetails != null && size != null && predictionsDetails.size() <= size) {

            for (PlaceIdDetailsResponse placeIdDetailsResponse : predictionsDetails) {
                restaurantStateItems.add(mapPrediction(placeIdDetailsResponse.getResult(), userLocation, workmates));
            }
        }

        restaurantsMediatorLiveData.setValue(restaurantStateItems);

    }

    private RestaurantStateItem mapPrediction(
            DetailsResult result,
            Location userLocation,
            List<User> workmates) {

        return new RestaurantStateItem(
                result.getPlaceId(),
                result.getName(),
                result.getFormattedAddress().trim(),
                distance(userLocation, result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng()),
                isOpen(result.getOpeningHours()!=null? result.getOpeningHours().getOpenNow():false),
                result.getRating()!=null? (3 * result.getRating() / 5):0,
                (result.getPhotos() != null) ?
                        restaurantRepository.getUrlPicture(result.getPhotos().get(0).getPhotoReference()) :
                        "https://upload.wikimedia.org/wikipedia/commons/2/23/Light_green.PNG",
                WorkmatesUtils.getInstance().getNumberOfLunchmates(workmates, result.getPlaceId()));
    }


    @NonNull
    private RestaurantStateItem mapRestaurant(
            @NonNull NearbyResult result,
            @NonNull Location userLocation,
            @NonNull List<User> workmates) {

        return new RestaurantStateItem(
                result.getPlaceId(),
                result.getName(),
                result.getFormattedAddress().trim(),
                distance(userLocation, result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng()),
                (result.getOpeningHours() != null) ? isOpen(result.getOpeningHours().getOpenNow()) :
                        R.string.hr_unknown,
                3 * result.getRating() / 5,
                (result.getPhotos() != null) ?
                        restaurantRepository.getUrlPicture(result.getPhotos().get(0).getPhotoReference()) :
                        "https://upload.wikimedia.org/wikipedia/commons/2/23/Light_green.PNG",
                WorkmatesUtils.getInstance().getNumberOfLunchmates(workmates, result.getPlaceId()));
    }

    //IS OPEN
    private int isOpen(Boolean openNow) {
        //IsOpen String message handling
        @StringRes int isOpen;
        if (openNow) {
            isOpen = R.string.hr_open;
        } else {
            isOpen = R.string.hr_close;
        }

        return isOpen;
    }

    //DISTANCE
    private String distance(Location userLocation, Double lat, Double lng) {
        //Distance
        Location restaurantLocation = new Location("restaurant_provider");
        restaurantLocation.setLatitude(lat);
        restaurantLocation.setLongitude(lng);

        return String.format(Locale.getDefault(), "%.02f", (userLocation.distanceTo(restaurantLocation)) / 1000);

    }

    // Getter in LiveData
    public LiveData<List<RestaurantStateItem>> getViewStateLiveData() {
        return restaurantsMediatorLiveData;
    }

}//END of ListViewModel