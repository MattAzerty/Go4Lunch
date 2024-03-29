package fr.melanoxy.go4lunch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.Objects;

import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search.RestaurantsNearbyResponse;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.PlaceIdDetailsResponse;
import fr.melanoxy.go4lunch.data.repositories.LocationRepository;
import fr.melanoxy.go4lunch.data.repositories.RestaurantRepository;
import fr.melanoxy.go4lunch.data.repositories.SearchRepository;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;
import fr.melanoxy.go4lunch.ui.ListView.RestaurantStateItem;
import fr.melanoxy.go4lunch.data.PermissionChecker;
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

    private final MutableLiveData<Boolean> isNotifyPermissionGrantedLiveData = new MutableLiveData<>();
    private final MediatorLiveData<User> notifyStateMediatorLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<Boolean> progressBarMediatorLiveData = new MediatorLiveData<>();
    //private final MediatorLiveData<String> gpsMessageLiveData = new MediatorLiveData<>();

    //restaurant details activity SingleLiveEvent
    // Check https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150
    private final SingleLiveEvent<RestaurantStateItem> restaurantDetailsActivitySingleLiveEvent = new SingleLiveEvent<>();
    //snackBar SingleLiveEvent
    private final SingleLiveEvent<Integer> snackBarSingleLiveEvent = new SingleLiveEvent<>();

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



//NOTIFICATION
        LiveData<User> userLiveData = userRepository.getConnectedUserLiveData();

        notifyStateMediatorLiveData.addSource(isNotifyPermissionGrantedLiveData, hasNotifyPermission ->
                combineNotify(hasNotifyPermission, userLiveData.getValue())
        );

        notifyStateMediatorLiveData.addSource(userLiveData, user ->
                combineNotify(isNotifyPermissionGrantedLiveData.getValue(), user)
        );

//PROGRESS BAR (userLocation + NearbyAnswer + queryInput + predictionsDetails + restaurantRepoError)
        LiveData<Location> locationLiveData = locationRepository.getLocationLiveData();
        LiveData<RestaurantsNearbyResponse> restaurantsNearbyLiveData = restaurantRepository.getRestaurantNearbyResponseLiveData();
        LiveData<String> queryLiveData = searchRepository.getSearchFieldLiveData();
        LiveData<List<PlaceIdDetailsResponse>> predictionsDetailsLiveData = restaurantRepository.getPredictionsDetailsLiveData();
        LiveData<String> restaurantRepositoryErrorLiveData = restaurantRepository.getRestaurantRepositoryErrorLiveData();

        progressBarMediatorLiveData.addSource(locationLiveData, userLocation ->
                combineProgressBar(userLocation, isGpsPermissionGrantedLiveData.getValue(), restaurantsNearbyLiveData.getValue(),
                        queryLiveData.getValue(), predictionsDetailsLiveData.getValue(), restaurantRepositoryErrorLiveData.getValue())
        );

        progressBarMediatorLiveData.addSource(isGpsPermissionGrantedLiveData, hasGpsPermission ->
                combineProgressBar(locationLiveData.getValue(), hasGpsPermission, restaurantsNearbyLiveData.getValue(),
                        queryLiveData.getValue(), predictionsDetailsLiveData.getValue(), restaurantRepositoryErrorLiveData.getValue())
        );

        progressBarMediatorLiveData.addSource(restaurantsNearbyLiveData, restaurantsNearbyResponse ->
                combineProgressBar(locationLiveData.getValue(), isGpsPermissionGrantedLiveData.getValue(), restaurantsNearbyResponse,
                        queryLiveData.getValue(), predictionsDetailsLiveData.getValue(), restaurantRepositoryErrorLiveData.getValue())
        );

        progressBarMediatorLiveData.addSource(queryLiveData, query ->
                combineProgressBar(locationLiveData.getValue(), isGpsPermissionGrantedLiveData.getValue(), restaurantsNearbyLiveData.getValue(),
                        query, predictionsDetailsLiveData.getValue(), restaurantRepositoryErrorLiveData.getValue())
        );

        progressBarMediatorLiveData.addSource(predictionsDetailsLiveData, placeIdDetailsResponseList ->
                combineProgressBar(locationLiveData.getValue(), isGpsPermissionGrantedLiveData.getValue(), restaurantsNearbyLiveData.getValue(),
                        queryLiveData.getValue(), placeIdDetailsResponseList, restaurantRepositoryErrorLiveData.getValue())
        );

        progressBarMediatorLiveData.addSource(restaurantRepositoryErrorLiveData, error ->
                combineProgressBar(locationLiveData.getValue(), isGpsPermissionGrantedLiveData.getValue(), restaurantsNearbyLiveData.getValue(),
                        queryLiveData.getValue(), predictionsDetailsLiveData.getValue(), error)
        );
    }

    private void combineProgressBar(
            Location userLocation, Boolean hasGpsPermission,
            RestaurantsNearbyResponse restaurantsNearbyResponse,
            String query,
            List<PlaceIdDetailsResponse> placeIdDetailsResponseList,
            String error
    ) {
        boolean state = (hasGpsPermission != null && hasGpsPermission && restaurantsNearbyResponse == null) || (hasGpsPermission != null && hasGpsPermission && query != null && placeIdDetailsResponseList == null);

        progressBarMediatorLiveData.setValue(state);

        if (Objects.equals(error, "internetError")) {
            snackBarSingleLiveEvent.setValue(R.string.error_no_internet);
        }

        if (userLocation == null) {
            if (Boolean.FALSE.equals(hasGpsPermission)) {
                snackBarSingleLiveEvent.setValue(R.string.error_gps);
            }
        }
    }

    private void combineNotify(@Nullable Boolean hasNotifyPermission, @Nullable User user) {
        if (Boolean.TRUE.equals(hasNotifyPermission) && user != null) {
            notifyStateMediatorLiveData.setValue(user);
        }
    }

    @SuppressLint("MissingPermission")
    public void refresh() {
//NOTIFICATION
        isNotifyPermissionGrantedLiveData.setValue(permissionChecker.hasNotificationPermission());
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

    public LiveData<User> getNotifyStateLiveData() {
        return notifyStateMediatorLiveData;
    }

    public LiveData<Boolean> getProgressBarStateLiveData() {
        return progressBarMediatorLiveData;
    }

    public String getQuery() {
        return searchRepository.getQuery();
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

    public Task<Void> onSignOut(Context context) {
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

    public LiveData<Integer> getSnackBarSingleLiveEvent() {
        return snackBarSingleLiveEvent;
    }

    public void onYourLunchClicked() {

        User user = userRepository.getUser();

        if (user.getRestaurant_for_today_id() != null) {//If user has selected a restaurant launch the activity
            RestaurantStateItem rItem = new RestaurantStateItem(
                    user.getRestaurant_for_today_id(),
                    user.getRestaurant_for_today_name(),
                    user.getRestaurant_for_today_address(),
                    "",
                    R.string.error_unknown_error,
                    1,
                    user.getRestaurant_for_today_pic_url(),
                    0);

            restaurantDetailsActivitySingleLiveEvent.setValue(rItem);
        } else {//Else send a snackBar message:
            snackBarSingleLiveEvent.setValue(R.string.my_restaurant);
        }
    }

    public void OnSettingsSaved(Boolean notified, Uri imageUri, String username) {

        if (imageUri != null) {
            userRepository.uploadImage(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl()
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    snackBarSingleLiveEvent.setValue(R.string.error_no_internet);
                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    userRepository.updateUserSettings(notified, uri.toString(), username);
                                }
                            });
                }
            });
        } else {
            userRepository.updateUserSettings(
                    notified,
                    userRepository.getConnectedUserLiveData().getValue().getUrlPicture(), username
            );
        }
    }

}//END of MainActivityViewModel
