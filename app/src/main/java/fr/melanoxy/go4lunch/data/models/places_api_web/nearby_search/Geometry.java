
package fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geometry {

    @SerializedName("location")
    @Expose
    private NearbyPlaceLocation mNearbyPlaceLocation;
    @SerializedName("viewport")
    @Expose
    private Viewport mViewport;

    public NearbyPlaceLocation getLocation() {
        return mNearbyPlaceLocation;
    }

    public void setLocation(NearbyPlaceLocation nearbyPlaceLocation) {
        mNearbyPlaceLocation = nearbyPlaceLocation;
    }

    public Viewport getViewport() {
        return mViewport;
    }

    public void setViewport(Viewport viewport) {
        mViewport = viewport;
    }

}
