package fr.melanoxy.go4lunch.ui.MapView;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import fr.melanoxy.go4lunch.databinding.FragmentMapViewBinding;


public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private MapViewViewModel mMapViewViewModel;
    private FragmentMapViewBinding mFragmentMapViewBinding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //binding FragmentWorkmatesBinding layout
        mFragmentMapViewBinding = FragmentMapViewBinding.inflate(getLayoutInflater());
        View view = mFragmentMapViewBinding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //mMapViewViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MapViewViewModel.class);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
}