package fr.melanoxy.go4lunch.data.repositories;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fr.melanoxy.go4lunch.data.FirebaseHelper;
import fr.melanoxy.go4lunch.data.models.User;

public class UserRepository {

    private static final String COLLECTION_NAME = "users";
    private final MutableLiveData<User> connectedUserMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<User>> WorkmatesMutableLiveData = new MutableLiveData<>();

    @Nullable
    public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public Task<Void> signOut(Context context){
        return AuthUI.getInstance().signOut(context);
    }

    // Get the Collection Reference
    private CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Create User in Firestore
    public void createUser() {
        FirebaseUser user = getCurrentUser();
        if(user != null){
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String username = user.getDisplayName();
            String uid = user.getUid();
            String email = user.getEmail();
            // Create the User object
            User userToCreate = new User(uid, username, urlPicture, email);
            // Store User to Firestore
            UserRepository.this.getUsersCollection().document(uid).set(userToCreate);
            connectedUserMutableLiveData.setValue(userToCreate);
        }
    }
    // Check if user is logged on Firebase
    public Boolean isUserAuthenticatedInFirebase() {
        Boolean userLogged;
        //check firebase response
        FirebaseUser user = getCurrentUser();

        if (user != null) {
           userLogged = true;
           addConnectedUserToLiveData(user.getUid());
        }
        else {
            userLogged = false;
        }
        return userLogged;
    }

    private void addConnectedUserToLiveData(String uid) {

        getUsersCollection().document(uid).get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful()) {
                DocumentSnapshot document = userTask.getResult();
                if(document.exists()) {
                    User user = document.toObject(User.class);
                    connectedUserMutableLiveData.setValue(user);
                }
            } else {
                //logErrorMessage(userTask.getException().getMessage());TODO handle error
            }
        });}


    public LiveData<User> getConnectedUserLiveData() {
        return connectedUserMutableLiveData;
    }


    public MutableLiveData<List<User>> getWorkmates() {
        FirebaseHelper.getInstance().getAllWorkmates().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<User> workmates = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    workmates.add(document.toObject(User.class));
                }
                WorkmatesMutableLiveData.postValue(workmates);
            } else {
                Log.e("Error", "Error getting documents: ", task.getException());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //handle error
                WorkmatesMutableLiveData.postValue(new ArrayList<>());//TODO: Create a more interesting null error
            }
        });
        return WorkmatesMutableLiveData;
    }






}// END UserRepository
