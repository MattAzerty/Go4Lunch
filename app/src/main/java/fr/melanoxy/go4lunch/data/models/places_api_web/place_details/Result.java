
package fr.melanoxy.go4lunch.data.models.places_api_web.place_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("formatted_phone_number")
    @Expose
    private String mFormattedPhoneNumber;
    @SerializedName("opening_hours")
    @Expose
    private OpeningHours mOpeningHours;
    @SerializedName("website")
    @Expose
    private String mWebsite;

    public String getFormattedPhoneNumber() {
        return mFormattedPhoneNumber;
    }

    public void setFormattedPhoneNumber(String formattedPhoneNumber) {
        mFormattedPhoneNumber = formattedPhoneNumber;
    }

    public OpeningHours getOpeningHours() {
        return mOpeningHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        mOpeningHours = openingHours;
    }

    public String getWebsite() {
        return mWebsite;
    }

    public void setWebsite(String website) {
        mWebsite = website;
    }

}
