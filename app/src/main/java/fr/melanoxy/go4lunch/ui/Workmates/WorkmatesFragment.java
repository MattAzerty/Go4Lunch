package fr.melanoxy.go4lunch.ui.Workmates;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.melanoxy.go4lunch.MainActivity;
import fr.melanoxy.go4lunch.R;
import fr.melanoxy.go4lunch.databinding.FragmentWorkmatesBinding;
import fr.melanoxy.go4lunch.ui.ListView.RestaurantStateItem;
import fr.melanoxy.go4lunch.ui.RestaurantDetailsActivity.RestaurantDetailsActivity;
import fr.melanoxy.go4lunch.utils.ViewModelFactory;

public class WorkmatesFragment extends Fragment {

    private FragmentWorkmatesBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //binding FragmentWorkmatesBinding layout
        mBinding = FragmentWorkmatesBinding.inflate(getLayoutInflater());

        // Inflate the layout for this fragment
        return mBinding.getRoot();//return view
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //StopListener
        mBinding = null;
        //reset query field if needed
        ((MainActivity)getActivity()).closeSearchView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


//ViewModel used for this fragment
        WorkmatesViewModel mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance())
                .get(WorkmatesViewModel.class);

//reset query field if needed
        ((MainActivity)getActivity()).closeSearchView();

//Init RecyclerView
        WorkmatesAdapter adapter = new WorkmatesAdapter(item -> {
            if(item.getPlace_id()!=null){
            RestaurantStateItem rItem = new RestaurantStateItem(
                    item.getPlace_id(),
                    item.getPlace_name(),
                    item.getPlace_address(),
                    "",
                    R.string.error_unknown_error,
                    1,
                    item.getPlace_pic_url(),
                    0);
            startActivity(RestaurantDetailsActivity.navigate(requireContext(), rItem));
            }else{((MainActivity)getActivity()).showSnackBar(getString(R.string.workmates_restaurant_not_set));}
        },getContext());

        mBinding.workmatesRv.setAdapter(adapter);


//link ViewStateItem to liveDataViewStateItem
        mViewModel.getViewStateLiveData().observe(getViewLifecycleOwner(), adapter::submitList
        );
    }




}//END