package fr.melanoxy.go4lunch.ui.ListView;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.melanoxy.go4lunch.databinding.FragmentListViewBinding;
import fr.melanoxy.go4lunch.ui.RestaurantDetailsActivity.RestaurantDetailsActivity;
import fr.melanoxy.go4lunch.utils.ViewModelFactory;


public class ListViewFragment extends Fragment {

    private FragmentListViewBinding mBinding;
    private ListViewViewModel mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //binding FragmentListViewBinding layout
        mBinding = FragmentListViewBinding.inflate(getLayoutInflater());
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
                .get(ListViewViewModel.class);

//Init RecyclerView
        ListViewAdapter adapter = new ListViewAdapter(item ->
                startActivity(RestaurantDetailsActivity.navigate(requireContext(), item)));

        mBinding.restaurantsRv.setAdapter(adapter);

//link ViewStateItem to liveDataViewStateItem
        mViewModel.getViewStateLiveData().observe(getViewLifecycleOwner(), adapter::submitList
        );
    }

}//END of ListViewFragment