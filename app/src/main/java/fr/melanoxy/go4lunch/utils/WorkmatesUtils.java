package fr.melanoxy.go4lunch.utils;

import java.util.List;
import java.util.Objects;

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
    //TODO Color of workmates

}
