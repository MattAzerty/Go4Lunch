package fr.melanoxy.go4lunch.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import fr.melanoxy.go4lunch.MainActivityViewModel;
import fr.melanoxy.go4lunch.config.BuildConfigResolver;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;
import fr.melanoxy.go4lunch.ui.Workmates.WorkmatesViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory factory;

    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory(
                            new UserRepository(
                                    //TODO new BuildConfigResolver()
                            )
                    );
                }
            }
        }

        return factory;
    }

    // This field inherit the singleton property from the ViewModelFactory : it is scoped to the ViewModelFactory
    @NonNull
    private final UserRepository userRepository;

    private ViewModelFactory(@NonNull UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(
                    userRepository
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
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}