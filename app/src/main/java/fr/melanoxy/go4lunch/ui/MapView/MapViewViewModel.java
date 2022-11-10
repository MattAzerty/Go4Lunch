package fr.melanoxy.go4lunch.ui.MapView;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import fr.melanoxy.go4lunch.data.models.RestaurantsNearbyResponse;
import fr.melanoxy.go4lunch.data.models.Result;
import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.repositories.LocationRepository;
import fr.melanoxy.go4lunch.data.repositories.RestaurantRepository;
import fr.melanoxy.go4lunch.ui.ListView.ListViewStateItem;

public class MapViewViewModel extends ViewModel {

    //INIT
    //Injected with the ViewModelFactory
    @NonNull
    private final LocationRepository locationRepository;
    @NonNull
    private final RestaurantRepository restaurantRepository;
    private LiveData<RestaurantsNearbyResponse> restaurantsResponseLiveData;
    private final MediatorLiveData<List<Result>> restaurantsResponseMediatorLiveData = new MediatorLiveData<>();
    List<Result> restaurantsNearby;
    //CONSTRUCTOR
    public MapViewViewModel(
            @NonNull LocationRepository locationRepository,
            @NonNull RestaurantRepository restaurantRepository
    ) {
        this.locationRepository = locationRepository;
        this.restaurantRepository = restaurantRepository;

        restaurantsResponseLiveData = restaurantRepository.getRestaurantNearbyResponseLiveData();

        restaurantsResponseMediatorLiveData.addSource(restaurantsResponseLiveData, restaurantsNearbyResponse ->
                combine(restaurantsNearbyResponse));
    }

    private void combine(@Nullable final RestaurantsNearbyResponse restaurantsNearbyResponse) {
        restaurantsNearby = restaurantsNearbyResponse.getResults();
        restaurantsResponseMediatorLiveData.setValue(restaurantsNearby);
    }


    public LiveData<Location> getUserLocationLiveData() {
        return locationRepository.getLocationLiveData();
    }


    public LiveData<List<Result>> getNearbyRestaurantsResults() {
        return restaurantsResponseMediatorLiveData;

    }
}//end of WorkmatesViewModel//END of MainViewModel
