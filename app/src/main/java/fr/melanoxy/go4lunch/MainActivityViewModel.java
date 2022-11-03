package fr.melanoxy.go4lunch;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;

import java.util.Date;

import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;

public class MainActivityViewModel extends ViewModel {
//INIT
    //Injected with the ViewModelFactory
    @NonNull
    private final UserRepository userRepository;
    public LiveData<User> userLiveData;

//CONSTRUCTOR
public MainActivityViewModel(@NonNull UserRepository userRepository) {
    this.userRepository = userRepository;

    //LiveData<User> userLiveData = userRepository.getConnectedUserLiveData();

}

//Ask repo to check on firebase if the user instance exist
    public Boolean isUserAuthenticated() {
        return userRepository.isUserAuthenticatedInFirebase();
    }
//On User authentification success create him on firestore base
    public void onUserLoggedSuccess() {
        userRepository.createUser();
    }

    public Task<Void> onSignOut(Context context){
        return userRepository.signOut(context);
    }

    public LiveData<User> getConnectedUserLiveData() {
        return userRepository.getConnectedUserLiveData();
    }

}//END
