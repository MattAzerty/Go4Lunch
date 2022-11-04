package fr.melanoxy.go4lunch.ui.Workmates;

import com.google.firebase.firestore.DocumentSnapshot;

public interface OnWorkmateSelectedListener {

    void onWorkmateSelected(DocumentSnapshot workmate);

}