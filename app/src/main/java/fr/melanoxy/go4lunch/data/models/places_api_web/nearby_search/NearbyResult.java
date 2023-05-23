
package fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NearbyResult {

    @SerializedName("business_status")
    @Expose
    private String mBusinessStatus;
    @SerializedName("formatted_address")
    @Expose
    private String mFormattedAddress;
    @SerializedName("geometry")
    @Expose
    private Geometry mGeometry;
    @SerializedName("icon")
    @Expose
    private String mIcon;
    @SerializedName("icon_background_color")
    @Expose
    private String mIconBackgroundColor;
    @SerializedName("icon_mask_base_uri")
    @Expose
    private String mIconMaskBaseUri;
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
    @SerializedName("plus_code")
    @Expose
    private PlusCode mPlusCode;
    @SerializedName("price_level")
    @Expose
    private Long mPriceLevel;
    @SerializedName("rating")
    @Expose
    private Double mRating;
    @SerializedName("reference")
    @Expose
    private String mReference;
    @SerializedName("types")
    @Expose
    private List<String> mTypes;
    @SerializedName("user_ratings_total")
    @Expose
    private Long mUserRatingsTotal;

    public String getBusinessStatus() {
        return mBusinessStatus;
    }

    public void setBusinessStatus(String businessStatus) {
        mBusinessStatus = businessStatus;
    }

    public String getFormattedAddress() {
        return mFormattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        mFormattedAddress = formattedAddress;
    }

    public Geometry getGeometry() {
        return mGeometry;
    }

    public void setGeometry(Geometry geometry) {
        mGeometry = geometry;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getIconBackgroundColor() {
        return mIconBackgroundColor;
    }

    public void setIconBackgroundColor(String iconBackgroundColor) {
        mIconBackgroundColor = iconBackgroundColor;
    }

    public String getIconMaskBaseUri() {
        return mIconMaskBaseUri;
    }

    public void setIconMaskBaseUri(String iconMaskBaseUri) {
        mIconMaskBaseUri = iconMaskBaseUri;
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

    public PlusCode getPlusCode() {
        return mPlusCode;
    }

    public void setPlusCode(PlusCode plusCode) {
        mPlusCode = plusCode;
    }

    public Long getPriceLevel() {
        return mPriceLevel;
    }

    public void setPriceLevel(Long priceLevel) {
        mPriceLevel = priceLevel;
    }

    public Double getRating() {
        return mRating;
    }

    public void setRating(Double rating) {
        mRating = rating;
    }

    public String getReference() {
        return mReference;
    }

    public void setReference(String reference) {
        mReference = reference;
    }

    public List<String> getTypes() {
        return mTypes;
    }

    public void setTypes(List<String> types) {
        mTypes = types;
    }

    public Long getUserRatingsTotal() {
        return mUserRatingsTotal;
    }

    public void setUserRatingsTotal(Long userRatingsTotal) {
        mUserRatingsTotal = userRatingsTotal;
    }

}
