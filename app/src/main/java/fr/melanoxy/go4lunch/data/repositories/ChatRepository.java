package fr.melanoxy.go4lunch.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fr.melanoxy.go4lunch.data.FirebaseHelper;
import fr.melanoxy.go4lunch.data.models.Message;

public class ChatRepository {

    public ChatRepository() {
    }

    //return the list of messages stored on Firestore
    public LiveData<List<Message>> getMessagesLiveData() {
        return FirebaseHelper.getInstance().getMessagesFromFirestore();
    }

    //message and username added in collection as document
    public void createMessageForChat(String message, String username) {
        FirebaseHelper.getInstance().createMessageOnFirestore(message, username);
    }


}//END of ChatRepository
