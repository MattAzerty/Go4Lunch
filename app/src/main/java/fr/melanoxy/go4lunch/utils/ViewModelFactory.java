package fr.melanoxy.go4lunch.utils;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.LocationServices;

import fr.melanoxy.go4lunch.MainApplication;
import fr.melanoxy.go4lunch.ui.MapView.PermissionChecker;
import fr.melanoxy.go4lunch.data.repositories.LocationRepository;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;
import fr.melanoxy.go4lunch.MainActivityViewModel;
import fr.melanoxy.go4lunch.ui.Workmates.WorkmatesViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private volatile static ViewModelFactory sInstance;

    @NonNull
    private final UserRepository userRepository;
    @NonNull
    private final PermissionChecker permissionChecker;
    @NonNull
    private final LocationRepository locationRepository;

    public static ViewModelFactory getInstance() {
        if (sInstance == null) {
            // Double Checked Locking singleton pattern with Volatile works on Android since Honeycomb
            synchronized (ViewModelFactory.class) {
                if (sInstance == null) {
                    Application application = MainApplication.getApplication();

                    sInstance = new ViewModelFactory(
                            new UserRepository(
                            ),
                            new PermissionChecker(
                                    application
                            ),
                            new LocationRepository(
                                    LocationServices.getFusedLocationProviderClient(
                                            application
                                    )
                            )
                    );
                }
            }
        }

        return sInstance;
    }

    private ViewModelFactory(
            @NonNull UserRepository userRepository,
            @NonNull PermissionChecker permissionChecker,
            @NonNull LocationRepository locationRepository
    ) {
        this.userRepository = userRepository;
        this.permissionChecker = permissionChecker;
        this.locationRepository = locationRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(
                    userRepository,
                    permissionChecker,
                    locationRepository
            );
        /*} else if (modelClass.isAssignableFrom(FilterPageViewModel.class)) {
            return (T) new FilterPageViewModel(
                    MainApplication.getInstance(),
                    reunionRepository
            );*/
        } else if (modelClass.isAssignableFrom(WorkmatesViewModel.class)) {
            return (T) new WorkmatesViewModel(
                    userRepository
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class : " + modelClass);
    }
}