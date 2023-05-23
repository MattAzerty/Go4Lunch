
package fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RestaurantsNearbyResponse {

    @SerializedName("html_attributions")
    @Expose
    private List<Object> mHtmlAttributions;
    @SerializedName("next_page_token")
    @Expose
    private String mNextPageToken;
    @SerializedName("results")
    @Expose
    private List<NearbyResult> mResults;
    @SerializedName("status")
    @Expose
    private String mStatus;

    public List<Object> getHtmlAttributions() {
        return mHtmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        mHtmlAttributions = htmlAttributions;
    }

    public String getNextPageToken() {
        return mNextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        mNextPageToken = nextPageToken;
    }

    public List<NearbyResult> getResults() {
        return mResults;
    }

    public void setResults(List<NearbyResult> results) {
        mResults = results;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

}
