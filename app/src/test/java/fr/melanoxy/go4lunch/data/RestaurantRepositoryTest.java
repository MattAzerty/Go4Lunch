package fr.melanoxy.go4lunch.data;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import fr.melanoxy.go4lunch.LiveDataTestUtils;
import fr.melanoxy.go4lunch.ReadJsonAsString;
import fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search.RestaurantsNearbyResponse;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_autocomplete.PlaceAutocompleteResponse;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.DetailsResult;
import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.PlaceIdDetailsResponse;
import fr.melanoxy.go4lunch.data.repositories.RestaurantRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private RestaurantSearchService restaurantSearchService;

    private RestaurantRepository restaurantRepository;
    private PlaceIdDetailsResponse placeIdDetailsResponse;
    private final String location = "-48.876667,-123.393333";
    private final String radius = "2000";
    private final String type = "restaurant";

    private final String placeId = "PLACE_ID";
    private final String fields1 = "opening_hours,website,formatted_phone_number,rating";//asked from restaurantActivity

    private final String input = "MY_INPUT";

    private final String apikey = "API_KEY";

    @Before
    public void setUp() throws Exception {

//mock restaurantSearchService with special constructor
        restaurantSearchService = mock(RestaurantSearchService.class);
        restaurantRepository = new RestaurantRepository(restaurantSearchService, apikey);
//nearby
        given(restaurantSearchService.searchNearbyRestaurants(location, radius, type, apikey))
                .willReturn(mockedNearbyResponseCall);
        given(mockedNearbyApiResponse.body()).willReturn(mockedRestaurantNearbyResponse);
//placeIdDetails
        given(restaurantSearchService.searchPlaceIdDetails(placeId, fields1, "fr", apikey))
                .willReturn(mockedDetailsResponseCall);
        //PlaceIdDetailsResponse from a json file (from default emulator coordinates)
        Gson gson = new GsonBuilder().create();
        String detailsJson = ReadJsonAsString.readFileAsString("src/test/java/fr/melanoxy/go4lunch/resources/placeiddetails.json");
        placeIdDetailsResponse = gson.fromJson(detailsJson, PlaceIdDetailsResponse.class);

        given(mockedDetailsApiResponse.body()).willReturn(placeIdDetailsResponse);

//autocomplete
        //from a json file (from default emulator coordinates) and all placeId replaced by "PLACE_ID"
        String predictionsJson = ReadJsonAsString.readFileAsString("src/test/java/fr/melanoxy/go4lunch/resources/autocomplete.json");
        PlaceAutocompleteResponse predictionsResponse = gson.fromJson(predictionsJson, PlaceAutocompleteResponse.class);

        given(restaurantSearchService.searchFromQueryPlaces(input, type, location, radius, apikey))
                .willReturn(mockedAutocompleteResponseCall);
        given(mockedAutocompleteApiResponse.body()).willReturn(predictionsResponse);

        //asked from RestaurantRepository
        String fields2 = "opening_hours,place_id,geometry,name,formatted_address,rating,photo";
        given(restaurantSearchService.searchPlaceIdDetails(placeId, fields2, "fr", apikey))
                .willReturn(mockedDetailsResponseCall);

    }

    @Test
    public void nominal_case_searchNearbyRestaurants() {
        // Given
        // Let's call the repository method
        restaurantRepository.searchNearbyRestaurants(location, radius, type, apikey);
        // Capture the callback waiting for data
        verify(restaurantSearchService.searchNearbyRestaurants(location, radius, type, apikey))
                .enqueue(callbackNearbyResponseArgumentCaptor.capture());
        // When
        // Trigger the response ourselves
        callbackNearbyResponseArgumentCaptor.getValue().onResponse(mockedNearbyResponseCall, mockedNearbyApiResponse);

        LiveData<RestaurantsNearbyResponse> result = restaurantRepository.getRestaurantNearbyResponseLiveData();
        // Then
        // Assert the result is posted to the LiveData
        LiveDataTestUtils.observeForTesting(result, liveData -> {
            assertEquals(mockedRestaurantNearbyResponse, liveData);
        });

        LiveData<String> result2 = restaurantRepository.getRestaurantRepositoryErrorLiveData();
        LiveDataTestUtils.observeForTesting(result2, liveData -> {
            assertEquals("noError", liveData);
        });
    }

    @Test
    public void nominal_case_searchPlaceIdDetails() {

        // Given
        // Let's call the repository method
        restaurantRepository.searchPlaceIdDetails(placeId, fields1, apikey);
        // Capture the callback waiting for data
        verify(restaurantSearchService.searchPlaceIdDetails(placeId, fields1, "fr", apikey))
                .enqueue(callbackPlaceIdResponseArgumentCaptor.capture());
        // When
        // Trigger the response ourselves
        callbackPlaceIdResponseArgumentCaptor.getValue().onResponse(mockedDetailsResponseCall, mockedDetailsApiResponse);

        LiveData<PlaceIdDetailsResponse> result = restaurantRepository.getPlaceIdDetailsResponseLiveData();
        // Then
        // Assert the result is posted to the LiveData
        LiveDataTestUtils.observeForTesting(result, liveData -> {
            assertEquals(placeIdDetailsResponse, liveData);
        });
    }

    @Test
    public void nominal_case_searchFromQueryPlaces() {

        // Given
        // Let's call the repository method
        restaurantRepository.searchFromQueryPlaces(input, type, location, radius, apikey);
        // Capture the callback waiting for data
        verify(restaurantSearchService.searchFromQueryPlaces(input, type, location, radius, apikey))
                .enqueue(callbackAutocompleteResponseArgumentCaptor.capture());
        // When
        // Trigger the response ourselves
        callbackAutocompleteResponseArgumentCaptor.getValue().onResponse(mockedAutocompleteResponseCall, mockedAutocompleteApiResponse);

        LiveData<Integer> result = restaurantRepository.getRestaurantsSizeByQueryLiveData();
        // Then
        // Assert the result is posted to the LiveData
        LiveDataTestUtils.observeForTesting(result, liveData -> {
            assertEquals(Integer.valueOf(5), liveData);
        });
    }

    @Test
    public void on_getUrlPicture() {
        String photoRef = "photoRef";
        String result = restaurantRepository.getUrlPicture(photoRef);
        String expectedResult = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoRef + "&key=" + apikey;
        assertEquals(expectedResult, result);
    }


    // region IN
    @Captor
    private ArgumentCaptor<Callback<RestaurantsNearbyResponse>> callbackNearbyResponseArgumentCaptor;
    @Captor
    private ArgumentCaptor<Callback<PlaceIdDetailsResponse>> callbackPlaceIdResponseArgumentCaptor;
    @Captor
    private ArgumentCaptor<Callback<PlaceAutocompleteResponse>> callbackAutocompleteResponseArgumentCaptor;

    @Mock
    private Call<RestaurantsNearbyResponse> mockedNearbyResponseCall;
    @Mock
    private Call<PlaceIdDetailsResponse> mockedDetailsResponseCall;
    @Mock
    private Call<PlaceAutocompleteResponse> mockedAutocompleteResponseCall;
    // endregion IN

    // region OUT
    @Mock
    private Response<RestaurantsNearbyResponse> mockedNearbyApiResponse;
    @Mock
    private Response<PlaceIdDetailsResponse> mockedDetailsApiResponse;
    @Mock
    private Response<PlaceAutocompleteResponse> mockedAutocompleteApiResponse;

    @Mock
    private RestaurantsNearbyResponse mockedRestaurantNearbyResponse;
    // endregion OUT

}//END of RestaurantRepositoryTest
