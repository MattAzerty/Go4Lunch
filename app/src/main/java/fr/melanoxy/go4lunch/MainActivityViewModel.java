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

import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.repositories.LocationRepository;
import fr.melanoxy.go4lunch.data.repositories.RestaurantRepository;
import fr.melanoxy.go4lunch.data.repositories.SearchRepository;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;
import fr.melanoxy.go4lunch.ui.ListView.RestaurantStateItem;
import fr.melanoxy.go4lunch.ui.MapView.PermissionChecker;
import fr.melanoxy.go4lunch.ui.RestaurantDetailsActivity.SingleLiveEvent;

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

    private final MutableLiveData<Boolean> isNotifyPermissionGrantedLiveData = new MutableLiveData<>();
    private final MediatorLiveData<User> notifyStateMediatorLiveData = new MediatorLiveData<>();

    //restaurant details activity SingleLiveEvent
    // Check https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150
    private final SingleLiveEvent<RestaurantStateItem> restaurantDetailsActivitySingleLiveEvent = new SingleLiveEvent<>();

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

//GPS LOCATION
        //point nemo coordinates
        previousLocation.setLatitude(-48.876667);
        previousLocation.setLongitude(-123.393333);

        LiveData<Location> locationLiveData = locationRepository.getLocationLiveData();

        gpsMessageLiveData.addSource(locationLiveData, location ->
                combineLocation(location, isGpsPermissionGrantedLiveData.getValue())
        );
        gpsMessageLiveData.addSource(isGpsPermissionGrantedLiveData, hasGpsPermission ->
                combineLocation(locationLiveData.getValue(), hasGpsPermission)
        );

        LiveData<User> userLiveData = userRepository.getConnectedUserLiveData();

//NOTIFICATION
        notifyStateMediatorLiveData.addSource(isNotifyPermissionGrantedLiveData, hasNotifyPermission ->
                combineNotify(hasNotifyPermission,userLiveData.getValue())
        );

        notifyStateMediatorLiveData.addSource(userLiveData, user ->
                combineNotify(isNotifyPermissionGrantedLiveData.getValue(),user)
        );

        //TODO settings input
}

    private void combineLocation(@Nullable Location location, @Nullable Boolean hasGpsPermission) {
        if (location == null) {
            if (hasGpsPermission == null || !hasGpsPermission) {
                // Never hardcode translatable Strings, always use Context.getString(R.string.my_string) instead !
                gpsMessageLiveData.setValue("I am lost... Should I click the permission button ?!");
            }
        }
    }

    private void combineNotify(@Nullable Boolean hasNotifyPermission,@Nullable User user) {
        if(hasNotifyPermission && user!=null){
        notifyStateMediatorLiveData.setValue(user);}
    }

    @SuppressLint("MissingPermission")
    public void refresh() {
//NOTIFICATION
        boolean hasNotifyPermission = true;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            hasNotifyPermission = permissionChecker.hasNotificationPermission();
        }
        isNotifyPermissionGrantedLiveData.setValue(hasNotifyPermission);
//GPS
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

    public LiveData<Boolean> getIsNotifyPermissionGrantedLiveData() {
        return isNotifyPermissionGrantedLiveData;
    }

    public LiveData<String> getGpsMessageLiveData() {
        return gpsMessageLiveData;
    }

    public LiveData<User> getNotifyStateLiveData() {
        return notifyStateMediatorLiveData;
    }

//Ask repo to check on firebase if the user instance exist
    public Boolean isUserAuthenticated() {
        return userRepository.isUserAuthenticatedInFirebase();
    }
//On User authentication success create him on firestore base
    public void onUserLoggedSuccess() {
        userRepository.getWorkmates();
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
        if (location.distanceTo(previousLocation) > 500 || restaurantRepository.getRestaurantNearbyResponseLiveData() == null) {
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

    public LiveData<RestaurantStateItem> getRestaurantDetailsActivitySingleLiveEvent() {
        return restaurantDetailsActivitySingleLiveEvent;
    }

    public void onYourLunchClicked() {

        User user = userRepository.mUser;

        RestaurantStateItem rItem = new RestaurantStateItem(
                user.getRestaurant_for_today_id(),
                user.getRestaurant_for_today_name(),
                user.getRestaurant_for_today_address(),
                "",
                R.string.error_unknown_error,
                1,
                user.getRestaurant_for_today_pic_url()
        );

        restaurantDetailsActivitySingleLiveEvent.setValue(rItem);
    }
}//END
