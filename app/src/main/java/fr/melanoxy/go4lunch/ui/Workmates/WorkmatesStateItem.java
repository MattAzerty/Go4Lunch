package fr.melanoxy.go4lunch.ui.Workmates;

import androidx.annotation.NonNull;

import java.util.Objects;

//ViewStateItem: contain all 'dynamic' data of the view

public class WorkmatesStateItem {

    private final String uid;
    private final String username;
    @NonNull
    private final String email;
    private final String avatarUrl;
    private final String mainField;
    private final String place_id;
    private final String place_name;
    private final String place_address;
    private final String place_pic_url;




    public WorkmatesStateItem(
            String uid,
            String username,
            @NonNull String email,
            String avatarUrl,
            String mainField,
            String place_id,
            String place_name,
            String place_address,
            String place_pic_url
    ){

        this.uid = uid;
        this.username = username;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.mainField = mainField;
        this.place_id = place_id;
        this.place_name = place_name;
        this.place_address = place_address;
        this.place_pic_url = place_pic_url;
    }

//GETTERS

    public String getUid() {
        return uid;
    }
    public String getUsername() {
        return username;
    }
    @NonNull
    public String getEmail() {
        return email;
    }
    public String getAvatarUrl() {
        return avatarUrl;
    }
    public String getPlace_name() {
        return place_name;
    }
    public String getPlace_id() {
        return place_id;
    }
    public String getPlace_address() {
        return place_address;
    }
    public String getMainField() {
        return mainField;
    }
    public String getPlace_pic_url() {
        return place_pic_url;
    }

    //generated

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkmatesStateItem that = (WorkmatesStateItem) o;
        return Objects.equals(uid, that.uid) && Objects.equals(username, that.username) && email.equals(that.email) && Objects.equals(avatarUrl, that.avatarUrl) && Objects.equals(mainField, that.mainField) && Objects.equals(place_id, that.place_id) && Objects.equals(place_name, that.place_name) && Objects.equals(place_address, that.place_address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, username, email, avatarUrl, mainField, place_id, place_name, place_address);
    }


}