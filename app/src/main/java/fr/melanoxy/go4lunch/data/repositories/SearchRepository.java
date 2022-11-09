package fr.melanoxy.go4lunch.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SearchRepository {

    private final MutableLiveData<String> searchFieldMutableLiveData = new MutableLiveData<>();

    public SearchRepository() {
    }

    public LiveData<String> getSearchFieldLiveData() {
        return searchFieldMutableLiveData;
    }

    public void searchField(String query) {
        searchFieldMutableLiveData.setValue(query);
    }


}
