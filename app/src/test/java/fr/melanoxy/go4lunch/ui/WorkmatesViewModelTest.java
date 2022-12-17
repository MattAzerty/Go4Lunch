package fr.melanoxy.go4lunch.ui;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import android.app.Application;

import androidx.annotation.NonNull;
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
import java.util.List;

import fr.melanoxy.go4lunch.LiveDataTestUtils;
import fr.melanoxy.go4lunch.R;
import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.repositories.SearchRepository;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;
import fr.melanoxy.go4lunch.ui.Workmates.WorkmatesStateItem;
import fr.melanoxy.go4lunch.ui.Workmates.WorkmatesViewModel;

@RunWith(MockitoJUnitRunner.class)
public class WorkmatesViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private UserRepository userRepository;
    @Mock
    private SearchRepository searchRepository;
    @Mock
    private Application application;

    private WorkmatesViewModel viewModel;
    private static final String EATING_AT = "EATING_AT";
    private static final String RESTAURANT_NOT_SET = "RESTAURANT_NOT_SET";
    private static final String DEFAULT_WORKMATE_NAME = "DEFAULT_WORKMATE_NAME";
    private static final String DEFAULT_RESTAURANT_NAME = "DEFAULT_RESTAURANT_NAME";
    private MutableLiveData<List<User>> workmatesLiveData;
    private MutableLiveData<String> queryLiveData;

    @Before
    public void setUp() {
        // Reinitialize LiveData every test
        workmatesLiveData = new MutableLiveData<>();//from UserRepository
        queryLiveData = new MutableLiveData<>();//from SearchRepository
        // Mock resources
        given(application.getString(R.string.workmates_eating_at)).willReturn(EATING_AT);
        //given(application.getString(R.string.workmates_restaurant_not_set)).willReturn(RESTAURANT_NOT_SET);

        // Set default values to LiveData
        List<User> workmates = getDefaultWorkmates(3);
        workmatesLiveData.setValue(workmates);

        // Mock LiveData returned from Repository
        searchRepository = mock(SearchRepository.class);
        userRepository = mock(UserRepository.class);

        // given behavior for some methods called
        Mockito.doReturn(queryLiveData).when(searchRepository).getSearchFieldLiveData();
        Mockito.doReturn(workmatesLiveData).when(userRepository).getWorkmates();

        viewModel = new WorkmatesViewModel(
                userRepository,
                searchRepository,
                application);
    }

    @Test
    public void when_workmates_not_null_but_query_is_should_display_all_workmates() {

        queryLiveData.setValue(null);

        // When
        LiveDataTestUtils.observeForTesting(viewModel.getViewStateLiveData(), value -> {
            // Then
            // Step 1 for Then : assertions...
            assertEquals(3, value.size());

            // ... Step 2 for Then : verify !
            verify(userRepository).getWorkmates();
            verifyNoMoreInteractions(userRepository);
        });
    }

   @Test
    public void when_workmates_and_query_not_null_should_display_specific_workmate() {

       queryLiveData.setValue(DEFAULT_WORKMATE_NAME + 1);//workmate #2 out of 3

        // When
        LiveDataTestUtils.observeForTesting(viewModel.getViewStateLiveData(), value -> {
            // Then
            // Step 1 for Then : assertions...
            assertEquals(1, value.size());
            assertEquals(EATING_AT+DEFAULT_RESTAURANT_NAME + 1+".", value.get(0).getMainField());
            // ... Step 2 for Then : verify !
            verify(userRepository).getWorkmates();
            verifyNoMoreInteractions(userRepository);
        });
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
                "uid" + index,
                DEFAULT_WORKMATE_NAME + index,
                "urlpicture" + index,
                "email" + index,
                "placeId" + index,
                DEFAULT_RESTAURANT_NAME + index,
                "restaurant_for_today_address" + index,
                "restaurant_for_today_pic_url" + index,
                null,
                true
        );
    }

    // region OUT (this is the default values that are exposed by the ViewModel)
    private List<WorkmatesStateItem> getDefaultWorkmatesStateItems() {
        List<WorkmatesStateItem> workmates = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            workmates.add(getDefaultWorkmatesStateItem(i));
        }

        return workmates;
    }

    @NonNull
    private WorkmatesStateItem getDefaultWorkmatesStateItem(int index) {
        return new WorkmatesStateItem(
                "uid" + index,
                DEFAULT_WORKMATE_NAME + index,
                "email" + index,
                "urlpicture" + index,
                EATING_AT+DEFAULT_RESTAURANT_NAME + index,
                "placeId" + index,
                DEFAULT_RESTAURANT_NAME + index,
                "restaurant_for_today_address" + index,
                "restaurant_for_today_pic_url" + index
        );
    }
    // endregion OUT

}//End of WorkmatesViewModelTest
