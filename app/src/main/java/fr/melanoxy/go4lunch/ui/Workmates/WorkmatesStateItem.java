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
    @NonNull
    private final String mainfield;


    public WorkmatesStateItem(
            String uid,
            String username,
            @NonNull String email,
            String avatarUrl,
            @NonNull String mainfield){

        this.uid = uid;
        this.username = username;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.mainfield = mainfield;
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

    @NonNull
    public String getMainfield() {
        return mainfield;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkmatesStateItem that = (WorkmatesStateItem) o;
        return Objects.equals(uid, that.uid) && Objects.equals(username, that.username) && email.equals(that.email) && Objects.equals(avatarUrl, that.avatarUrl) && mainfield.equals(that.mainfield);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, username, email, avatarUrl, mainfield);
    }


    // Les fonctions equals(), hashcode() et sont utiles pour les tests unitaires (dans les assertions)
    // et peuvent être autogénérées avec Alt + Inser

}