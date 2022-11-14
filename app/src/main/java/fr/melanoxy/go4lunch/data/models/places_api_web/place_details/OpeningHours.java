
package fr.melanoxy.go4lunch.data.models.places_api_web.place_details;

import android.text.TextUtils;

import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import fr.melanoxy.go4lunch.data.models.User;

public class OpeningHours {

    @SerializedName("open_now")
    @Expose
    private Boolean mOpenNow;
    @SerializedName("periods")
    @Expose
    private List<Period> mPeriods;
    @SerializedName("weekday_text")
    @Expose
    private List<String> mWeekdayText;

    public Boolean getOpenNow() {
        return mOpenNow;
    }

    public void setOpenNow(Boolean openNow) {
        mOpenNow = openNow;
    }

    public List<Period> getPeriods() {
        return mPeriods;
    }

    public void setPeriods(List<Period> periods) {
        mPeriods = periods;
    }

    public List<String> getWeekdayText() {
        return mWeekdayText;
    }

    public void setWeekdayText(List<String> weekdayText) {
        mWeekdayText = weekdayText;
    }

}
