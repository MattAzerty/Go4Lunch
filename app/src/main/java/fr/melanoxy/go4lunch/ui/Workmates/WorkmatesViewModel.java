package fr.melanoxy.go4lunch.ui.Workmates;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;

public class WorkmatesViewModel extends ViewModel {
    //INIT
    //Injected with the ViewModelFactory
    @NonNull
    private final UserRepository userRepository;
    private boolean mIsSigningIn;

    //CONSTRUCTOR
    public WorkmatesViewModel(@NonNull UserRepository userRepository) {
        this.userRepository = userRepository;
        mIsSigningIn = false;
    }

    public boolean getIsSigningIn() {
        return mIsSigningIn;
    }


    public void setIsSigningIn(boolean mIsSigningIn) {
        this.mIsSigningIn = mIsSigningIn;
    }

    public LiveData<List<WorkmatesStateItem>> getViewStateLiveData() {
        return Transformations.map(userRepository.getWorkmates(), workmates -> {
            List<WorkmatesStateItem> workmatesViewStateItems = new ArrayList<>();

            // mapping data to the view
            for (User user : workmates) {
                workmatesViewStateItems.add(
                        new WorkmatesStateItem(
                                user.uid,
                                user.username,
                                user.email,
                                user.getUrlPicture(),
                                "i'm eating @La fourchette"
                        )
                );
            }

            return workmatesViewStateItems;
        });


    }

}//end of WorkmatesViewModel