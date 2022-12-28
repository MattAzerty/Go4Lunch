package fr.melanoxy.go4lunch.ui;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import static fr.melanoxy.go4lunch.UnitTestUtils.getOrAwaitValue;

import android.content.Context;
import android.location.Location;
import android.net.Uri;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import fr.melanoxy.go4lunch.LiveDataTestUtils;
import fr.melanoxy.go4lunch.MainActivityViewModel;
import fr.melanoxy.go4lunch.R;
import fr.melanoxy.go4lunch.ReadJsonAsString;
import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search.RestaurantsNearbyResponse;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.PlaceIdDetailsResponse;
import fr.melanoxy.go4lunch.data.repositories.LocationRepository;
import fr.melanoxy.go4lunch.data.repositories.RestaurantRepository;
import fr.melanoxy.go4lunch.data.repositories.SearchRepository;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;
import fr.melanoxy.go4lunch.data.PermissionChecker;

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
    private Location userLocation;

    private MutableLiveData<Location> locationLiveData;
    private MutableLiveData<User> userLiveData;
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
        userLiveData = new MutableLiveData<>();//from UserRepository
        restaurantsNearbyLiveData = new MutableLiveData<>();//from RestaurantRepository
        predictionsDetailsLiveData = new MutableLiveData<>();//from RestaurantRepository
        queryLiveData = new MutableLiveData<>();//from SearchRepository
        restaurantRepositoryErrorLiveData = new MutableLiveData<>();//from RestaurantRepository

        //mock for userLocation
        userLocation = mock(Location.class);
        given(userLocation.getLatitude()).willReturn(-48.876667);
        //given(userLocation.getLongitude()).willReturn(-123.393333);

        //NearbyResponse from a json file (from default emulator coordinates)
        Gson gson = new GsonBuilder().create();
        String nearbyJson = ReadJsonAsString.readFileAsString("src/test/java/fr/melanoxy/go4lunch/resources/nearby.json");
        nearbyResponse = gson.fromJson(nearbyJson, RestaurantsNearbyResponse.class);

        //the livedata value are set here instead of repository
        locationLiveData.setValue(userLocation);
        userLiveData.setValue(getDefaultUser(0));
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
        Mockito.doReturn(userLiveData).when(userRepository).getConnectedUserLiveData();
        //Mockito.doReturn(queryLiveData).when(searchRepository).getSearchFieldLiveData();
        Mockito.doReturn(restaurantsNearbyLiveData).when(restaurantRepository).getRestaurantNearbyResponseLiveData();
        Mockito.doReturn(queryLiveData).when(searchRepository).getSearchFieldLiveData();
        Mockito.doReturn(predictionsDetailsLiveData).when(restaurantRepository).getPredictionsDetailsLiveData();
        Mockito.doReturn(restaurantRepositoryErrorLiveData).when(restaurantRepository).getRestaurantRepositoryErrorLiveData();
        Mockito.doReturn(true).when(permissionChecker).hasLocationPermission();
        Mockito.doReturn(true).when(permissionChecker).hasNotificationPermission();

    }

    @Test
    public void no_gps_permission_should_send_a_snackBar_message() throws InterruptedException {

        locationLiveData.setValue(null);
        Mockito.doReturn(false).when(permissionChecker).hasLocationPermission();

        // mock injected in viewModel
        viewModel = new MainActivityViewModel(
                userRepository,
                permissionChecker,
                locationRepository,
                searchRepository,
                restaurantRepository);

        // When
        viewModel.refresh();//TODO why
        Boolean result = getOrAwaitValue(viewModel.getProgressBarStateLiveData());

        assertEquals(false,
                result);

        // Then
        LiveDataTestUtils.observeForTesting(viewModel.getSnackBarSingleLiveEvent(), value -> {
            // Then
            assertEquals(Integer.valueOf(R.string.error_gps),// gps location not allowed integer
                    value);
        });
    }

    @Test
    public void verify_isUserAuthenticated() {
        // mock injected in viewModel
        viewModel = new MainActivityViewModel(
                userRepository,
                permissionChecker,
                locationRepository,
                searchRepository,
                restaurantRepository);
        // When
        viewModel.isUserAuthenticated();
        // Then
        verify(userRepository).isUserAuthenticatedInFirebase();
    }

    @Test
    public void get_Connected_User_return_DefaultUser() throws InterruptedException {

        // mock injected in viewModel
        viewModel = new MainActivityViewModel(
                userRepository,
                permissionChecker,
                locationRepository,
                searchRepository,
                restaurantRepository);

        // When
        User result = getOrAwaitValue(viewModel.getConnectedUserLiveData());
//should contain a User
        assertEquals(getDefaultUser(0),
                result);

    }
    @Test
    public void get_UserLocation_return_DefaultLocation() throws InterruptedException {

        // mock injected in viewModel
        viewModel = new MainActivityViewModel(
                userRepository,
                permissionChecker,
                locationRepository,
                searchRepository,
                restaurantRepository);

        // When
        Location result = getOrAwaitValue(viewModel.getUserLocationLiveData());
//should contain a User
        assertEquals(-48.876667,
                result.getLatitude(),0.000001);

    }

    @Test
    public void get_NotifyState_return_User_when_notify_permission_is_true() throws InterruptedException {


        // mock injected in viewModel
        viewModel = new MainActivityViewModel(
                userRepository,
                permissionChecker,
                locationRepository,
                searchRepository,
                restaurantRepository);

        // When
        viewModel.refresh();
        User result = getOrAwaitValue(viewModel.getNotifyStateLiveData());
//should contain a User
        assertEquals(getDefaultUser(0),
                result);

    }

    @Test
    public void progressBar_should_be_on_if_GpsPermission_granted_but_nearbyResults_are_null() throws InterruptedException {

        restaurantsNearbyLiveData.setValue(null);

        // mock injected in viewModel
        viewModel = new MainActivityViewModel(
                userRepository,
                permissionChecker,
                locationRepository,
                searchRepository,
                restaurantRepository);

        // When
        viewModel.refresh();
        Boolean result = getOrAwaitValue(viewModel.getProgressBarStateLiveData());
//StateItems should contain 20 restaurants
        assertEquals(true,
                result);

    }

    @Test
    public void progressBar_should_be_on_if_GpsPermission_granted_and_query_not_null_but_autocompleteResuls_are_null() throws InterruptedException {

        queryLiveData.setValue("McDo");

        // mock injected in viewModel
        viewModel = new MainActivityViewModel(
                userRepository,
                permissionChecker,
                locationRepository,
                searchRepository,
                restaurantRepository);

        // When
        viewModel.refresh();
        Boolean result = getOrAwaitValue(viewModel.getProgressBarStateLiveData());
//StateItems should contain 20 restaurants
        assertEquals(true,
                result);

    }

    /*@Test//TODO here why nearby not null
    public void on_searchNearbyRestaurant_triggered() {

        restaurantsNearbyLiveData.setValue(null);
        Location previousLocation = Mockito.mock(Location.class);
        when(userLocation.distanceTo(previousLocation)).thenReturn(100.0f);
        // mock injected in viewModel
        viewModel = new MainActivityViewModel(
                userRepository,
                permissionChecker,
                locationRepository,
                searchRepository,
                restaurantRepository);

        // When
        viewModel.searchNearbyRestaurant(userLocation,"2000","restaurant","apikey");
        // Then
        verify(restaurantRepository).searchNearbyRestaurants("-48.876667,-123.393333","2000","restaurant","apikey");
    }*/

    @Test
    public void verify_on_searchQuery_call() {

        // mock injected in viewModel
        viewModel = new MainActivityViewModel(
                userRepository,
                permissionChecker,
                locationRepository,
                searchRepository,
                restaurantRepository);
        // Given
        String query = "my query";

        // When
        viewModel.onSearchQueryCall(query);
        // Then
        verify(searchRepository).searchField(query);
    }

    @Test
    public void verify_onUserLoggedSuccess_call() {

        // mock injected in viewModel
        viewModel = new MainActivityViewModel(
                userRepository,
                permissionChecker,
                locationRepository,
                searchRepository,
                restaurantRepository);

        // When
        viewModel.onUserLoggedSuccess();
        // Then
        verify(userRepository).getWorkmates();
        verify(userRepository).createUser();
    }

    @Test
    public void verify_onSignOut_call() {


        Context context = mock(Context.class);

        // mock injected in viewModel
        viewModel = new MainActivityViewModel(
                userRepository,
                permissionChecker,
                locationRepository,
                searchRepository,
                restaurantRepository);

        // When
        viewModel.onSignOut(context);
        // Then
        verify(userRepository).signOut(context);
    }

    @Test
    public void verify_on_your_lunch_clicked_case_no_lunch_for_today() {

        given(userRepository.getUser()).willReturn(getDefaultUser(0));//user "0" has no lunch for today

        // mock injected in viewModel
        viewModel = new MainActivityViewModel(
                userRepository,
                permissionChecker,
                locationRepository,
                searchRepository,
                restaurantRepository);

        // When
        viewModel.onYourLunchClicked();
        // Then
        LiveDataTestUtils.observeForTesting(viewModel.getSnackBarSingleLiveEvent(), value -> {
            // Then
            assertEquals(Integer.valueOf(R.string.restaurant_selected),// no restaurant selected integer
                    value);
        });
    }

    @Test
    public void verify_on_your_lunch_clicked_case_lunch_for_today() {

        given(userRepository.getUser()).willReturn(getDefaultUser(1));//user "1" as a lunch for today

        // mock injected in viewModel
        viewModel = new MainActivityViewModel(
                userRepository,
                permissionChecker,
                locationRepository,
                searchRepository,
                restaurantRepository);

        // When
        viewModel.onYourLunchClicked();
        // Then
        LiveDataTestUtils.observeForTesting(viewModel.getRestaurantDetailsActivitySingleLiveEvent(), value -> {
            // Then
            assertEquals("placeid1",
                    value.getPlace_id());
        });
    }

    @Test
    public void verify_on_username_from_settings_change_save_clicked() {

        // mock injected in viewModel
        viewModel = new MainActivityViewModel(
                userRepository,
                permissionChecker,
                locationRepository,
                searchRepository,
                restaurantRepository);

        // When
        viewModel.OnSettingsSaved(true,null,"newusername");
        // Then
        verify(userRepository).updateUserSettings(true,"urlpicture0","newusername");
        //verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void verify_on_uri_from_settings_change_save_clicked() {

        // mock injected in viewModel
        viewModel = new MainActivityViewModel(
                userRepository,
                permissionChecker,
                locationRepository,
                searchRepository,
                restaurantRepository);

        // When
        viewModel.OnSettingsSaved(true, Uri.parse("uri"),"newusername");
        // Then
        verify(userRepository).updateUserSettings(true,"urlpicture0","newusername");
        //verifyNoMoreInteractions(userRepository);
    }

    private User getDefaultUser(int index) {

        return new User(
                "uid" + index,
                "username" + index,
                "urlpicture" + index,
                "email" + index,
                index==0?null:"placeid"+index,
                "restaurant_for_today_name" + index,
                "restaurant_for_today_address" + index,
                "restaurant_for_today_pic_url" + index,
                null,
                true
        );
    }

}//END of MainActivityViewModel
