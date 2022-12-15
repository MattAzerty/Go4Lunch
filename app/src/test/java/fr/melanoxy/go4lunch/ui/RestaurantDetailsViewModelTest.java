package fr.melanoxy.go4lunch.ui;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.PlaceIdDetailsResponse;
import fr.melanoxy.go4lunch.data.repositories.RestaurantRepository;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;
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
    private RestaurantDetailsViewModel viewModel;

    @Before
    public void setUp() {

//TODO Mock initial stuff

    }
//TODO Restaurant details build source
/*
    @Test
    public void when_PlaceIdDetailsResponse_is_not_null_should_display_RestaurantDetailsResults() throws InterruptedException {

        // Given
        // MutableLiveData for RestaurantRepository Mock
        placeIdDetailsResponseLiveData = new MutableLiveData<>();//from RestaurantRepository

        //Value for MutableLiveData
        PlaceIdDetailsResponse placeIdDetailsResponse = getPlaceIdDetailsResponse();
        placeIdDetailsResponseLiveData.setValue(placeIdDetailsResponse);

        //We build the mock for RestaurantRepository
        restaurantRepository = Mockito.mock(RestaurantRepository.class);

        // With given methods to replace
        Mockito.doReturn(reunionsMutableLiveData).when(reunionRepository).getReunionsLiveData();


        // Repository is now injected in the ViewModel
        viewModel = new RestaurantDetailsViewModel(
                userRepository,
                restaurantRepository);

        List<ReunionsViewStateItem> listReunionsViewStateItem = new ArrayList<>();
        listReunionsViewStateItem.add(getDefaultReunionViewStateItem(0));
        listReunionsViewStateItem.add(getDefaultReunionViewStateItem(1));
        listReunionsViewStateItem.add(getDefaultReunionViewStateItem(2));
        listReunionsViewStateItem.add(getDefaultReunionViewStateItem(3));

        // When
        List<ReunionsViewStateItem> result = getOrAwaitValue(viewModel.getViewStateLiveData());

        assertEquals(listReunionsViewStateItem,
                result);

        String result2 = getOrAwaitValue(viewModel.getInfoFilterLiveData());

        assertEquals(
                "- 4 résultat(s) trouvé(s) -",
                result2
        );

    }*/

//TODO OnRestaurantForTodayClicked
    // For every public method in the ViewModel, we should verify that data passed to the ViewModel
    // is correctly dispatched to the underlying components (the repository, in this instance)
    /*@Test
    public void verify_on_RestaurantForToday_clicked() {

        viewModel = new RestaurantDetailsViewModel(
                userRepository,
                restaurantRepository);

        // Given
        String placeId = "placeId";
        String placeName = "placeName";
        String placeAddress = "placeAddress";
        String placePicUrl = "placePicUrl";

        // When
        viewModel.onRestaurantForTodayClicked(placeId,placeName,placeAddress,placePicUrl);
        // Then
        verify(userRepository).updateTodayRestaurantUser(placeId,placeName,placeAddress,placePicUrl);
        verifyNoMoreInteractions(userRepository);
    }*/

//TODO on FAVclicked
    /*@Test
    public void verify_on_Fav_clicked() {

        viewModel = new RestaurantDetailsViewModel(
                userRepository,
                restaurantRepository);

        // Given
        String placeId = "placeId";

        // When
        viewModel.onFavClicked(placeId);
        // Then
        verify(userRepository).updateFavList(placeId);
        verifyNoMoreInteractions(userRepository);
    }*/


}//END of RestaurantDetailsViewModelTest
