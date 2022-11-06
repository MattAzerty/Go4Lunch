package fr.melanoxy.go4lunch.ui.MapView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

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


}//end of WorkmatesViewModel//END of MainViewModel
