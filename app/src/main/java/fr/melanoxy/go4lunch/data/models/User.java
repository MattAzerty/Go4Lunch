package fr.melanoxy.go4lunch.data.models;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    public String uid;
    public String username;
    @Nullable
    public String urlPicture;
    @SuppressWarnings("WeakerAccess")
    public String email;

    public User() {}

    public User(String uid, String username, @Nullable String urlPicture, String email) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.email = email;
    }

    // --- GETTERS ---
    @Nullable
    public String getUrlPicture() { return urlPicture; }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    // --- SETTERS ---
    public void setUrlPicture(@Nullable String urlPicture) { this.urlPicture = urlPicture; }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(uid, user.uid) && Objects.equals(username, user.username) && Objects.equals(urlPicture, user.urlPicture) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, username, urlPicture, email);
    }

}