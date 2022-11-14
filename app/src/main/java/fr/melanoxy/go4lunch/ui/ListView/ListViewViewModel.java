package fr.melanoxy.go4lunch.ui.ListView;

import static fr.melanoxy.go4lunch.BuildConfig.MAPS_API_KEY;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search.RestaurantsNearbyResponse;
import fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search.Result;
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
            @NonNull RestaurantRepository restaurantRepository
    ) {
        this.locationRepository = locationRepository;
        this.restaurantRepository = restaurantRepository;

        restaurantsResponseLiveData = restaurantRepository.getRestaurantNearbyResponseLiveData();
        final LiveData<RestaurantsNearbyResponse> restaurantsLiveData = restaurantRepository.getRestaurantNearbyResponseLiveData();

        restaurantsMediatorLiveData.addSource(restaurantsLiveData, restaurantsNearbyResponse ->
                combine(restaurantsNearbyResponse));
    }


        private void combine(@Nullable final RestaurantsNearbyResponse restaurantsNearbyResponse) {

           List<Result> restaurantsNearby = restaurantsNearbyResponse.getResults();

            // map on a ViewStateItem
            List<RestaurantStateItem> restaurantStateItem = new ArrayList<>();
            for (Result result : restaurantsNearby) {
                restaurantStateItem.add(mapRestaurant(result));
            }
            restaurantsMediatorLiveData.setValue(restaurantStateItem);
        }

    @NonNull
    private RestaurantStateItem mapRestaurant(@NonNull Result result) {

        String urlPreview = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+ result.getPhotos().get(0).getPhotoReference() + "&key=" + MAPS_API_KEY;

        return new RestaurantStateItem(
                result.getPlaceId(),
                result.getName(),
                result.getFormattedAddress().trim(),
                result.getBusinessStatus(),
                urlPreview
        );
    }

    // Getter in LiveData
    public LiveData<List<RestaurantStateItem>> getViewStateLiveData() {
        return restaurantsMediatorLiveData;
    }




}//END of ListViewModel