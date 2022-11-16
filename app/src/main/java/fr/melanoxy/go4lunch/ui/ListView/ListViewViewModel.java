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

import fr.melanoxy.go4lunch.R;
import android.location.Location;
import fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search.RestaurantsNearbyResponse;
import fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search.NearbyResult;
import fr.melanoxy.go4lunch.data.repositories.LocationRepository;
import fr.melanoxy.go4lunch.data.repositories.RestaurantRepository;

public class ListViewViewModel extends ViewModel {

    @NonNull
    private final LocationRepository locationRepository;
    @NonNull
    private final RestaurantRepository restaurantRepository;

    private final MediatorLiveData<List<RestaurantStateItem>> restaurantsMediatorLiveData = new MediatorLiveData<>();
    private LiveData<RestaurantsNearbyResponse> restaurantsResponseLiveData;

    //CONSTRUCTOR
    public ListViewViewModel(
            @NonNull LocationRepository locationRepository,
            @NonNull RestaurantRepository restaurantRepository) {
        this.locationRepository = locationRepository;
        this.restaurantRepository = restaurantRepository;

        restaurantsResponseLiveData = restaurantRepository.getRestaurantNearbyResponseLiveData();
        LiveData<Location> userLocationLiveData = locationRepository.getLocationLiveData();
        final LiveData<RestaurantsNearbyResponse> restaurantsLiveData = restaurantRepository.getRestaurantNearbyResponseLiveData();

        restaurantsMediatorLiveData.addSource(restaurantsLiveData, restaurantsNearbyResponse ->
                combine(restaurantsNearbyResponse, userLocationLiveData.getValue()));

        restaurantsMediatorLiveData.addSource(userLocationLiveData, userLocation ->
                combine(restaurantsLiveData.getValue(), userLocation));
    }


        private void combine(
                @Nullable final RestaurantsNearbyResponse restaurantsNearbyResponse,
                @Nullable final Location userLocation
        ) {

            List<RestaurantStateItem> restaurantStateItem = new ArrayList<>();
            List<NearbyResult> restaurantsNearby = null;

            if (restaurantsNearbyResponse != null) {
                restaurantsNearby = restaurantsNearbyResponse.getResults();


            // map on a ViewStateItem
            for (NearbyResult result : restaurantsNearby) {
                restaurantStateItem.add(mapRestaurant(result,userLocation));
            }}
            restaurantsMediatorLiveData.setValue(restaurantStateItem);
        }

    @NonNull
    private RestaurantStateItem mapRestaurant(@NonNull NearbyResult result,@NonNull Location userLocation) {

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
        String urlPreview;
        if(result.getPhotos()!=null){
            String photoRef = result.getPhotos().get(0).getPhotoReference();
            urlPreview = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+ photoRef + "&key=" + MAPS_API_KEY;
        }else{urlPreview="https://upload.wikimedia.org/wikipedia/commons/2/23/Light_green.PNG";}

//Distance
        Location restaurantLocation = new Location("restaurant_provider");
        restaurantLocation.setLatitude(result.getGeometry().getLocation().getLat());
        restaurantLocation.setLongitude(result.getGeometry().getLocation().getLng());
        String distance = String.format(Locale.getDefault(),"%.02f",(userLocation.distanceTo(restaurantLocation))/1000);


            return new RestaurantStateItem(
                result.getPlaceId(),
                result.getName(),
                result.getFormattedAddress().trim(),
                distance,
                isOpen,
                3*result.getRating()/5,
                urlPreview
        );
    }

    // Getter in LiveData
    public LiveData<List<RestaurantStateItem>> getViewStateLiveData() {
        return restaurantsMediatorLiveData;
    }

}//END of ListViewModel