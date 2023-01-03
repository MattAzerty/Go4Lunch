package fr.melanoxy.go4lunch.ui.Workmates;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.melanoxy.go4lunch.R;
import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.repositories.SearchRepository;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;

public class WorkmatesViewModel extends ViewModel {

    private final Application application;

    private final MediatorLiveData<List<WorkmatesStateItem>> workmatesMediatorLiveData = new MediatorLiveData<>();

    //CONSTRUCTOR
    public WorkmatesViewModel(
            @NonNull UserRepository userRepository,
            @NonNull SearchRepository searchRepository,
            Application application) {

        //INIT
        //Injected with the ViewModelFactory
        this.application = application;

        LiveData<List<User>> workmatesLiveData = userRepository.getWorkmates();
        LiveData<String> queryLiveData = searchRepository.getSearchFieldLiveData();

        workmatesMediatorLiveData.addSource(workmatesLiveData, workmates ->
                combine(workmates, queryLiveData.getValue()));

        workmatesMediatorLiveData.addSource(queryLiveData, query ->
                combine(workmatesLiveData.getValue(), query));

    }

    private void combine(
            @Nullable final List<User> workmates,
            @Nullable String query
    ) {

        // Filter workmates with query parameter
        List<User> filteredWorkmates = getFilteredWorkmates(workmates, query);

        // map on the ViewStateItem
        List<WorkmatesStateItem> workmatesStateItem = new ArrayList<>();
        for (User filteredWorkmate : filteredWorkmates) {
            workmatesStateItem.add(mapWorkmate(filteredWorkmate));
        }
        workmatesMediatorLiveData.setValue(workmatesStateItem);
    }

    @NonNull
    private List<User> getFilteredWorkmates(
            @Nullable final List<User> workmates,
            @Nullable String query
    ) {
        List<User> filteredWorkmates = new ArrayList<>();

        if (workmates == null) {
            return filteredWorkmates;
        }
        if (query == null) {
            return workmates;
        }

        for (User user : workmates) {
            if (user.getUsername().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))) {
                filteredWorkmates.add(user);
            }
        }

        return filteredWorkmates;
    }

    // This is here we transform the "raw data" (User) into a "user pleasing" view model (ViewStateItem)
    @NonNull
    private WorkmatesStateItem mapWorkmate(@NonNull User user) {

        String place_name;
        if (user.restaurant_for_today_name != null) {
            place_name = application.getString(R.string.workmates_eating_at) + user.restaurant_for_today_name;
        } else {
            place_name = application.getString(R.string.workmates_restaurant_not_set);
        }

        return new WorkmatesStateItem(
                user.uid,
                "- " + user.username,
                "." + user.email,
                user.getUrlPicture(),
                place_name + ".",
                user.getRestaurant_for_today_id(),
                user.restaurant_for_today_name,
                user.getRestaurant_for_today_address(),
                user.getRestaurant_for_today_pic_url()
        );
    }

    public LiveData<List<WorkmatesStateItem>> getViewStateLiveData() {
        return workmatesMediatorLiveData;
    }

}//end of WorkmatesViewModel