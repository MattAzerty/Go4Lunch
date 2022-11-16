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
    //RestaurantForTodayFields
    @Nullable
    public String restaurant_for_today_id;
    @Nullable
    public String restaurant_for_today_name;
    @Nullable
    public String restaurant_for_today_address;

    public User() {}



    public User(

            String uid,
            String username,
            @Nullable String urlPicture,
            String email,
            @Nullable String restaurant_for_today_id,
            @Nullable String restaurant_for_today_name,
            @Nullable String restaurant_for_today_address

            ) {

        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.email = email;
        this.restaurant_for_today_id = restaurant_for_today_id;
        this.restaurant_for_today_name = restaurant_for_today_name;
        this.restaurant_for_today_address = restaurant_for_today_address;

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

    @Nullable
    public String getRestaurant_for_today_id() {
        return restaurant_for_today_id;
    }

    @Nullable
    public String getRestaurant_for_today_name() {
        return restaurant_for_today_name;
    }

    @Nullable
    public String getRestaurant_for_today_address() {
        return restaurant_for_today_address;
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

    public void setRestaurant_for_today_id(@Nullable String restaurant_for_today_id) {
        this.restaurant_for_today_id = restaurant_for_today_id;
    }

    public void setRestaurant_for_today_name(@Nullable String restaurant_for_today_name) {
        this.restaurant_for_today_name = restaurant_for_today_name;
    }

    public void setRestaurant_for_today_address(@Nullable String restaurant_for_today_address) {
        this.restaurant_for_today_address = restaurant_for_today_address;
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