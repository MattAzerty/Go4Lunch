package fr.melanoxy.go4lunch.data.repositories;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import fr.melanoxy.go4lunch.data.FirebaseHelper;
import fr.melanoxy.go4lunch.data.models.User;

public class UserRepository {

    private static final String COLLECTION_NAME = "users";
    private static final String FIELD_NAME_FAV_RESTAURANTS = "my_favorite_restaurants";
    public User mUser;
    private ListenerRegistration mRegistration;
    private final MutableLiveData<User> connectedUserMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<User>> workmatesMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<User>> lunchmatesMutableLiveData = new MutableLiveData<>();

    public UserRepository() {
    }
//Current user logged
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
    //get User document ref firestore
        FirebaseUser user = getCurrentUser();
        DocumentReference userRef = FirebaseHelper.getInstance().getWorkmateCollection().document(user.getUid());

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot userDocument = task.getResult();
                if (userDocument.exists()) {
                    mUser = userDocument.toObject(User.class);
                    connectedUserMutableLiveData.setValue(mUser);
                } else {//if no document in firestore we create it
                        String urlPicture;
                        if (user.getPhotoUrl() != null) urlPicture = user.getPhotoUrl().toString();
                        else urlPicture = generateAvatarUrl();
                        String username = user.getDisplayName();
                        String uid = user.getUid();
                        String email = user.getEmail();
                        String place_id = null;
                        String place_name = null;
                        String place_address =null;
                        String place_pic_url =null;
                        List<String> my_favorite_restaurants = new ArrayList<>();
                        // Create the User object
                        mUser = new User(
                                uid,
                                username,
                                urlPicture,
                                email,
                                place_id,
                                place_name,
                                place_address,
                                place_pic_url,
                                my_favorite_restaurants,
                                true);//Notification true by default
                        // Store User to Firestore
                        UserRepository.this.getUsersCollection().document(uid).set(mUser);
                        connectedUserMutableLiveData.setValue(mUser);
                }
            } else {
                connectedUserMutableLiveData.setValue(null);//TODO handle error
            }
        });

        /*List<? extends UserInfo> userInfos = user.getProviderData();
        UserInfo userinfo = userInfos.get(1);*/ //TODO pfp for facebook
        //FirebaseHelper.getInstance().getWorkmateCollection().whereEqualTo();

    }

    public void updateTodayRestaurantUser(
            String restaurant_for_today_id,
            String restaurant_for_today_name,
            String restaurant_for_today_address,
            String restaurant_for_today_pic_url
    ) {

        FirebaseUser user = getCurrentUser();
        DocumentReference userRef = FirebaseHelper.getInstance().getWorkmateCollection().document(user.getUid());
//Case when this exact restaurant was already stored in Firestore
        if (Objects.equals(restaurant_for_today_id, mUser.restaurant_for_today_id)){
            restaurant_for_today_id = null;
            restaurant_for_today_name = null;
            restaurant_for_today_address = null;
            restaurant_for_today_pic_url = null;
        }

        String finalRestaurant_for_today_id = restaurant_for_today_id;
        String finalRestaurant_for_today_name = restaurant_for_today_name;
        String finalRestaurant_for_today_address = restaurant_for_today_address;
        String finalRestaurant_for_today_pic_url = restaurant_for_today_pic_url;

//update the restaurant for today info
        userRef
                .update(
                        "restaurant_for_today_id", restaurant_for_today_id,
                        "restaurant_for_today_name",restaurant_for_today_name,
                        "restaurant_for_today_address",restaurant_for_today_address,
                        "restaurant_for_today_pic_url",restaurant_for_today_pic_url

                )
                .addOnSuccessListener(aVoid -> {
                    //report those modifications to connectedUserLiveData
                    mUser.setRestaurant_for_today_id(finalRestaurant_for_today_id);
                    mUser.setRestaurant_for_today_name(finalRestaurant_for_today_name);
                    mUser.setRestaurant_for_today_address(finalRestaurant_for_today_address);
                    mUser.setRestaurant_for_today_pic_url(finalRestaurant_for_today_pic_url);
                    connectedUserMutableLiveData.postValue(mUser);
                })
                .addOnFailureListener(e -> {
                    //Log.w(TAG, "Error updating document", e);TODO handle error
                });
    }

    public void updateFavList(String place_id) {

        FirebaseUser user = getCurrentUser();
        DocumentReference userRef = FirebaseHelper.getInstance().getWorkmateCollection().document(user.getUid());

        List<String> updatedList = mUser.my_favorite_restaurants;

        if(mUser.my_favorite_restaurants.contains(place_id)){
            // Atomically remove a region from the "regions" array field.
            userRef.update(FIELD_NAME_FAV_RESTAURANTS, FieldValue.arrayRemove(place_id));

            updatedList.remove(place_id);
            mUser.setMy_favorite_restaurants(updatedList);
            connectedUserMutableLiveData.setValue(mUser);
        }else{
            // Atomically add a new region to the "regions" array field.
            userRef.update(FIELD_NAME_FAV_RESTAURANTS, FieldValue.arrayUnion(place_id));

            updatedList.add(place_id);
            mUser.setMy_favorite_restaurants(updatedList);
            connectedUserMutableLiveData.setValue(mUser);
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

    //--------------------------For notification

    public User getDataUser(String uid) throws ExecutionException, InterruptedException {

        //getUsersCollection().document(uid).get()
        DocumentSnapshot doc = com.google.android.gms.tasks.Tasks.await(getUsersCollection().document(uid).get());
        mUser = doc.toObject(User.class);
        return mUser;
    }


    public List<String> getLunchmates(String placeId) throws ExecutionException, InterruptedException {

        ArrayList<String> lunchmates = new ArrayList<>();

        //getUsersCollection().document(uid).get()
        QuerySnapshot documents = Tasks.await(
                FirebaseHelper.getInstance().getWorkmateCollection()
                        .whereEqualTo("restaurant_for_today_id", placeId)
                        .get());

        for (DocumentSnapshot document : documents.getDocuments()) {
            lunchmates.add((document.toObject(User.class)).getUsername());}

        return lunchmates;
    }

                                                        //---------------------------------------------

    public MutableLiveData<List<User>> getLunchmatesLiveData(String place_id) {

        Query query = FirebaseHelper.getInstance().getWorkmateCollection()
                .whereEqualTo("restaurant_for_today_id", place_id);

        mRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    lunchmatesMutableLiveData.postValue(new ArrayList<>());//TODO handle error
                    return;
                }
                ArrayList<User> lunchmates = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                        lunchmates.add(doc.toObject(User.class));
                }
                lunchmatesMutableLiveData.postValue(lunchmates);
            }
        });

        return lunchmatesMutableLiveData;

    }

    public void onEndOfDetailsActivity() {
        mRegistration.remove();
        lunchmatesMutableLiveData.setValue(new ArrayList<>());

    }

    public MutableLiveData<List<User>> getWorkmates() {

        FirebaseHelper.getInstance().getWorkmateCollection()
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        workmatesMutableLiveData.postValue(new ArrayList<>());//TODO handle error
                        return;
                    }
                    ArrayList<User> workmates = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("username") != null) {
                            workmates.add(doc.toObject(User.class));
                        }
                    }
                    workmatesMutableLiveData.postValue(workmates);
                });

        return workmatesMutableLiveData;
    }

    @NonNull
    private String generateAvatarUrl() {
        return "https://i.pravatar.cc/200?u=" + System.currentTimeMillis();
    }

    public UploadTask uploadImage(Uri imageUri){
        String uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference("MyGo4LunchAvatar/" + uuid);
        return mImageRef.putFile(imageUri);
    }


    // Update notification setting
    public void updateUserSettings(Boolean notified, String urlPicture, String username) {

        FirebaseUser user = getCurrentUser();
        DocumentReference userRef = FirebaseHelper.getInstance().getWorkmateCollection().document(user.getUid());
        userRef
                .update(
                        "notified", notified,
                        "urlPicture",urlPicture,
                        "username",username

                )
                .addOnSuccessListener(aVoid -> {
                    //report those modifications to connectedUserLiveData
                    mUser.setNotified(notified);
                    mUser.setUrlPicture(urlPicture);
                    mUser.setUsername(username);
                    connectedUserMutableLiveData.postValue(mUser);
                })
                .addOnFailureListener(e -> {
                    //Log.w(TAG, "Error updating document", e);TODO handle error
                });

    }

}// END UserRepository
