package fr.melanoxy.go4lunch.ui;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import static fr.melanoxy.go4lunch.UnitTestUtils.getOrAwaitValue;

import android.location.Location;

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

import java.util.ArrayList;
import java.util.List;

import fr.melanoxy.go4lunch.ReadJsonAsString;
import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search.RestaurantsNearbyResponse;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.PlaceIdDetailsResponse;
import fr.melanoxy.go4lunch.data.repositories.LocationRepository;
import fr.melanoxy.go4lunch.data.repositories.RestaurantRepository;
import fr.melanoxy.go4lunch.data.repositories.SearchRepository;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;
import fr.melanoxy.go4lunch.ui.ListView.ListViewViewModel;
import fr.melanoxy.go4lunch.ui.ListView.RestaurantStateItem;

@RunWith(MockitoJUnitRunner.class)
public class ListViewViewModelTest {

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

    private MutableLiveData<RestaurantsNearbyResponse> restaurantsLiveData;
    private MutableLiveData<Location> userLocationLiveData;
    private MutableLiveData<String> queryLiveData;
    private MutableLiveData<Integer> sizeAutocompleteLiveData;
    private MutableLiveData<List<PlaceIdDetailsResponse>> predictionsDetailsLiveData;
    private MutableLiveData<List<User>> workmatesLiveData;
    private List<PlaceIdDetailsResponse> predictionsDetails;
    private RestaurantsNearbyResponse nearbyResponse;
    private ListViewViewModel viewModel;

    @Before
    public void setUp() throws Exception {

        //NearbyResponse from a json file (from default emulator coordinates)
        Gson gson = new GsonBuilder().create();
        String nearbyJson = ReadJsonAsString.readFileAsString("src/test/java/fr/melanoxy/go4lunch/resources/nearby.json");
        nearbyResponse = gson.fromJson(nearbyJson, RestaurantsNearbyResponse.class);

        //Predictions
        predictionsDetails = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            String details = ReadJsonAsString.readFileAsString("src/test/java/fr/melanoxy/go4lunch/resources/autodetail"+i+".json");
            predictionsDetails.add(gson.fromJson(details, PlaceIdDetailsResponse.class));
        }
    }

    @Test
    public void when_UserLocation_and_NearbyResults_are_not_null_should_display_NearbyRestaurants() throws InterruptedException {

        // Given
        // MutableLiveData used by the mediator to build for the recyclerview the list of StateItem
        restaurantsLiveData = new MutableLiveData<>();//from RestaurantRepository
        userLocationLiveData = new MutableLiveData<>();//from LocationRepository
        queryLiveData = new MutableLiveData<>();//from SearchRepository
        sizeAutocompleteLiveData = new MutableLiveData<>();//from RestaurantRepository
        predictionsDetailsLiveData = new MutableLiveData<>();//from RestaurantRepository
        workmatesLiveData = new MutableLiveData<>();//from userRepository

        //mock for userLocation
        Location userLocation = mock(Location.class);

        //the livedata value are set here instead of repository
        restaurantsLiveData.setValue(nearbyResponse);
        userLocationLiveData.setValue(userLocation);
        queryLiveData.setValue(null);//not used here
        sizeAutocompleteLiveData.setValue(0);//not used here
        predictionsDetailsLiveData.setValue(null);//not used here
        List<User> workmates = getDefaultWorkmates(3);
        workmatesLiveData.setValue(workmates);

        // Mock repositories for needed methods:
        locationRepository = mock(LocationRepository.class);
        searchRepository = mock(SearchRepository.class);
        restaurantRepository = mock(RestaurantRepository.class);
        userRepository = mock(UserRepository.class);

        // given behavior for some methods called
        Mockito.doReturn(userLocationLiveData).when(locationRepository).getLocationLiveData();
        Mockito.doReturn(restaurantsLiveData).when(restaurantRepository).getRestaurantNearbyResponseLiveData();
        Mockito.doReturn(queryLiveData).when(searchRepository).getSearchFieldLiveData();
        Mockito.doReturn(sizeAutocompleteLiveData).when(restaurantRepository).getRestaurantsSizeByQueryLiveData();
        Mockito.doReturn(predictionsDetailsLiveData).when(restaurantRepository).getPredictionsDetailsLiveData();
        Mockito.doReturn(workmatesLiveData).when(userRepository).getWorkmates();

        // mock injected in viewModel
        viewModel = new ListViewViewModel(
                userRepository,
                locationRepository,
                searchRepository,
                restaurantRepository);

        // When
        List<RestaurantStateItem> result = getOrAwaitValue(viewModel.getViewStateLiveData());
//StateItems should contain 20 restaurants
        assertEquals(20,
                result.size());
//3rd restaurant on the list should be marked as 3 lunchmates lunching here.
        assertEquals(Integer.valueOf(3),
                result.get(2).getNumberOfLunchmates());
    }

    @Test
    public void when_query_and_predictionsDetails_are_not_null_should_display_AutocompleteRestaurants() throws InterruptedException {

        // Given
        // MutableLiveData used by the mediator to build for the recyclerview the list of StateItem
        restaurantsLiveData = new MutableLiveData<>();//from RestaurantRepository
        userLocationLiveData = new MutableLiveData<>();//from LocationRepository
        queryLiveData = new MutableLiveData<>();//from SearchRepository
        sizeAutocompleteLiveData = new MutableLiveData<>();//from RestaurantRepository
        predictionsDetailsLiveData = new MutableLiveData<>();//from RestaurantRepository
        workmatesLiveData = new MutableLiveData<>();//from userRepository

        //mock for userLocation
        Location userLocation = mock(Location.class);

        //the livedata value are set here instead of repository
        restaurantsLiveData.setValue(nearbyResponse);
        userLocationLiveData.setValue(userLocation);
        queryLiveData.setValue("McDo");
        sizeAutocompleteLiveData.setValue(3);
        predictionsDetailsLiveData.setValue(predictionsDetails);
        List<User> workmates = getDefaultWorkmates(3);
        workmatesLiveData.setValue(workmates);

        // Mock repositories for needed methods:
        locationRepository = mock(LocationRepository.class);
        searchRepository = mock(SearchRepository.class);
        restaurantRepository = mock(RestaurantRepository.class);
        userRepository = mock(UserRepository.class);

        // given behavior for some methods called
        Mockito.doReturn(userLocationLiveData).when(locationRepository).getLocationLiveData();
        Mockito.doReturn(restaurantsLiveData).when(restaurantRepository).getRestaurantNearbyResponseLiveData();
        Mockito.doReturn(queryLiveData).when(searchRepository).getSearchFieldLiveData();
        Mockito.doReturn(sizeAutocompleteLiveData).when(restaurantRepository).getRestaurantsSizeByQueryLiveData();
        Mockito.doReturn(predictionsDetailsLiveData).when(restaurantRepository).getPredictionsDetailsLiveData();
        Mockito.doReturn(workmatesLiveData).when(userRepository).getWorkmates();

        // mock injected in viewModel
        viewModel = new ListViewViewModel(
                userRepository,
                locationRepository,
                searchRepository,
                restaurantRepository);

        // When
        List<RestaurantStateItem> result1 = getOrAwaitValue(viewModel.getViewStateLiveData());
        assertEquals(0,
                result1.size());//the NearbyResults list is empty then...
        List<RestaurantStateItem> result2 = getOrAwaitValue(viewModel.getViewStateLiveData());
        assertEquals(3,
                result2.size());//the list get filled with predictionsResults
    }

    private List<User> getDefaultWorkmates(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(getDefaultUser(i));
        }
        return users;
    }

    private User getDefaultUser(int index) {

        return new User(
                "uid"+index,
                "username"+index,
                "urlpicture"+index,
                "email" + index,
                "ChIJ--MPCaWwj4ARyYAEuYhsf0E",//3rd item from NearbyResponse dummy
                "restaurant_for_today_name" + index,
                "restaurant_for_today_address" + index,
                "restaurant_for_today_pic_url" + index,
                null,
                true
        );
    }

}//END of ListViewViewModelTest
