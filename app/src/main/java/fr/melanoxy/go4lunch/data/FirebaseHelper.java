package fr.melanoxy.go4lunch.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseHelper {
    private static FirebaseHelper sFirebaseHelper;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String CHAT_COLLECTION = "chats";
    public final CollectionReference workmatesRef = db.collection("users");

    public static FirebaseHelper getInstance() {
        if (sFirebaseHelper == null) {
            sFirebaseHelper = new FirebaseHelper();
        }
        return sFirebaseHelper;
    }
    //Get Users Collection
    public CollectionReference getWorkmateCollection(){
        return workmatesRef;
    }
    //Get Messages Collection
    public CollectionReference getChatCollection(){
        return FirebaseFirestore.getInstance().collection(CHAT_COLLECTION);
    }
    //Get Users Collection
    public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

}
