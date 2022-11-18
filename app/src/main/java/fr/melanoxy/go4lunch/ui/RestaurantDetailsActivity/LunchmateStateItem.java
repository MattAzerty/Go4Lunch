package fr.melanoxy.go4lunch.ui.RestaurantDetailsActivity;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.util.Objects;

public class LunchmateStateItem {

    private final String uid;
    private final String username;
    private final String avatarUrl;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LunchmateStateItem that = (LunchmateStateItem) o;
        return Objects.equals(uid, that.uid) && Objects.equals(username, that.username) && Objects.equals(avatarUrl, that.avatarUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, username, avatarUrl);
    }

    public LunchmateStateItem(String uid, String username, String avatarUrl) {
        this.uid = uid;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }

    public String getUid() {
        return uid;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getUsername() {
        return username;
    }

}//end of LunchmateStateItem
