package fr.melanoxy.go4lunch.ui;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import static fr.melanoxy.go4lunch.UnitTestUtils.getOrAwaitValue;

import androidx.annotation.NonNull;
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

import fr.melanoxy.go4lunch.LiveDataTestUtils;
import fr.melanoxy.go4lunch.ReadJsonAsString;
import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.DetailsResult;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.PlaceIdDetailsResponse;
import fr.melanoxy.go4lunch.data.repositories.RestaurantRepository;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;
import fr.melanoxy.go4lunch.ui.RestaurantDetailsActivity.LunchmateStateItem;
import fr.melanoxy.go4lunch.ui.RestaurantDetailsActivity.RestaurantDetailsStateItem;
import fr.melanoxy.go4lunch.ui.RestaurantDetailsActivity.RestaurantDetailsViewModel;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantDetailsViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private UserRepository userRepository;

    private MutableLiveData<PlaceIdDetailsResponse> placeIdDetailsResponseLiveData;
    PlaceIdDetailsResponse placeIdDetailsResponse;
    private RestaurantDetailsViewModel viewModel;
    private final String PLACE_ID="ChIJc8T8eAO6j4ARR1AWdP3Iyi8";

    @Before
    public void setUp() throws Exception {
        //PlaceIdDetailsResponse from a json file (from default emulator coordinates)
        Gson gson = new GsonBuilder().create();
        String detailsJson = ReadJsonAsString.readFileAsString("src/test/java/fr/melanoxy/go4lunch/resources/placeiddetails.json");
        placeIdDetailsResponse = gson.fromJson(detailsJson , PlaceIdDetailsResponse.class);
    }

    @Test
    public void when_PlaceIdDetailsResponse_is_not_null_should_display_RestaurantDetailsMissing() throws InterruptedException {

        // Given
        // MutableLiveData for RestaurantRepository Mock
        placeIdDetailsResponseLiveData = new MutableLiveData<>();//from RestaurantRepository

        //Value for MutableLiveData
        placeIdDetailsResponseLiveData.setValue(placeIdDetailsResponse);

        //We build the mock for RestaurantRepository
        restaurantRepository = Mockito.mock(RestaurantRepository.class);

        // With given methods to replace
        Mockito.doReturn(placeIdDetailsResponseLiveData).when(restaurantRepository).getPlaceIdDetailsResponseLiveData();


        // Repository is now injected in the ViewModel
        viewModel = new RestaurantDetailsViewModel(
                userRepository,
                restaurantRepository);

        // When
        RestaurantDetailsStateItem result = getOrAwaitValue(viewModel.getRestaurantDetailsResults());

        assertEquals("http://michaelsatshoreline.com/",
                result.getWebsite());

    }

    @Test
    public void when_lunchmates_is_not_null_should_display_lunchmates() throws InterruptedException {

        // Given
        // MutableLiveData for RestaurantRepository Mock
        MutableLiveData<List<User>> lunchmatesLiveData = new MutableLiveData<>();//from UserRepository
        placeIdDetailsResponseLiveData = new MutableLiveData<>();//from RestaurantRepository

        //Value for MutableLiveData
        lunchmatesLiveData.setValue(getDefaultLunchmates());
        placeIdDetailsResponseLiveData.setValue(placeIdDetailsResponse);

        //We build the mock for RestaurantRepository
        userRepository = Mockito.mock(UserRepository.class);
        restaurantRepository = Mockito.mock(RestaurantRepository.class);

        // With given methods to replace
        Mockito.doReturn(lunchmatesLiveData).when(userRepository).getLunchmatesLiveData(PLACE_ID);
        Mockito.doReturn(placeIdDetailsResponseLiveData).when(restaurantRepository).getPlaceIdDetailsResponseLiveData();

        // Repository is now injected in the ViewModel
        viewModel = new RestaurantDetailsViewModel(
                userRepository,
                restaurantRepository);

        // When
        LiveDataTestUtils.observeForTesting(viewModel.getLunchmateStateItemsLiveData(PLACE_ID), value -> {
            // Then
            // Step 1 for Then : assertions...
            assertEquals(getDefaultLunchmatesStateItems(), value);

            // ... Step 2 for Then : verify !
            verify(userRepository).getLunchmatesLiveData(PLACE_ID);
            verifyNoMoreInteractions(userRepository);
        });

    }


    //Region IN
    private List<User> getDefaultLunchmates() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
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
                PLACE_ID,//placeId from json placeiddetails
                "restaurant_for_today_name" + index,
                "restaurant_for_today_address" + index,
                "restaurant_for_today_pic_url" + index,
                null,
                true
        );
    }
//End of Region IN

    // region OUT (this is the default values that are exposed by the ViewModel)
    private List<LunchmateStateItem> getDefaultLunchmatesStateItems() {
        List<LunchmateStateItem> lunchmates = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            lunchmates.add(getDefaultLunchmatesStateItem(i));
        }

        return lunchmates;
    }

    @NonNull
    private LunchmateStateItem getDefaultLunchmatesStateItem(int index) {
        return new LunchmateStateItem(
                "uid"+index,
                "username"+index,
                "urlpicture"+index
        );
    }
    // endregion OUT
    // For every public method in the ViewModel, we should verify that data passed to the ViewModel
    // is correctly dispatched to the underlying components (the repository, in this instance)
    @Test
    public void verify_on_RestaurantForToday_clicked() {

        // Given
        // MutableLiveData for RestaurantRepository Mock
        placeIdDetailsResponseLiveData = new MutableLiveData<>();//from RestaurantRepository

        //Value for MutableLiveData
        placeIdDetailsResponseLiveData.setValue(placeIdDetailsResponse);

        //We build the mock for RestaurantRepository
        restaurantRepository = Mockito.mock(RestaurantRepository.class);

        // With given methods to replace
        Mockito.doReturn(placeIdDetailsResponseLiveData).when(restaurantRepository).getPlaceIdDetailsResponseLiveData();

        viewModel = new RestaurantDetailsViewModel(
                userRepository,
                restaurantRepository);

        // Given
        String placeName = "placeName";
        String placeAddress = "placeAddress";
        String placePicUrl = "placePicUrl";

        // When
        viewModel.onRestaurantForTodayClicked(PLACE_ID,placeName,placeAddress,placePicUrl);
        // Then
        verify(userRepository).updateTodayRestaurantUser(PLACE_ID,placeName,placeAddress,placePicUrl);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void verify_on_Fav_clicked() {

        // Given
        // MutableLiveData for RestaurantRepository Mock
        placeIdDetailsResponseLiveData = new MutableLiveData<>();//from RestaurantRepository

        //Value for MutableLiveData
        placeIdDetailsResponseLiveData.setValue(placeIdDetailsResponse);

        //We build the mock for RestaurantRepository
        restaurantRepository = Mockito.mock(RestaurantRepository.class);

        // With given methods to replace
        Mockito.doReturn(placeIdDetailsResponseLiveData).when(restaurantRepository).getPlaceIdDetailsResponseLiveData();

        viewModel = new RestaurantDetailsViewModel(
                userRepository,
                restaurantRepository);

        // When
        viewModel.onFavClicked(PLACE_ID);
        // Then
        verify(userRepository).updateFavList(PLACE_ID);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void verify_on_searchPlaceIdDetails_Triggered() {

        // Given
        // MutableLiveData for RestaurantRepository Mock
        placeIdDetailsResponseLiveData = new MutableLiveData<>();//from RestaurantRepository

        //Value for MutableLiveData
        placeIdDetailsResponseLiveData.setValue(placeIdDetailsResponse);

        //We build the mock for RestaurantRepository
        restaurantRepository = Mockito.mock(RestaurantRepository.class);

        // With given methods to replace
        Mockito.doReturn(placeIdDetailsResponseLiveData).when(restaurantRepository).getPlaceIdDetailsResponseLiveData();

        viewModel = new RestaurantDetailsViewModel(
                userRepository,
                restaurantRepository);

        // When
        viewModel.searchPlaceIdDetails(PLACE_ID,"opening_hours,website,formatted_phone_number,rating","apikey");
            //verify !
            verify(restaurantRepository).getPlaceIdDetailsResponseLiveData();
    }


    @Test
    public void verify_on_back_Pressed() {

        // Given
        // MutableLiveData for RestaurantRepository Mock
        placeIdDetailsResponseLiveData = new MutableLiveData<>();//from RestaurantRepository

        //Value for MutableLiveData
        placeIdDetailsResponseLiveData.setValue(placeIdDetailsResponse);

        //We build the mock for RestaurantRepository
        restaurantRepository = Mockito.mock(RestaurantRepository.class);

        // With given methods to replace
        Mockito.doReturn(placeIdDetailsResponseLiveData).when(restaurantRepository).getPlaceIdDetailsResponseLiveData();

        viewModel = new RestaurantDetailsViewModel(
                userRepository,
                restaurantRepository);

        // When
        viewModel.onBackPressed();
        // Then
        verify(userRepository).onEndOfDetailsActivity();
        verifyNoMoreInteractions(userRepository);
    }


}//END of RestaurantDetailsViewModelTest
