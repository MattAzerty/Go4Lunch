package fr.melanoxy.go4lunch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;

import java.util.Date;

import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.repositories.LocationRepository;
import fr.melanoxy.go4lunch.data.repositories.RestaurantRepository;
import fr.melanoxy.go4lunch.data.repositories.SearchRepository;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;
import fr.melanoxy.go4lunch.ui.MapView.PermissionChecker;

public class MainActivityViewModel extends ViewModel {
//INIT
    //Injected with the ViewModelFactory
    @NonNull
    private final UserRepository userRepository;
    @NonNull
    private final PermissionChecker permissionChecker;
    @NonNull
    private final LocationRepository locationRepository;
    @NonNull
    private final SearchRepository searchRepository;
    @NonNull
    private final RestaurantRepository restaurantRepository;

    private Location previousLocation = new Location("point_nemo_provider");
    private final MutableLiveData<Boolean> isGpsPermissionGrantedLiveData = new MutableLiveData<>();
    private final MediatorLiveData<String> gpsMessageLiveData = new MediatorLiveData<>();

//CONSTRUCTOR
    public MainActivityViewModel(
        @NonNull UserRepository userRepository,
        @NonNull PermissionChecker permissionChecker,
        @NonNull LocationRepository locationRepository,
        @NonNull SearchRepository searchRepository,
        @NonNull RestaurantRepository restaurantRepository
    ) {
        this.userRepository = userRepository;
        this.permissionChecker = permissionChecker;
        this.locationRepository = locationRepository;
        this.searchRepository = searchRepository;
        this.restaurantRepository = restaurantRepository;

        //point nemo coordinate
        previousLocation.setLatitude(-48.876667);
        previousLocation.setLongitude(-123.393333);

        LiveData<Location> locationLiveData = locationRepository.getLocationLiveData();

        gpsMessageLiveData.addSource(locationLiveData, location ->
                combine(location, isGpsPermissionGrantedLiveData.getValue())
        );

        gpsMessageLiveData.addSource(isGpsPermissionGrantedLiveData, hasGpsPermission ->
                combine(locationLiveData.getValue(), hasGpsPermission)
        );
}

    private void combine(@Nullable Location location, @Nullable Boolean hasGpsPermission) {
        if (location == null) {
            if (hasGpsPermission == null || !hasGpsPermission) {
                // Never hardcode translatable Strings, always use Context.getString(R.string.my_string) instead !
                gpsMessageLiveData.setValue("I am lost... Should I click the permission button ?!");
            } else {
                gpsMessageLiveData.setValue("Querying location, please wait for a few seconds...");
            }
        } else {
            gpsMessageLiveData.setValue("I am at coordinates (lat:" + location.getLatitude() + ", long:" + location.getLongitude() + ")");
        }
    }

    @SuppressLint("MissingPermission")
    public void refresh() {
        boolean hasGpsPermission = permissionChecker.hasLocationPermission();
        isGpsPermissionGrantedLiveData.setValue(hasGpsPermission);

        if (hasGpsPermission) {
            locationRepository.startLocationRequest();
        } else {
            locationRepository.stopLocationRequest();
        }
    }

    public LiveData<Boolean> getIsGpsPermissionGrantedLiveData() {
        return isGpsPermissionGrantedLiveData;
    }

    public LiveData<String> getGpsMessageLiveData() {
        return gpsMessageLiveData;
    }

//Ask repo to check on firebase if the user instance exist
    public Boolean isUserAuthenticated() {
        return userRepository.isUserAuthenticatedInFirebase();
    }
//On User authentication success create him on firestore base
    public void onUserLoggedSuccess() {
        userRepository.createUser();
    }

    public Task<Void> onSignOut(Context context){
        return userRepository.signOut(context);
    }

    public LiveData<User> getConnectedUserLiveData() {
        return userRepository.getConnectedUserLiveData();
    }

    public void searchNearbyRestaurant(Location location, String radius, String type, String apiKey) {
        //Check if distance with previous location is less than 500 meters.
        previousLocation = previousLocation;
        if (location.distanceTo(previousLocation) > 500) {
            String latitude = String.valueOf(location.getLatitude());
            String longitude = String.valueOf(location.getLongitude());
            restaurantRepository.searchNearbyRestaurants(latitude + "," + longitude, radius, type, apiKey);
            previousLocation = location;
        }
    }

    //GPS location of current user
    public LiveData<Location> getUserLocationLiveData() {
        return locationRepository.getLocationLiveData();
    }

    public void onSearchQueryCall(String query) {
        searchRepository.searchField(query);
    }

}//END
