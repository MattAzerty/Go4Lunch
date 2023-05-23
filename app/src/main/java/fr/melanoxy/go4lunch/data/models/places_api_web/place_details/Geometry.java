
package fr.melanoxy.go4lunch.data.models.places_api_web.place_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geometry {

    @SerializedName("location")
    @Expose
    private Location mLocation;
    @SerializedName("viewport")
    @Expose
    private Viewport mViewport;

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public Viewport getViewport() {
        return mViewport;
    }

    public void setViewport(Viewport viewport) {
        mViewport = viewport;
    }

}
