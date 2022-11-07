package fr.melanoxy.go4lunch.ui.Workmates;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;


import fr.melanoxy.go4lunch.data.models.User;
import fr.melanoxy.go4lunch.databinding.FragmentWorkmatesBinding;
import fr.melanoxy.go4lunch.utils.ViewModelFactory;

public class WorkmatesFragment extends Fragment {

    private FragmentWorkmatesBinding mBinding;
    private WorkmatesViewModel mViewModel;
    private WorkmatesAdapter mAdapter;
    private Query mQuery;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //binding FragmentWorkmatesBinding layout
        mBinding = FragmentWorkmatesBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


//ViewModel used for this fragment
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance())
                .get(WorkmatesViewModel.class);

//Init RecyclerView
        WorkmatesAdapter adapter = new WorkmatesAdapter(new OnWorkmateClickedListener() {
            @Override
            public void onWorkmateClicked(String uid) {
                Toast.makeText(getActivity(), "details about the lunch of my workmate", Toast.LENGTH_LONG).show();
            }
        });

        mBinding.workmatesRv.setAdapter(adapter);


//link ViewStateItem to liveDataViewStateItem
        mViewModel.getViewStateLiveData().observe(getViewLifecycleOwner(), workmatesViewStateItems ->
                adapter.submitList(workmatesViewStateItems)
        );


    }




}//END