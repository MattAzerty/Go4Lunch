package fr.melanoxy.go4lunch.data.repositories;

import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.internal.zzt;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.melanoxy.go4lunch.data.FirebaseHelper;
import fr.melanoxy.go4lunch.data.models.User;

public class UserRepository {

    private static final String COLLECTION_NAME = "users";
    private User mUser;
    private final MutableLiveData<User> connectedUserMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<User>> WorkmatesMutableLiveData = new MutableLiveData<>();

    public UserRepository() {
    }

    @Nullable
    public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    //SignOut
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
        DocumentReference userRef = FirebaseHelper.getInstance().getWorkmateCollection().document(user.getUid());

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot userDocument = task.getResult();
                    if (userDocument.exists()) {
                        mUser = userDocument.toObject(User.class);
                        connectedUserMutableLiveData.setValue(mUser);
                    } else {
                            String urlPicture;
                            if (user.getPhotoUrl() != null) urlPicture = user.getPhotoUrl().toString();
                            else urlPicture = generateAvatarUrl();
                            String username = user.getDisplayName();
                            String uid = user.getUid();
                            String email = user.getEmail();
                            String place_id = null;
                            String place_name = null;
                            String place_address =null;
                            // Create the User object
                            mUser = new User(uid, username, urlPicture, email, place_id, place_name, place_address);
                            // Store User to Firestore
                            UserRepository.this.getUsersCollection().document(uid).set(mUser);
                            connectedUserMutableLiveData.setValue(mUser);
                    }
                } else {
                    connectedUserMutableLiveData.setValue(null);//TODO handle error
                }
            }
        });

        /*List<? extends UserInfo> userInfos = user.getProviderData();
        UserInfo userinfo = userInfos.get(1);*/ //TODO pfp for facebook
        //FirebaseHelper.getInstance().getWorkmateCollection().whereEqualTo();

    }

    public void updateTodayRestaurantUser(
            String restaurant_for_today_id,
            String restaurant_for_today_name,
            String restaurant_for_today_address
    ) {

        FirebaseUser user = getCurrentUser();
        DocumentReference userRef = FirebaseHelper.getInstance().getWorkmateCollection().document(user.getUid());

        if (Objects.equals(restaurant_for_today_id, mUser.restaurant_for_today_id)){
            restaurant_for_today_id = null;
            restaurant_for_today_name = null;
            restaurant_for_today_address = null;
        }

        String finalRestaurant_for_today_id = restaurant_for_today_id;
        String finalRestaurant_for_today_name = restaurant_for_today_name;
        String finalRestaurant_for_today_address = restaurant_for_today_address;

        userRef
                .update(
                        "restaurant_for_today_id", restaurant_for_today_id,
                        "restaurant_for_today_name",restaurant_for_today_name,
                        "restaurant_for_today_address",restaurant_for_today_address

                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mUser.setRestaurant_for_today_id(finalRestaurant_for_today_id);
                        mUser.setRestaurant_for_today_name(finalRestaurant_for_today_name);
                        mUser.setRestaurant_for_today_address(finalRestaurant_for_today_address);
                        connectedUserMutableLiveData.setValue(mUser);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error updating document", e);TODO handle error
                    }
                });
    }

    //TODO add a restaurant to favorite list

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
                    mUser = document.toObject(User.class);
                    connectedUserMutableLiveData.setValue(mUser);
                }
            } else {
                //logErrorMessage(userTask.getException().getMessage());TODO handle error
            }
        });}


    public LiveData<User> getConnectedUserLiveData() {
        return connectedUserMutableLiveData;
    }

    public MutableLiveData<List<User>> getWorkmates() {

        FirebaseHelper.getInstance().getWorkmateCollection()
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            WorkmatesMutableLiveData.postValue(new ArrayList<>());//TODO handle error
                            return;
                        }

                        ArrayList<User> workmates = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("username") != null) {
                                workmates.add(doc.toObject(User.class));
                            }
                        }
                        WorkmatesMutableLiveData.postValue(workmates);
                    }
                });

        return WorkmatesMutableLiveData;
    }

    @NonNull
    private String generateAvatarUrl() {
        return "https://i.pravatar.cc/200?u=" + System.currentTimeMillis();
    }

}// END UserRepository
