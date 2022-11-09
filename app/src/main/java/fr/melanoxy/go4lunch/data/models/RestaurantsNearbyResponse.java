
package fr.melanoxy.go4lunch.data.models;

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
    private List<Result> mResults;
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

    public List<Result> getResults() {
        return mResults;
    }

    public void setResults(List<Result> results) {
        mResults = results;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

}
