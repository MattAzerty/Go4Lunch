package fr.melanoxy.go4lunch.ui.MapView;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import fr.melanoxy.go4lunch.R;
import fr.melanoxy.go4lunch.databinding.FragmentMapViewBinding;
import fr.melanoxy.go4lunch.ui.Workmates.WorkmatesViewModel;
import fr.melanoxy.go4lunch.utils.ViewModelFactory;


public class MapViewFragment extends Fragment implements
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback {

    private MapViewViewModel mMapViewViewModel;
    private FragmentMapViewBinding mFragmentMapViewBinding;
    private Marker myPositionMaker;

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

    // Get a handle to the map fragment by calling FragmentManager.findFragmentById(). Then use getMapAsync() to register for the map callback:
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //ViewModel used for this fragment
        mMapViewViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance())
                .get(MapViewViewModel.class);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMapViewViewModel.getUserLocationLiveData().observe(this, userLocation ->
                placeMyLocationMarker(googleMap, userLocation));

    }

    private void placeMyLocationMarker(@NonNull GoogleMap googleMap, Location userLocation) {

        if(userLocation!=null){
        // Add a marker in current user's location
        LatLng myLocation = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        if (myPositionMaker!=null){myPositionMaker.remove();}
        myPositionMaker = googleMap.addMarker(new MarkerOptions()
                .position(myLocation).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title(getString(R.string.my_position_marker)+"(lat:" + userLocation.getLatitude() + ", long:" + userLocation.getLongitude() + ")"));
        // and move the map's camera to the same location with a zoom of 15.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));

    }


}
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }
}//END of MapViewFragment