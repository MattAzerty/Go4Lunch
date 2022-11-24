
package fr.melanoxy.go4lunch.data.models.places_api_web.place_details;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DetailsResult {

    @SerializedName("formatted_address")
    @Expose
    private String mFormattedAddress;
    @SerializedName("formatted_phone_number")
    @Expose
    private String mFormattedPhoneNumber;
    @SerializedName("geometry")
    @Expose
    private Geometry mGeometry;
    @SerializedName("name")
    @Expose
    private String mName;
    @SerializedName("opening_hours")
    @Expose
    private OpeningHours mOpeningHours;
    @SerializedName("photos")
    @Expose
    private List<Photo> mPhotos;
    @SerializedName("place_id")
    @Expose
    private String mPlaceId;
    @SerializedName("rating")
    @Expose
    private Double mRating;
    @SerializedName("website")
    @Expose
    private String mWebsite;

    public String getFormattedAddress() {
        return mFormattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        mFormattedAddress = formattedAddress;
    }

    public String getFormattedPhoneNumber() {
        return mFormattedPhoneNumber;
    }

    public void setFormattedPhoneNumber(String formattedPhoneNumber) {
        mFormattedPhoneNumber = formattedPhoneNumber;
    }

    public Geometry getGeometry() {
        return mGeometry;
    }

    public void setGeometry(Geometry geometry) {
        mGeometry = geometry;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public OpeningHours getOpeningHours() {
        return mOpeningHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        mOpeningHours = openingHours;
    }

    public List<Photo> getPhotos() {
        return mPhotos;
    }

    public void setPhotos(List<Photo> photos) {
        mPhotos = photos;
    }

    public String getPlaceId() {
        return mPlaceId;
    }

    public void setPlaceId(String placeId) {
        mPlaceId = placeId;
    }

    public Double getRating() {
        return mRating;
    }

    public void setRating(Double rating) {
        mRating = rating;
    }

    public String getWebsite() {
        return mWebsite;
    }

    public void setWebsite(String website) {
        mWebsite = website;
    }

}
