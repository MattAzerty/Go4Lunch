package fr.melanoxy.go4lunch.data.models;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;
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
    @Nullable
    public String restaurant_for_today_pic_url;
    //fav restaurants list
    @Nullable
    public List<String> my_favorite_restaurants;
    //Settings parameter
    public Boolean notified;

    //no-argument constructor for deserialization
    public User() {}

    public User(

            String uid,
            String username,
            @Nullable String urlPicture,
            String email,
            @Nullable String restaurant_for_today_id,
            @Nullable String restaurant_for_today_name,
            @Nullable String restaurant_for_today_address,
            @Nullable String restaurant_for_today_pic_url,
            @Nullable List<String> my_favorite_restaurants,
            Boolean notified

            ) {

        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.email = email;
        this.restaurant_for_today_id = restaurant_for_today_id;
        this.restaurant_for_today_name = restaurant_for_today_name;
        this.restaurant_for_today_address = restaurant_for_today_address;
        this.restaurant_for_today_pic_url = restaurant_for_today_pic_url;
        this.my_favorite_restaurants = my_favorite_restaurants;
        this.notified = notified;

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

    @Nullable
    public List<String> getMy_favorite_restaurants() {
        return my_favorite_restaurants;
    }

    @Nullable
    public String getRestaurant_for_today_pic_url() {
        return restaurant_for_today_pic_url;
    }

    public Boolean getNotified() {
        return notified;
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

    public void setMy_favorite_restaurants(@Nullable List<String> my_favorite_restaurants) {
        this.my_favorite_restaurants = my_favorite_restaurants;
    }

    public void setRestaurant_for_today_pic_url(@Nullable String restaurant_for_today_pic_url) {
        this.restaurant_for_today_pic_url = restaurant_for_today_pic_url;
    }

    public void setNotified(Boolean notified) {
        this.notified = notified;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(uid, user.uid) && Objects.equals(username, user.username) && Objects.equals(urlPicture, user.urlPicture) && Objects.equals(email, user.email) && Objects.equals(restaurant_for_today_id, user.restaurant_for_today_id) && Objects.equals(restaurant_for_today_name, user.restaurant_for_today_name) && Objects.equals(restaurant_for_today_address, user.restaurant_for_today_address) && Objects.equals(restaurant_for_today_pic_url, user.restaurant_for_today_pic_url) && Objects.equals(my_favorite_restaurants, user.my_favorite_restaurants) && Objects.equals(notified, user.notified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, username, urlPicture, email, restaurant_for_today_id, restaurant_for_today_name, restaurant_for_today_address, restaurant_for_today_pic_url, my_favorite_restaurants, notified);
    }

}//END of user