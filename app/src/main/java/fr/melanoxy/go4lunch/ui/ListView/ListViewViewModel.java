package fr.melanoxy.go4lunch.ui.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.melanoxy.go4lunch.data.models.RestaurantsNearbyResponse;
import fr.melanoxy.go4lunch.data.models.Result;
import fr.melanoxy.go4lunch.data.repositories.RestaurantRepository;

public class ListViewViewModel extends ViewModel {

    @NonNull
    private final RestaurantRepository restaurantRepository;
    private final MediatorLiveData<List<ListViewStateItem>> restaurantsMediatorLiveData = new MediatorLiveData<>();
    private LiveData<RestaurantsNearbyResponse> restaurantsResponseLiveData;
    //CONSTRUCTOR
    public ListViewViewModel(
            @NonNull RestaurantRepository restaurantRepository
    ) {
        this.restaurantRepository = restaurantRepository;
        restaurantsResponseLiveData = restaurantRepository.getRestaurantNearbyResponseLiveData();
        final LiveData<RestaurantsNearbyResponse> restaurantsLiveData = restaurantRepository.getRestaurantNearbyResponseLiveData();

        restaurantsMediatorLiveData.addSource(restaurantsLiveData, new Observer<RestaurantsNearbyResponse>() {
            @Override
            public void onChanged(RestaurantsNearbyResponse restaurantsNearbyResponse) {
                combine(restaurantsNearbyResponse);
            }
        });
    }


        private void combine(@Nullable final RestaurantsNearbyResponse restaurantsNearbyResponse) {

           List<Result> restaurantsNearby = restaurantsNearbyResponse.getResults();

            // map on a ViewStateItem
            List<ListViewStateItem> listViewStateItem = new ArrayList<>();
            for (Result result : restaurantsNearby) {
                listViewStateItem.add(mapRestaurant(result));
            }


            restaurantsMediatorLiveData.setValue(listViewStateItem);
        }

    @NonNull
    private ListViewStateItem mapRestaurant(@NonNull Result result) {
        return new ListViewStateItem(
                result.getPlaceId(),
                result.getName(),
                result.getFormattedAddress(),
                "open?:"+result.getReference(),
                result.getBusinessStatus()
        );
    }

    // Getter typé en LiveData (et pas MediatorLiveData pour éviter la modification de la valeur de la LiveData dans la View)
    public LiveData<List<ListViewStateItem>> getViewStateLiveData() {
        return restaurantsMediatorLiveData;
    }

    public void searchNearbyRestaurant(String location, String radius, String type, String apiKey) {
        restaurantRepository.searchNearbyRestaurants(location, radius, type, apiKey);
    }

    public LiveData<RestaurantsNearbyResponse> getVolumesResponseLiveData() {
        return restaurantRepository.getRestaurantNearbyResponseLiveData();
    }
}