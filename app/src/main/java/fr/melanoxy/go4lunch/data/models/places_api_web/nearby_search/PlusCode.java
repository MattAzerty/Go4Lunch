
package fr.melanoxy.go4lunch.data.models.places_api_web.nearby_search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlusCode {

    @SerializedName("compound_code")
    @Expose
    private String mCompoundCode;
    @SerializedName("global_code")
    @Expose
    private String mGlobalCode;

    public String getCompoundCode() {
        return mCompoundCode;
    }

    public void setCompoundCode(String compoundCode) {
        mCompoundCode = compoundCode;
    }

    public String getGlobalCode() {
        return mGlobalCode;
    }

    public void setGlobalCode(String globalCode) {
        mGlobalCode = globalCode;
    }

}
