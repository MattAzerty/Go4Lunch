
package fr.melanoxy.go4lunch.data.models.places_api_web.place_autocomplete;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceAutocompleteResponse {

    @SerializedName("predictions")
    @Expose
    private List<Prediction> mPredictions;
    @SerializedName("status")
    @Expose
    private String mStatus;

    public List<Prediction> getPredictions() {
        return mPredictions;
    }

    public void setPredictions(List<Prediction> predictions) {
        mPredictions = predictions;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

}
