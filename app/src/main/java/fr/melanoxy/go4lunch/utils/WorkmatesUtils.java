package fr.melanoxy.go4lunch.utils;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import fr.melanoxy.go4lunch.data.models.User;

public class WorkmatesUtils {

    private static WorkmatesUtils sWorkmatesUtils;

    public static WorkmatesUtils getInstance() {
        if (sWorkmatesUtils == null) {
            sWorkmatesUtils = new WorkmatesUtils();
        }
        return sWorkmatesUtils;
    }

    //NUMBER OF LUNCHMATES
    public Integer getNumberOfLunchmates(List<User> workmates, String placeId) {
        //Number of lunchmates
        Integer numberOfLunchmates =0;
        for (User user : workmates) {
            if (Objects.equals(user.getRestaurant_for_today_id(), placeId))
            {numberOfLunchmates++;}
        }
        return numberOfLunchmates;
    }

    public Integer convertStringToAssignedRandomColor(String username) {
        // Generates a random color from a given string

        // Create a random number generator
        Random rng = new Random(username.hashCode());

        // Generate random red, green, and blue values
        int red = rng.nextInt(256);
        int green = rng.nextInt(256);
        int blue = rng.nextInt(256);

        // Return the random color into Integer
        return android.graphics.Color.rgb(red , green, blue);

    }


}
