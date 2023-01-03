package fr.melanoxy.go4lunch.data;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fr.melanoxy.go4lunch.data.models.Message;
import fr.melanoxy.go4lunch.data.models.User;

public class FirebaseHelper {
    private static FirebaseHelper sFirebaseHelper;
    private User mUser;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String MESSAGE_COLLECTION = "messages";
    private static final String CHAT_COLLECTION = "chats";//if later individual chats needed
    private static final String CHAT_NAME = "main";
    public final CollectionReference workmatesRef = db.collection("users");
    private final MutableLiveData<List<Message>> messagesMutableLiveData = new MutableLiveData<>();

    public static FirebaseHelper getInstance() {
        if (sFirebaseHelper == null) {
            sFirebaseHelper = new FirebaseHelper();
        }
        return sFirebaseHelper;
    }

    //Get Users Collection
    public CollectionReference getWorkmateCollection() {
        return workmatesRef;
    }

    //Get Messages Collection
    public CollectionReference getChatCollection() {
        return FirebaseFirestore.getInstance().collection(CHAT_COLLECTION);
    }

    //create message on Firestore
    public void createMessageOnFirestore(String message, String username) {
        // Create the Message object
        Message userMessage = new Message(message, username);
        // Store Message to Firestore
        this.getChatCollection()
                .document(CHAT_NAME)
                .collection(MESSAGE_COLLECTION)
                .add(userMessage);
    }

    //return the list of messages stored on Firestore
    public MutableLiveData<List<Message>> getMessagesFromFirestore() {

        FirebaseHelper.getInstance().getChatCollection()
                .document(CHAT_NAME)
                .collection(MESSAGE_COLLECTION)
                .orderBy("dateCreated")
                .limit(50)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        messagesMutableLiveData.postValue(new ArrayList<>());
                        return;
                    }
                    ArrayList<Message> messages = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("message") != null) {
                            messages.add(doc.toObject(Message.class));
                        }
                    }
                    messagesMutableLiveData.postValue(messages);
                });

        return messagesMutableLiveData;
    }

    //Get Users Collection
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    //Get User DocumentReference for Firestore
    public DocumentReference getUserDocumentReferenceOnFirestore(String userUid) {
        return FirebaseHelper.getInstance().getWorkmateCollection().document(userUid);
    }


    public void storeUserOnFirestore(User user) {
        getWorkmateCollection().document(user.getUid()).set(user);
    }
}//END of FirebaseHelper
