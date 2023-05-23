
package fr.melanoxy.go4lunch.data.models.places_api_web.place_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Viewport {

    @SerializedName("northeast")
    @Expose
    private Northeast mNortheast;
    @SerializedName("southwest")
    @Expose
    private Southwest mSouthwest;

    public Northeast getNortheast() {
        return mNortheast;
    }

    public void setNortheast(Northeast northeast) {
        mNortheast = northeast;
    }

    public Southwest getSouthwest() {
        return mSouthwest;
    }

    public void setSouthwest(Southwest southwest) {
        mSouthwest = southwest;
    }

}
