package fr.melanoxy.go4lunch.ui.MapView;

import static fr.melanoxy.go4lunch.BuildConfig.MAPS_API_KEY;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search.NearbyResult;
import fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search.RestaurantsNearbyResponse;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.PlaceIdDetailsResponse;
import fr.melanoxy.go4lunch.data.repositories.LocationRepository;
import fr.melanoxy.go4lunch.data.repositories.RestaurantRepository;
import fr.melanoxy.go4lunch.data.repositories.SearchRepository;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;
import fr.melanoxy.go4lunch.utils.WorkmatesUtils;

public class MapViewViewModel extends ViewModel {

    //INIT
    //Injected with the ViewModelFactory
    @NonNull
    private final UserRepository userRepository;
    @NonNull
    private final LocationRepository locationRepository;
    @NonNull
    private final SearchRepository searchRepository;
    @NonNull
    private final RestaurantRepository restaurantRepository;

    private final MediatorLiveData<List<MarkerInfoStateItem>> markersMediatorLiveData = new MediatorLiveData<>();
    LiveData<List<PlaceIdDetailsResponse>> predictionsDetailsLiveData;
    private String mPreviousQuery = null;

    //CONSTRUCTOR
    public MapViewViewModel(
            @NonNull UserRepository userRepository,
            @NonNull LocationRepository locationRepository,
            @NonNull SearchRepository searchRepository,
            @NonNull RestaurantRepository restaurantRepository
    ) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.searchRepository = searchRepository;
        this.restaurantRepository = restaurantRepository;

        LiveData<Location> userLocationLiveData = locationRepository.getLocationLiveData();
        LiveData<RestaurantsNearbyResponse> restaurantsNearbyLiveData = restaurantRepository.getRestaurantNearbyResponseLiveData();
        LiveData<String> queryLiveData = searchRepository.getSearchFieldLiveData();
        LiveData<List<User>> workmatesLiveData = userRepository.getWorkmates();
        LiveData<List<PlaceIdDetailsResponse>> predictionsDetailsLiveData = restaurantRepository.getPredictionsDetailsLiveData();
        LiveData<Integer> sizeAutocompleteLiveData = restaurantRepository.getRestaurantsSizeByQueryLiveData();

//MEDIATOR (userLocation + lunchmates + NearbyAnswer + queryInput, predictionsAnswer)

        markersMediatorLiveData.addSource(userLocationLiveData, userLocation ->
                combine(userLocation, workmatesLiveData.getValue(),
                        restaurantsNearbyLiveData.getValue(), queryLiveData.getValue(), sizeAutocompleteLiveData.getValue(), predictionsDetailsLiveData.getValue()));

        markersMediatorLiveData.addSource(workmatesLiveData, workmates ->
                combine(userLocationLiveData.getValue(), workmates,
                        restaurantsNearbyLiveData.getValue(), queryLiveData.getValue(), sizeAutocompleteLiveData.getValue(), predictionsDetailsLiveData.getValue()));

        markersMediatorLiveData.addSource(restaurantsNearbyLiveData, restaurantsNearbyResponse ->
                combine(userLocationLiveData.getValue(), workmatesLiveData.getValue(),
                        restaurantsNearbyResponse, queryLiveData.getValue(), sizeAutocompleteLiveData.getValue(), predictionsDetailsLiveData.getValue()));

        markersMediatorLiveData.addSource(queryLiveData, query ->
                combine(userLocationLiveData.getValue(), workmatesLiveData.getValue(),
                        restaurantsNearbyLiveData.getValue(), query, sizeAutocompleteLiveData.getValue(), predictionsDetailsLiveData.getValue()));

        markersMediatorLiveData.addSource(sizeAutocompleteLiveData, size ->
                combine(userLocationLiveData.getValue(), workmatesLiveData.getValue(),
                        restaurantsNearbyLiveData.getValue(), queryLiveData.getValue(), size, predictionsDetailsLiveData.getValue()));

        markersMediatorLiveData.addSource(predictionsDetailsLiveData, predictions ->
                combine(userLocationLiveData.getValue(), workmatesLiveData.getValue(),
                        restaurantsNearbyLiveData.getValue(), queryLiveData.getValue(), sizeAutocompleteLiveData.getValue(), predictions));
    }

    private void combine(
            @Nullable Location userLocation,
            List<User> workmates,
            @Nullable RestaurantsNearbyResponse restaurantsNearbyResponse,
            @Nullable String query,
            Integer size,
            List<PlaceIdDetailsResponse> predictions) {

        List<MarkerInfoStateItem> markerInfoStateItems = new ArrayList<>();

        if (restaurantsNearbyResponse != null && query == null) {
            mPreviousQuery = null;
            for (NearbyResult result : restaurantsNearbyResponse.getResults()) {
                markerInfoStateItems.add(mapNearby(result, workmates));
            }
        }

        if (query != null && userLocation != null && !Objects.equals(query, mPreviousQuery)) {
            markerInfoStateItems = new ArrayList<>();
            mPreviousQuery = query;
            //Ask for predictions
            String coordinate = userLocation.getLatitude() + "," + userLocation.getLongitude();
            restaurantRepository.searchFromQueryPlaces(query, "restaurant", coordinate, "500", MAPS_API_KEY);
        }

        if (query != null && predictions != null && size != null && size == predictions.size()) {

            for (PlaceIdDetailsResponse placeIdDetailsResponse : predictions) {
                markerInfoStateItems.add(mapPredictions(placeIdDetailsResponse, workmates));
            }
        }

        markersMediatorLiveData.setValue(markerInfoStateItems);
    }

    private MarkerInfoStateItem mapNearby(NearbyResult result, List<User> workmates) {

        String placeId = result.getPlaceId();

        return new MarkerInfoStateItem(
                placeId,
                result.getName(),
                result.getFormattedAddress(),
                (result.getPhotos() != null) ?
                        restaurantRepository.getUrlPicture(result.getPhotos().get(0).getPhotoReference()) :
                        "https://upload.wikimedia.org/wikipedia/commons/2/23/Light_green.PNG",
                WorkmatesUtils.getInstance().getNumberOfLunchmates(workmates, placeId),
                result.getGeometry().getLocation().getLat(),
                result.getGeometry().getLocation().getLng()
        );
    }

    private MarkerInfoStateItem mapPredictions(
            PlaceIdDetailsResponse prediction,
            List<User> workmates
    ) {


        String placeId = prediction.getResult().getPlaceId();

        return new MarkerInfoStateItem(
                placeId,
                prediction.getResult().getName(),
                prediction.getResult().getFormattedAddress(),
                (prediction.getResult().getPhotos() != null) ?
                        restaurantRepository.getUrlPicture(prediction.getResult().getPhotos().get(0).getPhotoReference()) :
                        "https://upload.wikimedia.org/wikipedia/commons/2/23/Light_green.PNG",
                WorkmatesUtils.getInstance().getNumberOfLunchmates(workmates, placeId),
                prediction.getResult().getGeometry().getLocation().getLat(),
                prediction.getResult().getGeometry().getLocation().getLng());
    }

    public LiveData<Location> getUserLocationLiveData() {
        return locationRepository.getLocationLiveData();
    }


    public LiveData<List<MarkerInfoStateItem>> getMarkerInfosLiveData() {
        return markersMediatorLiveData;
    }

}//end of MapViewModel
