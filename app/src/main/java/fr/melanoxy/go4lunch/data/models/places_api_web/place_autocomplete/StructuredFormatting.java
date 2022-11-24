
package fr.melanoxy.go4lunch.data.models.places_api_web.place_autocomplete;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StructuredFormatting {

    @SerializedName("main_text")
    @Expose
    private String mMainText;
    @SerializedName("main_text_matched_substrings")
    @Expose
    private List<MainTextMatchedSubstring> mMainTextMatchedSubstrings;
    @SerializedName("secondary_text")
    @Expose
    private String mSecondaryText;

    public String getMainText() {
        return mMainText;
    }

    public void setMainText(String mainText) {
        mMainText = mainText;
    }

    public List<MainTextMatchedSubstring> getMainTextMatchedSubstrings() {
        return mMainTextMatchedSubstrings;
    }

    public void setMainTextMatchedSubstrings(List<MainTextMatchedSubstring> mainTextMatchedSubstrings) {
        mMainTextMatchedSubstrings = mainTextMatchedSubstrings;
    }

    public String getSecondaryText() {
        return mSecondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        mSecondaryText = secondaryText;
    }

}
