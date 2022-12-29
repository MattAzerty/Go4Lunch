package fr.melanoxy.go4lunch.data;

import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import fr.melanoxy.go4lunch.LiveDataTestUtils;
import fr.melanoxy.go4lunch.data.repositories.SearchRepository;

@RunWith(MockitoJUnitRunner.class)
public class SearchRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private SearchRepository searchRepository;


    @Before
    public void setUp() {

        searchRepository = new SearchRepository();

    }

    @Test
    public void nominal_case_searchField() {

        //Given
    String query = "MyQuery";
    searchRepository.searchField(query);

        // When
        LiveDataTestUtils.observeForTesting(searchRepository.getSearchFieldLiveData(), value -> {
            // Then
            assertEquals(query, value);
        });
    }


}//End of SearchRepositoryTest
