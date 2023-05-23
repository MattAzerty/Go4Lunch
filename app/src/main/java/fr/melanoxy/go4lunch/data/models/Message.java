package fr.melanoxy.go4lunch.data.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.Objects;

public class Message {

    private String message;
    private Date dateCreated;
    private String userName;

//no-argument constructor for deserialization
    public Message() {}

    public Message(
            String message,
            String userName
    ) {
        this.message = message;
        this.userName = userName;
    }


    // --- GETTERS ---
    public String getMessage() { return message; }
    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }
    public String getUserName() { return userName; }

    // --- SETTERS ---
    public void setMessage(String message) { this.message = message; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setUserName(String userName) { this.userName = userName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(message, message1.message) && Objects.equals(dateCreated, message1.dateCreated) && Objects.equals(userName, message1.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, dateCreated, userName);
    }
}//END of Message
