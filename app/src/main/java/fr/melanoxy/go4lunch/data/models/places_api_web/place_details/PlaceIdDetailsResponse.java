
package fr.melanoxy.go4lunch.data.models.places_api_web.place_details;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceIdDetailsResponse {

    @SerializedName("html_attributions")
    @Expose
    private List<Object> mHtmlAttributions;
    @SerializedName("result")
    @Expose
    private DetailsResult mDetailsResult;
    @SerializedName("status")
    @Expose
    private String mStatus;

    public List<Object> getHtmlAttributions() {
        return mHtmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        mHtmlAttributions = htmlAttributions;
    }

    public DetailsResult getResult() {
        return mDetailsResult;
    }

    public void setResult(DetailsResult detailsResult) {
        mDetailsResult = detailsResult;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

}
