
package fr.melanoxy.go4lunch.data.models.places_api_web.place_autocomplete;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MatchedSubstring {

    @SerializedName("length")
    @Expose
    private Long mLength;
    @SerializedName("offset")
    @Expose
    private Long mOffset;

    public Long getLength() {
        return mLength;
    }

    public void setLength(Long length) {
        mLength = length;
    }

    public Long getOffset() {
        return mOffset;
    }

    public void setOffset(Long offset) {
        mOffset = offset;
    }

}
