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

import fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search.RestaurantsNearbyResponse;
import fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search.NearbyResult;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_autocomplete.PlaceAutocompleteResponse;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.DetailsResult;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.PlaceIdDetailsResponse;
import fr.melanoxy.go4lunch.data.repositories.LocationRepository;
import fr.melanoxy.go4lunch.data.repositories.RestaurantRepository;
import fr.melanoxy.go4lunch.data.repositories.SearchRepository;

public class ListViewViewModel extends ViewModel {

    @NonNull
    private final LocationRepository locationRepository;
    @NonNull
    private final SearchRepository searchRepository;
    @NonNull
    private final RestaurantRepository restaurantRepository;
    private String mPreviousQuery=null;

    @NonNull private final MediatorLiveData<List<RestaurantStateItem>> restaurantsMediatorLiveData = new MediatorLiveData<>();

    //CONSTRUCTOR
    public ListViewViewModel(
            @NonNull LocationRepository locationRepository,
            @NonNull SearchRepository searchRepository,
            @NonNull RestaurantRepository restaurantRepository
    ) {

        this.locationRepository = locationRepository;
        this.searchRepository = searchRepository;
        this.restaurantRepository = restaurantRepository;

        LiveData<Location> userLocationLiveData = locationRepository.getLocationLiveData();
        LiveData<RestaurantsNearbyResponse> restaurantsLiveData = restaurantRepository.getRestaurantNearbyResponseLiveData();
        LiveData<String> queryLiveData = searchRepository.getSearchFieldLiveData();
        LiveData<Integer> sizeAutocompleteLiveData = restaurantRepository.getRestaurantsByQueryLiveData();
        LiveData<List<PlaceIdDetailsResponse>> predictionsDetailsLiveData = restaurantRepository.getPredictionsDetailsLiveData();

        //StateItems creation
        restaurantsMediatorLiveData.addSource(restaurantsLiveData, restaurantsNearbyResponse ->
                combine(restaurantsNearbyResponse, userLocationLiveData.getValue(),
                        queryLiveData.getValue(),sizeAutocompleteLiveData.getValue(),
                        predictionsDetailsLiveData.getValue()));

        restaurantsMediatorLiveData.addSource(userLocationLiveData, userLocation ->
                combine(restaurantsLiveData.getValue(), userLocation,
                        queryLiveData.getValue(),sizeAutocompleteLiveData.getValue(),
                        predictionsDetailsLiveData.getValue()));

        restaurantsMediatorLiveData.addSource(queryLiveData, query ->
                combine(restaurantsLiveData.getValue(), userLocationLiveData.getValue(),
                        query,sizeAutocompleteLiveData.getValue(),
                        predictionsDetailsLiveData.getValue()));

        restaurantsMediatorLiveData.addSource(sizeAutocompleteLiveData, size ->
                combine(restaurantsLiveData.getValue(), userLocationLiveData.getValue(),
                        queryLiveData.getValue(),size,
                        predictionsDetailsLiveData.getValue()));

        restaurantsMediatorLiveData.addSource(predictionsDetailsLiveData, predictionsDetails ->
                combine(restaurantsLiveData.getValue(), userLocationLiveData.getValue(),
                        queryLiveData.getValue(),sizeAutocompleteLiveData.getValue(),
                        predictionsDetails));
    }

    private void combine(
            @Nullable RestaurantsNearbyResponse restaurantsNearbyResponse,
            @Nullable Location userLocation,
            @Nullable String query,
            @Nullable Integer size,
            @Nullable List<PlaceIdDetailsResponse> predictionsDetails
    ) {

        List<NearbyResult> restaurantsNearby;

//Case with NearbySearch
        if (restaurantsNearbyResponse != null && query == null) {
            List<RestaurantStateItem> nearbyStateItems = new ArrayList<>();
            //getNearbyResult
            restaurantsNearby = restaurantsNearbyResponse.getResults();
            // map on a ViewStateItem for rv
            for (NearbyResult result : restaurantsNearby) {
                nearbyStateItems.add(mapRestaurant(result, userLocation));
            }
            restaurantsMediatorLiveData.setValue(nearbyStateItems);

        }else if (query != null && !Objects.equals(query, mPreviousQuery)){
                mPreviousQuery=query;
                //Ask for predictions
                String coordinate = userLocation.getLatitude() + "," + userLocation.getLongitude();
                restaurantRepository.searchFromQueryPlaces(query, "restaurant", coordinate, "500", MAPS_API_KEY);
                List<RestaurantStateItem> resetStateItems = new ArrayList<>();
                restaurantsMediatorLiveData.setValue(resetStateItems);
        }else if(query !=null && predictionsDetails!=null && size!=null && predictionsDetails.size()==size) {
            List<RestaurantStateItem> autocompleteStateItems= new ArrayList<>();
            for (PlaceIdDetailsResponse placeIdDetailsResponse : predictionsDetails) {
                autocompleteStateItems.add(mapPrediction(placeIdDetailsResponse.getResult(), userLocation));
            }
            restaurantsMediatorLiveData.setValue(autocompleteStateItems);//send StateItem
        }
        }


    private RestaurantStateItem mapPrediction(
            DetailsResult result,
            Location userLocation
    ) {

        //IsOpen String message handling
        @StringRes int isOpen;
        if (result.getOpeningHours()!=null){

            if (result.getOpeningHours().getOpenNow()){
                isOpen = R.string.hr_open;
            }else{isOpen = R.string.hr_close;
            }

        }else{
            isOpen = R.string.hr_unknown;
        }

        //Web url
        String urlPreview;//TODO move this to restaurantRepo
        if(result.getPhotos()!=null){
            String photoRef = result.getPhotos().get(0).getPhotoReference();
            urlPreview = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+ photoRef + "&key=" + MAPS_API_KEY;
        }else{urlPreview="https://upload.wikimedia.org/wikipedia/commons/2/23/Light_green.PNG";}

        return new RestaurantStateItem(
                result.getPlaceId(),
                result.getName(),
                result.getFormattedAddress().trim(),
                distance(userLocation,result.getGeometry().getLocation().getLat(),result.getGeometry().getLocation().getLng()),
                isOpen,
                3*result.getRating()/5,
                urlPreview
        );


    }

    @NonNull
    private RestaurantStateItem mapRestaurant(@NonNull NearbyResult result,@NonNull Location userLocation) {

//Web url
        String urlPreview;//TODO move this to restaurantRepo
        if(result.getPhotos()!=null){
            String photoRef = result.getPhotos().get(0).getPhotoReference();
            urlPreview = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+ photoRef + "&key=" + MAPS_API_KEY;
        }else{urlPreview="https://upload.wikimedia.org/wikipedia/commons/2/23/Light_green.PNG";}

        //IsOpen String message handling
        @StringRes int isOpen;
        if (result.getOpeningHours()!=null){

            if (result.getOpeningHours().getOpenNow()){
                isOpen = R.string.hr_open;
            }else{isOpen = R.string.hr_close;
            }

        }else{
            isOpen = R.string.hr_unknown;
        }

            return new RestaurantStateItem(
                result.getPlaceId(),
                result.getName(),
                result.getFormattedAddress().trim(),
                distance(userLocation,result.getGeometry().getLocation().getLat(),result.getGeometry().getLocation().getLng()),
                isOpen,
                3*result.getRating()/5,
                urlPreview
        );
    }

    private String distance(Location userLocation, Double lat, Double lng) {

        //Distance
        Location restaurantLocation = new Location("restaurant_provider");
        restaurantLocation.setLatitude(lat);
        restaurantLocation.setLongitude(lng);
        String distance = String.format(Locale.getDefault(),"%.02f",(userLocation.distanceTo(restaurantLocation))/1000);

        return distance;

    }

    // Getter in LiveData
    public LiveData<List<RestaurantStateItem>> getViewStateLiveData() {
        return restaurantsMediatorLiveData;
    }

}//END of ListViewModel