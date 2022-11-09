package fr.melanoxy.go4lunch.ui.MapView;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.repositories.LocationRepository;

public class MapViewViewModel extends ViewModel {

    //INIT
    //Injected with the ViewModelFactory
    @NonNull
    private final LocationRepository locationRepository;


    //CONSTRUCTOR
    public MapViewViewModel(@NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public LiveData<Location> getUserLocationLiveData() {
        return locationRepository.getLocationLiveData();
    }


}//end of WorkmatesViewModel//END of MainViewModel
