package fr.melanoxy.go4lunch.ui.MapView;

import androidx.annotation.NonNull;

import java.util.Objects;

public class MarkerInfoStateItem {

    @NonNull
    private final String placeId;
    @NonNull
    private final String placeName;
    @NonNull
    private final String placeAddress;
    @NonNull
    private final String placePreviewPicUrl;
    @NonNull
    private final Integer numberOfLunchmates;
    @NonNull
    private final Double latitude;
    @NonNull
    private final Double longitude;


    public MarkerInfoStateItem(@NonNull String placeId, @NonNull String placeName, @NonNull String placeAddress, @NonNull String placePreviewPicUrl, @NonNull Integer numberOfLunchmates, @NonNull Double latitude, @NonNull Double longitude) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.placePreviewPicUrl = placePreviewPicUrl;
        this.numberOfLunchmates = numberOfLunchmates;
        this.latitude = latitude;
        this.longitude = longitude;
    }

//Getters

    @NonNull
    public String getPlaceId() {
        return placeId;
    }

    @NonNull
    public String getPlaceName() {
        return placeName;
    }

    @NonNull
    public String getPlaceAddress() {
        return placeAddress;
    }

    @NonNull
    public String getPlacePreviewPicUrl() {
        return placePreviewPicUrl;
    }

    @NonNull
    public Integer getNumberOfLunchmates() {
        return numberOfLunchmates;
    }

    @NonNull
    public Double getLatitude() {
        return latitude;
    }

    @NonNull
    public Double getLongitude() {
        return longitude;
    }

//For test units

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarkerInfoStateItem that = (MarkerInfoStateItem) o;
        return placeId.equals(that.placeId) && placeName.equals(that.placeName) && placeAddress.equals(that.placeAddress) && placePreviewPicUrl.equals(that.placePreviewPicUrl) && numberOfLunchmates.equals(that.numberOfLunchmates) && latitude.equals(that.latitude) && longitude.equals(that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, placeName, placeAddress, placePreviewPicUrl, numberOfLunchmates, latitude, longitude);
    }


}//END of MarkerInfoStateItem
