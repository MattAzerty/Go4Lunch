package fr.melanoxy.go4lunch.ui.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.io.Serializable;
import java.util.Objects;

public class RestaurantStateItem implements Serializable {
    @NonNull
    private final String place_id;
    @NonNull
    private final String place_name;
    @NonNull
    private final String place_address;
    @NonNull
    private final String place_distance;
    private final @StringRes int place_is_open;
    @NonNull
    private final Double place_rating;
    @NonNull
    private final String place_preview_pic_url;

    public RestaurantStateItem(
            @NonNull String place_id,
            @NonNull String place_name,
            @NonNull String place_address,
            @NonNull String place_distance,
            @StringRes int place_is_open,
            @NonNull double place_rating,
            @NonNull String place_preview_pic_url) {
        this.place_id = place_id;
        this.place_name = place_name;
        this.place_address = place_address;
        this.place_distance = place_distance;
        this.place_is_open = place_is_open;
        this.place_rating = place_rating;
        this.place_preview_pic_url = place_preview_pic_url;
    }

    @NonNull
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
    public String getPlace_distance() {
        return place_distance;
    }

    public @StringRes int getPlace_is_open() {
        return place_is_open;
    }

    @NonNull
    public Double getPlace_rating() {
        return place_rating;
    }

    @NonNull
    public String getPlace_preview_pic_url() {
        return place_preview_pic_url;
    }

    // For test units

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantStateItem that = (RestaurantStateItem) o;
        return place_is_open == that.place_is_open && Objects.equals(place_id, that.place_id) && place_name.equals(that.place_name) && place_address.equals(that.place_address) && place_rating.equals(that.place_rating) && place_preview_pic_url.equals(that.place_preview_pic_url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(place_id, place_name, place_address, place_is_open, place_rating, place_preview_pic_url);
    }


}//END of RestaurantStateItem
