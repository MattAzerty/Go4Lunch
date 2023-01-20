package fr.melanoxy.go4lunch.ui.RestaurantDetailsActivity;

import java.util.Objects;

public class RestaurantDetailsStateItem {
    private final String openingHours;
    private final String formattedPhoneNumber;
    private final String website;

    public RestaurantDetailsStateItem(String openingHours, String formattedPhoneNumber, String website) {
        this.openingHours = openingHours;
        this.formattedPhoneNumber = formattedPhoneNumber;
        this.website = website;
    }


    public String getOpeningHours() {
        return openingHours;
    }

    public String getFormattedPhoneNumber() {
        return formattedPhoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantDetailsStateItem that = (RestaurantDetailsStateItem) o;
        return Objects.equals(openingHours, that.openingHours) && Objects.equals(formattedPhoneNumber, that.formattedPhoneNumber) && Objects.equals(website, that.website);
    }

    @Override
    public int hashCode() {
        return Objects.hash(openingHours, formattedPhoneNumber, website);
    }

}//END of RestaurantDetailsStateItem
