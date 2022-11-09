package fr.melanoxy.go4lunch.ui.Workmates;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.data.repositories.SearchRepository;
import fr.melanoxy.go4lunch.data.repositories.UserRepository;

public class WorkmatesViewModel extends ViewModel {
    //INIT
    //Injected with the ViewModelFactory
    @NonNull
    private final UserRepository userRepository;
    @NonNull
    private final SearchRepository searchRepository;

    private final MediatorLiveData<List<WorkmatesStateItem>> workmatesMediatorLiveData = new MediatorLiveData<>();

    //CONSTRUCTOR
    public WorkmatesViewModel(
            @NonNull UserRepository userRepository,
            @NonNull SearchRepository searchRepository
    ) {
        this.userRepository = userRepository;
        this.searchRepository = searchRepository;

        final LiveData<List<User>> workmatesLiveData = userRepository.getWorkmates();
        LiveData<String> queryLiveData = searchRepository.getSearchFieldLiveData();

        workmatesMediatorLiveData.addSource(workmatesLiveData, workmates ->
                combine(workmates, queryLiveData.getValue()));

        workmatesMediatorLiveData.addSource(queryLiveData, query ->
                combine(workmatesLiveData.getValue(), query));
    }

    private void combine(@Nullable final List<User> workmates, @Nullable String query) {

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

        if (workmates == null) {return filteredWorkmates;}
        if (workmates != null && query == null) {return workmates;}

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
        return new WorkmatesStateItem(
                user.uid,
                "- "+user.username,
                "."+user.email,
                user.getUrlPicture(),
                "i'm eating @La fourchette "
        );
    }
    // Getter typé en LiveData (et pas MediatorLiveData pour éviter la modification de la valeur de la LiveData dans la View)
    public LiveData<List<WorkmatesStateItem>> getViewStateLiveData() {
        return workmatesMediatorLiveData;
    }

}//end of WorkmatesViewModel