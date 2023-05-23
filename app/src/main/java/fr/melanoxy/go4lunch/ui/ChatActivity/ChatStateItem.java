package fr.melanoxy.go4lunch.ui.ChatActivity;

import java.util.Objects;

public class ChatStateItem {

    private final String message;
    private final String userName;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatStateItem that = (ChatStateItem) o;
        return Objects.equals(message, that.message) && Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, userName);
    }

    public ChatStateItem(String message, String userName) {
        this.message = message;
        this.userName = userName;
    }

    //GETTERS
    public String getMessage() {
        return message;
    }
    public String getUserName() {
        return userName;
    }



}
