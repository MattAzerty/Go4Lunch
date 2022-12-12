package fr.melanoxy.go4lunch.data.repositories;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fr.melanoxy.go4lunch.data.FirebaseHelper;
import fr.melanoxy.go4lunch.data.models.Message;

public class ChatRepository {

    private final MutableLiveData<List<Message>> messagesMutableLiveData = new MutableLiveData<>();
    private static final String MESSAGE_COLLECTION = "messages";
    private static final String CHAT_COLLECTION = "chats";//if later individual chats needed
    private static final String CHAT_NAME = "main";

    public ChatRepository() {
    }

    //return the list of messages stored on Firestore
    public MutableLiveData<List<Message>> getMessagesLiveData() {

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

    public void createMessageForChat(String message, String username) {
        // Create the Message object
        Message userMessage = new Message(message, username);
        // Store Message to Firestore
        this.getChatCollection()
                .document(CHAT_NAME)
                .collection(MESSAGE_COLLECTION)
                .add(userMessage);
    }

    public CollectionReference getChatCollection(){
        return FirebaseHelper.getInstance().getChatCollection();
    }
}//END of ChatRepository
