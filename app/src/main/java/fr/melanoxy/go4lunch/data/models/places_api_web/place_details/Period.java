
package fr.melanoxy.go4lunch.data.models.places_api_web.place_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Period {

    @SerializedName("close")
    @Expose
    private Close mClose;
    @SerializedName("open")
    @Expose
    private Open mOpen;

    public Close getClose() {
        return mClose;
    }

    public void setClose(Close close) {
        mClose = close;
    }

    public Open getOpen() {
        return mOpen;
    }

    public void setOpen(Open open) {
        mOpen = open;
    }

}
