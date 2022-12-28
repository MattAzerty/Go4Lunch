package fr.melanoxy.go4lunch.data;

import android.location.Location;

import androidx.lifecycle.LiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;

import fr.melanoxy.go4lunch.data.repositories.LocationRepository;

@RunWith(MockitoJUnitRunner.class)
public class LocationRepositoryTest {

    @Mock
    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationRepository locationRepository;

    @Before
    public void setUp() {

        locationRepository = new LocationRepository(fusedLocationProviderClient);

    }

    @Test
    public void testGetLocationLiveData() {
        LiveData<Location> locationLiveData = locationRepository.getLocationLiveData();
        Assert.assertNotNull(locationLiveData);
        Assert.assertNull(locationLiveData.getValue());
    }

    @Test
    public void testStartLocationRequest() {

        //startLocationRequest
        locationRepository.startLocationRequest();
//TODO how?

    }

    @Test
    public void testStopLocationRequest() throws NoSuchFieldException, IllegalAccessException {

        // Use reflection to set the value of the private callback field in location repository
        Field callbackField = locationRepository.getClass().getDeclaredField("callback");
        callbackField.setAccessible(true);
        LocationCallback callback = Mockito.mock(LocationCallback.class);
        callbackField.set(locationRepository, callback);
        //stopLocationRequest
        locationRepository.stopLocationRequest();
        Mockito.verify(fusedLocationProviderClient).removeLocationUpdates(callback);
    }

}//End of LocationRepositoryTest

