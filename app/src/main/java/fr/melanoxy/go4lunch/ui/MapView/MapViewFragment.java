package fr.melanoxy.go4lunch.ui.MapView;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NONE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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
import java.util.Objects;

import fr.melanoxy.go4lunch.R;
import fr.melanoxy.go4lunch.databinding.FragmentMapViewBinding;
import fr.melanoxy.go4lunch.ui.ListView.RestaurantStateItem;
import fr.melanoxy.go4lunch.ui.RestaurantDetailsActivity.RestaurantDetailsActivity;
import fr.melanoxy.go4lunch.utils.ViewModelFactory;


public class MapViewFragment extends Fragment implements
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback {

    private MapViewViewModel mMapViewViewModel;
    private Marker myPositionMaker;
    private LatLngBounds mBounds = null;
    private GoogleMap mMap = null;
    private final List<Marker> restaurantsMarker = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

//BINDING --> FragmentWorkmates layout
        fr.melanoxy.go4lunch.databinding.FragmentMapViewBinding mFragmentMapViewBinding = FragmentMapViewBinding.inflate(getLayoutInflater());
        View view = mFragmentMapViewBinding.getRoot();

//GOOGLE MAP INIT
    //Get a handle to the map fragment by calling FragmentManager.findFragmentById().
       SupportMapFragment mMapFragment =
               (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    // Then use getMapAsync() to register for the map callback:
    Objects.requireNonNull(mMapFragment).getMapAsync(this);

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
                LatLng latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                MapViewFragment.this.addMyLocationMarker(mMap,latLng);//Place userCurrentLocation
            }
        });

        //Observe MarkerInfos for any change
        mMapViewViewModel.getMarkerInfosLiveData().observe(getViewLifecycleOwner(), markerInfos -> {

            if(!markerInfos.isEmpty()) {
                MapViewFragment.this.addNearbyRestaurants(mMap, markerInfos);
            }
        }
        );

        //TODO observe singleLiveEvent for center camera
    }

    private void setSelectedStyle() {
        MapStyleOptions style;
        style = MapStyleOptions.loadRawResourceStyle(getActivity(),R.raw.mapstyle_retro);
        mMap.setMapStyle(style);
    }

    private void addMyLocationMarker(@NonNull GoogleMap googleMap, LatLng latLng) {

        if (myPositionMaker!=null){
            myPositionMaker.remove();
        }
        myPositionMaker = googleMap.addMarker(new MarkerOptions()
                .position(latLng).icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .snippet("(lat:" + latLng.latitude + ", long:" + latLng.longitude + ")")
                .title(getString(R.string.my_position_marker)));

        // and move the map's camera to the same location with a zoom of 15.
        if (restaurantsMarker.isEmpty()) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
}

    private void addNearbyRestaurants(GoogleMap mMap, List<MarkerInfoStateItem> markerInfoStateItems) {

        if (!restaurantsMarker.isEmpty()){
            restaurantsMarker.clear();
            mMap.clear();
            addMyLocationMarker(mMap,myPositionMaker.getPosition());
        }


        LatLngBounds.Builder builderBounds = new LatLngBounds.Builder();

        for (MarkerInfoStateItem markerInfos : markerInfoStateItems) {

        Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(markerInfos.getLatitude(),markerInfos.getLongitude()))
                    .title(markerInfos.getPlaceName())
                    .snippet(markerInfos.getNumberOfLunchmates()+getResources().getString(R.string.marker_snipet))
                    .icon(vectorToBitmap(R.drawable.ic_restaurant_menu_white_24dp,
                            (markerInfos.getNumberOfLunchmates()==0)?getResources().getColor(R.color.secondary):getResources().getColor(R.color.primary)))
                    .infoWindowAnchor(0.5f, 0.5f));

        builderBounds.include(marker.getPosition());

        RestaurantStateItem item = new RestaurantStateItem(
        markerInfos.getPlaceId(),
        markerInfos.getPlaceName(),
        markerInfos.getPlaceAddress().trim(),
        "2",
        R.string.error_unknown_error,
        3,
        markerInfos.getPlacePreviewPicUrl(),
        0);
        marker.setTag(item);
        restaurantsMarker.add(marker);

    }
// Set a listener for marker click.
        mMap.setOnInfoWindowClickListener(this);
//Bound camera around all cursors
        LatLngBounds bounds = builderBounds.build();
        if(mBounds==null || !mBounds.equals(bounds)){//when bounds are new we animate the camera
            mBounds=bounds;
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mBounds, 50));

        }else{//when it's a "on resume" case we don't
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBounds, 50));
        }

        mMap.setLatLngBoundsForCameraTarget(mBounds);//This set a move camera limit

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
        RestaurantStateItem item = (RestaurantStateItem) marker.getTag();
        startActivity(RestaurantDetailsActivity.navigate(requireContext(), item));
    }
}//END of MapViewFragment