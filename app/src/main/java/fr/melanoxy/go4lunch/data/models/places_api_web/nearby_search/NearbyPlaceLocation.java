
package fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import fr.melanoxy.go4lunch.data.models.places_api_web.place_details.Location;

public class NearbyPlaceLocation{

    @SerializedName("lat")
    @Expose
    private Double mLat;
    @SerializedName("lng")
    @Expose
    private Double mLng;

    public Double getLat() {
        return mLat;
    }

    public void setLat(Double lat) {
        mLat = lat;
    }

    public Double getLng() {
        return mLng;
    }

    public void setLng(Double lng) {
        mLng = lng;
    }

}
