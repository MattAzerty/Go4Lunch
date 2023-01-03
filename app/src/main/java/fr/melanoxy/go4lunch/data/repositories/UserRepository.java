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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
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

    private static final String FIELD_NAME_FAV_RESTAURANTS = "my_favorite_restaurants";
    private User mUser;
    private ListenerRegistration mRegistration;
    private final MutableLiveData<User> connectedUserMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<User>> workmatesMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<User>> lunchmatesMutableLiveData = new MutableLiveData<>();

    public UserRepository() {
    }

    //Current user logged
    public FirebaseUser getCurrentUser() {
        return FirebaseHelper.getInstance().getCurrentUser();
    }

    @Nullable
    public User getUser() {
        return mUser;
    }

    //SignOut
    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }


// Create User in Firestore

    public void createUser() {
        //Current user logged
        FirebaseUser user = getCurrentUser();

        FirebaseHelper.getInstance().getUserDocumentReferenceOnFirestore(getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot userDocument = task.getResult();
                            if (userDocument.exists()) {
                                mUser = userDocument.toObject(User.class);
                                connectedUserMutableLiveData.setValue(mUser);
                            } else {//if no document in firestore we create it
                                String urlPicture;
                                if (user.getPhotoUrl() != null)
                                    urlPicture = user.getPhotoUrl().toString();
                                else urlPicture = UserRepository.this.generateAvatarUrl();
                                String username = user.getDisplayName();
                                String uid = user.getUid();
                                String email = user.getEmail();
                                String place_id = null;
                                String place_name = null;
                                String place_address = null;
                                String place_pic_url = null;
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
                                FirebaseHelper.getInstance().storeUserOnFirestore(mUser);
                                connectedUserMutableLiveData.setValue(mUser);
                            }
                        } else {
                            connectedUserMutableLiveData.setValue(null);//In case of Error
                        }
                    }
                });
    }

    //Update lunch for today information on user document
    public void updateTodayRestaurantUser(
            String restaurant_for_today_id,
            String restaurant_for_today_name,
            String restaurant_for_today_address,
            String restaurant_for_today_pic_url
    ) {

        FirebaseUser user = getCurrentUser();

        //Case when this exact restaurant was already stored in Firestore, this mean we remove the bookmark
        if (Objects.equals(restaurant_for_today_id, mUser.restaurant_for_today_id)) {
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
        FirebaseHelper.getInstance().getUserDocumentReferenceOnFirestore(user.getUid())
                .update(
                        "restaurant_for_today_id", restaurant_for_today_id,
                        "restaurant_for_today_name", restaurant_for_today_name,
                        "restaurant_for_today_address", restaurant_for_today_address,
                        "restaurant_for_today_pic_url", restaurant_for_today_pic_url
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

        DocumentReference userRef = FirebaseHelper.getInstance().getWorkmateCollection().document(getCurrentUser().getUid());

        List<String> updatedList = mUser.my_favorite_restaurants;

        if (mUser.my_favorite_restaurants.contains(place_id)) {
            // Atomically remove a region from the "regions" array field.
            userRef.update(FIELD_NAME_FAV_RESTAURANTS, FieldValue.arrayRemove(place_id));

            updatedList.remove(place_id);
        } else {
            // Atomically add a new region to the "regions" array field.
            userRef.update(FIELD_NAME_FAV_RESTAURANTS, FieldValue.arrayUnion(place_id));
            updatedList.add(place_id);
        }
        mUser.setMy_favorite_restaurants(updatedList);
        connectedUserMutableLiveData.setValue(mUser);
    }

    // Check if user is logged on Firebase
    public Boolean isUserAuthenticatedInFirebase() {
        boolean userLogged;
        //check firebase response
        FirebaseUser user = getCurrentUser();

        if (user != null) {
            userLogged = true;
            addConnectedUserToLiveData(user.getUid());
        } else {
            userLogged = false;
        }
        return userLogged;
    }

    private void addConnectedUserToLiveData(String uid) {

        FirebaseHelper.getInstance().getUserDocumentReferenceOnFirestore(uid)
                .get()
                .addOnCompleteListener(userTask -> {
                    if (userTask.isSuccessful()) {
                        DocumentSnapshot document = userTask.getResult();
                        if (document.exists()) {
                            mUser = document.toObject(User.class);
                            connectedUserMutableLiveData.setValue(mUser);
                        }
                    } else {
                        //logErrorMessage(userTask.getException().getMessage());TODO handle error signout
                    }
                });
    }

    public LiveData<User> getConnectedUserLiveData() {
        return connectedUserMutableLiveData;
    }

    //--------------------------For notification (sync task!)

    public User getDataUser(String uid) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = com.google.android.gms.tasks.Tasks.await(FirebaseHelper.getInstance().getUserDocumentReferenceOnFirestore(uid).get());
        mUser = doc.toObject(User.class);
        return mUser;//return user document
    }


    public List<String> getLunchmates(String placeId) throws ExecutionException, InterruptedException {

        ArrayList<String> lunchmates = new ArrayList<>();

        QuerySnapshot documents = Tasks.await(
                FirebaseHelper.getInstance().getWorkmateCollection()
                        .whereEqualTo("restaurant_for_today_id", placeId)
                        .get());

        for (DocumentSnapshot document : documents.getDocuments()) {
            lunchmates.add((document.toObject(User.class)).getUsername());
        }

        return lunchmates;//return a list of lunchmates name(s)
    }
    //---------------------------------------------


    //return list of User "luchmates" eating at "place_id"
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

    //return list of User "workmates" stored on Firestore
    public MutableLiveData<List<User>> getWorkmates() {

        FirebaseHelper.getInstance().getWorkmateCollection()
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        workmatesMutableLiveData.postValue(new ArrayList<>());
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
    private String generateAvatarUrl() {//for email login creation
        return "https://i.pravatar.cc/200?u=" + System.currentTimeMillis();
    }

    //Upload an image (pfp) on FireStorage
    public UploadTask uploadImage(Uri imageUri) {
        String uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference("MyGo4LunchAvatar/" + uuid);
        return mImageRef.putFile(imageUri);
    }

    // Update user settings
    public void updateUserSettings(Boolean notified, String urlPicture, String username) {

        DocumentReference userRef = FirebaseHelper.getInstance().getWorkmateCollection().document(getCurrentUser().getUid());
        userRef
                .update(
                        "notified", notified,
                        "urlPicture", urlPicture,
                        "username", username

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
