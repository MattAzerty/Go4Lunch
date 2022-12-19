package fr.melanoxy.go4lunch.ui;

import static org.mockito.Mockito.mock;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import fr.melanoxy.go4lunch.MainActivityViewModel;
import fr.melanoxy.go4lunch.ReadJsonAsString;
import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search.RestaurantsNearbyResponse;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.PlaceIdDetailsResponse;
import fr.melanoxy.go4lunch.data.repositories.LocationRepository;
import fr.melanoxy.go4lunch.data.repositories.RestaurantRepository;
import fr.melanoxy.go4lunch.data.repositories.SearchRepository;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;
import fr.melanoxy.go4lunch.ui.MapView.PermissionChecker;

@RunWith(MockitoJUnitRunner.class)
public class MainActivityViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private LocationRepository locationRepository;
    @Mock
    private SearchRepository searchRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PermissionChecker permissionChecker;

    private MainActivityViewModel viewModel;

    private MutableLiveData<Location> locationLiveData;
    private MutableLiveData<Boolean> isGpsPermissionGrantedLiveData;
    private MutableLiveData<User> userLiveData;
    private MutableLiveData<Boolean> isNotifyPermissionGrantedLiveData;
    private RestaurantsNearbyResponse nearbyResponse;
    private MutableLiveData<RestaurantsNearbyResponse> restaurantsNearbyLiveData;
    private MutableLiveData<List<PlaceIdDetailsResponse>> predictionsDetailsLiveData;
    private MutableLiveData<String> queryLiveData;
    private MutableLiveData<String> restaurantRepositoryErrorLiveData;

    @Before
    public void setUp() throws Exception {
        // Given
        // MutableLiveData used by the mediator to build for the recyclerview the list of StateItem
        locationLiveData = new MutableLiveData<>();//from LocationRepository
        isGpsPermissionGrantedLiveData = new MutableLiveData<>();//from PermissionChecker
        userLiveData = new MutableLiveData<>();//from UserRepository
        isNotifyPermissionGrantedLiveData = new MutableLiveData<>();//from PermissionChecker
        restaurantsNearbyLiveData = new MutableLiveData<>();//from RestaurantRepository
        predictionsDetailsLiveData = new MutableLiveData<>();//from RestaurantRepository
        queryLiveData = new MutableLiveData<>();//from SearchRepository
        restaurantRepositoryErrorLiveData = new MutableLiveData<>();//from RestaurantRepository

        //mock for userLocation
        Location userLocation = mock(Location.class);

        //NearbyResponse from a json file (from default emulator coordinates)
        Gson gson = new GsonBuilder().create();
        String nearbyJson = ReadJsonAsString.readFileAsString("src/test/java/fr/melanoxy/go4lunch/resources/nearby.json");
        nearbyResponse = gson.fromJson(nearbyJson, RestaurantsNearbyResponse.class);

        //the livedata value are set here instead of repository
        locationLiveData.setValue(userLocation);
        isGpsPermissionGrantedLiveData.setValue(false);
        userLiveData.setValue(getDefaultUser(0));
        isNotifyPermissionGrantedLiveData.setValue(false);
        restaurantsNearbyLiveData.setValue(nearbyResponse);
        predictionsDetailsLiveData.setValue(null);
        queryLiveData.setValue(null);
        restaurantRepositoryErrorLiveData.setValue(null);

        // Mock repositories for needed methods:
        locationRepository = mock(LocationRepository.class);
        searchRepository = mock(SearchRepository.class);
        restaurantRepository = mock(RestaurantRepository.class);
        userRepository = mock(UserRepository.class);
        permissionChecker = mock(PermissionChecker.class);

        // given behavior for some methods called
        Mockito.doReturn(locationLiveData).when(locationRepository).getLocationLiveData();
        Mockito.doReturn(isGpsPermissionGrantedLiveData).when(permissionChecker).hasLocationPermission();
        Mockito.doReturn(userLiveData).when(userRepository).getConnectedUserLiveData();
        Mockito.doReturn(isNotifyPermissionGrantedLiveData).when(permissionChecker).hasNotificationPermission();
        Mockito.doReturn(queryLiveData).when(searchRepository).getSearchFieldLiveData();
        Mockito.doReturn(restaurantsNearbyLiveData).when(restaurantRepository).getRestaurantNearbyResponseLiveData();
        Mockito.doReturn(queryLiveData).when(searchRepository).getSearchFieldLiveData();
        Mockito.doReturn(predictionsDetailsLiveData).when(restaurantRepository).getPredictionsDetailsLiveData();
        Mockito.doReturn(restaurantRepositoryErrorLiveData).when(restaurantRepository).getRestaurantRepositoryErrorLiveData();

        // mock injected in viewModel
        viewModel = new MainActivityViewModel(
                userRepository,
                permissionChecker,
                locationRepository,
                searchRepository,
                restaurantRepository);

    }

    private User getDefaultUser(int index) {

        return new User(
                "uid" + index,
                "username" + index,
                "urlpicture" + index,
                "email" + index,
                "placeId" + index,
                "restaurant_for_today_name" + index,
                "restaurant_for_today_address" + index,
                "restaurant_for_today_pic_url" + index,
                null,
                true
        );
    }

}//END of MainActivityViewModel
