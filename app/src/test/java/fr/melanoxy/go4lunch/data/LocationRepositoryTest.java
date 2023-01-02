package fr.melanoxy.go4lunch.data;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.location.Location;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;

import fr.melanoxy.go4lunch.LiveDataTestUtils;
import fr.melanoxy.go4lunch.data.repositories.LocationRepository;

@RunWith(MockitoJUnitRunner.class)
public class LocationRepositoryTest {

    @Mock
    private FusedLocationProviderClient fusedLocationProviderClient;
    @Mock
    private LocationResult locationResult;
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
        LocationCallback callback = mock(LocationCallback.class);
        callbackField.set(locationRepository, callback);
        //stopLocationRequest
        locationRepository.stopLocationRequest();
        Mockito.verify(fusedLocationProviderClient).removeLocationUpdates(callback);
    }

}//End of LocationRepositoryTest

