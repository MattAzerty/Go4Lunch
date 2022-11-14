package fr.melanoxy.go4lunch.ui.ListView;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class RestaurantStateItem implements Serializable {
    private final String place_id;
    @NonNull
    private final String place_name;
    @NonNull
    private final String place_address;
    @NonNull
    private final String place_openhour;
    @NonNull
    private final String place_preview_pic_url;

    public RestaurantStateItem(
            @NonNull String place_id,
            @NonNull String place_name,
            @NonNull String place_address,
            @NonNull String place_openhour,
            @NonNull String place_preview_pic_url) {
        this.place_id = place_id;
        this.place_name = place_name;
        this.place_address = place_address;
        this.place_openhour = place_openhour;
        this.place_preview_pic_url = place_preview_pic_url;
    }

    public String getPlace_id() {
        return place_id;
    }

    @NonNull
    public String getPlace_name() {
        return place_name;
    }

    @NonNull
    public String getPlace_address() {
        return place_address;
    }

    @NonNull
    public String getPlace_openhour() {
        return place_openhour;
    }

    @NonNull
    public String getPlace_preview_pic_url() {
        return place_preview_pic_url;
    }

    // Les fonctions equals(), hashcode() et sont utiles pour les tests unitaires (dans les assertions)
    // et peuvent être autogénérées avec Alt + Inser

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantStateItem that = (RestaurantStateItem) o;
        return Objects.equals(place_id, that.place_id) && place_name.equals(that.place_name) && place_address.equals(that.place_address) && place_openhour.equals(that.place_openhour) && place_preview_pic_url.equals(that.place_preview_pic_url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(place_id, place_name, place_address, place_openhour, place_preview_pic_url);
    }


}//END of RestaurantStateItem
