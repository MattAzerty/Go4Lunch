package fr.melanoxy.go4lunch.ui.MapView;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NONE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import fr.melanoxy.go4lunch.R;
import fr.melanoxy.go4lunch.data.models.Result;
import fr.melanoxy.go4lunch.databinding.FragmentMapViewBinding;
import fr.melanoxy.go4lunch.utils.ViewModelFactory;


public class MapViewFragment extends Fragment implements
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback {

    private MapViewViewModel mMapViewViewModel;
    private FragmentMapViewBinding mFragmentMapViewBinding;
    private Marker myPositionMaker;
    private GoogleMap mMap = null;
    private List<Result> restaurantsNearbyResults = new ArrayList<>();
    private final List<Marker> restaurantsMarker = new ArrayList<Marker>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

//BINDING --> FragmentWorkmates layout
        mFragmentMapViewBinding = FragmentMapViewBinding.inflate(getLayoutInflater());
        View view = mFragmentMapViewBinding.getRoot();

//GOOGLE MAP INIT
    //Get a handle to the map fragment by calling FragmentManager.findFragmentById().
       SupportMapFragment mMapFragment =
               (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    // Then use getMapAsync() to register for the map callback:
       mMapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//VIEW MODEL used for this fragment
        mMapViewViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance())
                .get(MapViewViewModel.class);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        //Adapt google map style to the application
        setSelectedStyle();
        //When userLocation is available, the map is centered on userLocation coordinates
        mMapViewViewModel.getUserLocationLiveData().observe(this, userLocation -> {

            if (userLocation == null) {
                mMap.setMapType(MAP_TYPE_NONE);//map is hidden on no location permission provided
            } else {
                mMap.setMapType(MAP_TYPE_NORMAL);//unveil the map

                MapViewFragment.this.addMyLocationMarker(mMap, userLocation);//Place userCurrentLocation
            }
        });

        //Observe restaurantNearbyList for any change
        mMapViewViewModel.getNearbyRestaurantsResults().observe(getViewLifecycleOwner(), restaurantsNearbyResults ->
                addNearbyRestaurants(mMap, restaurantsNearbyResults)//Place nearbyRestaurantsLocation associated
        );
    }

    private void setSelectedStyle() {
        MapStyleOptions style;
        style = MapStyleOptions.loadRawResourceStyle(getActivity(),R.raw.mapstyle_retro);
        mMap.setMapStyle(style);
    }

    private void addMyLocationMarker(@NonNull GoogleMap googleMap, Location userLocation) {

        if(userLocation!=null){
        // Add a marker in current user's location
        LatLng myLocation = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        if (myPositionMaker!=null){myPositionMaker.remove();}
        myPositionMaker = googleMap.addMarker(new MarkerOptions()
                .position(myLocation).icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .snippet("(lat:" + userLocation.getLatitude() + ", long:" + userLocation.getLongitude() + ")")
                .title(getString(R.string.my_position_marker)));
//MARKER listener
        //googleMap.setOnMarkerClickListener(this);
        // and move the map's camera to the same location with a zoom of 15.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));

    }
}

    private void addNearbyRestaurants(GoogleMap mMap, List<Result> restaurantsNearbyResults) {

        LatLngBounds.Builder builderBounds = new LatLngBounds.Builder();

        for (Result result : restaurantsNearbyResults) {

        Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(result.getGeometry().getLocation().getLat(),result.getGeometry().getLocation().getLng()))
                    .title(result.getName())
                    .snippet("Pam, Tom & Marl will eat here today")
                    .icon(vectorToBitmap(R.drawable.ic_restaurant_menu_white_24dp, getResources().getColor(R.color.primary)))
                    .infoWindowAnchor(0.5f, 0.5f));

        builderBounds.include(marker.getPosition());

        marker.setTag(result);
        restaurantsMarker.add(marker);

    }
// Set a listener for marker click.

        mMap.setOnInfoWindowClickListener(this);
        LatLngBounds bounds = builderBounds.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

    }

    /**
     * Demonstrates converting a {@link Drawable} to a {@link BitmapDescriptor},
     * for use as a marker icon.
     */
    private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Toast.makeText(getActivity(), "Click Info Marker", Toast.LENGTH_SHORT).show();
        return false;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {

        Result result = (Result) marker.getTag();

        String message = result.getName();

        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}//END of MapViewFragment