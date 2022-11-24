
package fr.melanoxy.go4lunch.data.models.places_api_web.place_autocomplete;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Prediction {

    @SerializedName("description")
    @Expose
    private String mDescription;
    @SerializedName("matched_substrings")
    @Expose
    private List<MatchedSubstring> mMatchedSubstrings;
    @SerializedName("place_id")
    @Expose
    private String mPlaceId;
    @SerializedName("reference")
    @Expose
    private String mReference;
    @SerializedName("structured_formatting")
    @Expose
    private StructuredFormatting mStructuredFormatting;
    @SerializedName("terms")
    @Expose
    private List<Term> mTerms;
    @SerializedName("types")
    @Expose
    private List<String> mTypes;

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public List<MatchedSubstring> getMatchedSubstrings() {
        return mMatchedSubstrings;
    }

    public void setMatchedSubstrings(List<MatchedSubstring> matchedSubstrings) {
        mMatchedSubstrings = matchedSubstrings;
    }

    public String getPlaceId() {
        return mPlaceId;
    }

    public void setPlaceId(String placeId) {
        mPlaceId = placeId;
    }

    public String getReference() {
        return mReference;
    }

    public void setReference(String reference) {
        mReference = reference;
    }

    public StructuredFormatting getStructuredFormatting() {
        return mStructuredFormatting;
    }

    public void setStructuredFormatting(StructuredFormatting structuredFormatting) {
        mStructuredFormatting = structuredFormatting;
    }

    public List<Term> getTerms() {
        return mTerms;
    }

    public void setTerms(List<Term> terms) {
        mTerms = terms;
    }

    public List<String> getTypes() {
        return mTypes;
    }

    public void setTypes(List<String> types) {
        mTypes = types;
    }

}
