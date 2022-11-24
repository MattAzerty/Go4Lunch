
package fr.melanoxy.go4lunch.data.models.places_api_web.place_details;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photo {

    @SerializedName("height")
    @Expose
    private Long mHeight;
    @SerializedName("html_attributions")
    @Expose
    private List<String> mHtmlAttributions;
    @SerializedName("photo_reference")
    @Expose
    private String mPhotoReference;
    @SerializedName("width")
    @Expose
    private Long mWidth;

    public Long getHeight() {
        return mHeight;
    }

    public void setHeight(Long height) {
        mHeight = height;
    }

    public List<String> getHtmlAttributions() {
        return mHtmlAttributions;
    }

    public void setHtmlAttributions(List<String> htmlAttributions) {
        mHtmlAttributions = htmlAttributions;
    }

    public String getPhotoReference() {
        return mPhotoReference;
    }

    public void setPhotoReference(String photoReference) {
        mPhotoReference = photoReference;
    }

    public Long getWidth() {
        return mWidth;
    }

    public void setWidth(Long width) {
        mWidth = width;
    }

}
