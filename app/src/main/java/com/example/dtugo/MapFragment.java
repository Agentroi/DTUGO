package com.example.dtugo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment implements GoogleMap.OnMarkerClickListener{

    private static final long POLLING_FREQ = 1000 * 5;
    private static final float MIN_DISTANCE = 3.0f;

    private GoogleMap mMap;
    private Location myLocation;
    private LocationListener locationListener;

    LocationManager locationManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //initialize view
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //Initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        //Initialize location manager:
        locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(LOCATION_SERVICE);

        //Initialize location listener
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLocation = location;
            }
        };

        //We check for permissions
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }


        //Init myLocation
        myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                restrictArea(googleMap);

                mMap.setMyLocationEnabled(true);

                //Point of interest for DTU LIBRARY
                addMarker("DTU Library", 55.786741, 12.523164);
                //S-Huset
                addMarker("S-Huset",55.7865393,12.5253112);
                //Skylab
                addMarker("SkyLab", 55.7814779,12.5112552);
                //Netto
                addMarker("Netto", 55.783832,12.5219749);

                mMap.setOnMarkerClickListener(MapFragment.this::onMarkerClick);
            }
        });

        //Return view
        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume(){
        super.onResume();
        if (null != locationManager.getProvider(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, POLLING_FREQ, MIN_DISTANCE, locationListener);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private void restrictArea(GoogleMap googleMap) {
        mMap = googleMap;
        //When map is loaded
        LatLng one = new LatLng(55.781627, 12.511186);
        LatLng two = new LatLng(55.791887, 12.528041);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //add them to builder
        builder.include(one);
        builder.include(two);

        LatLngBounds bounds = builder.build();

        //get width and height to current display screen
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        //padding
        int padding = (int) (width * 0.10);

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //set bounds
        mMap.setLatLngBoundsForCameraTarget(bounds);

        //move camera to fill the bound to screen
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));

        //set zoom to level to current so that you won't be able to zoom out viz. move outside bounds
        mMap.setMinZoomPreference(mMap.getCameraPosition().zoom);

    }

    //Method for adding markers.
    private void addMarker(String title ,double v1, double v2){
        mMap.addMarker(new MarkerOptions().position(new LatLng(v1,v2)).title(title));
    }

    @Override
    public boolean onMarkerClick(Marker marker){
        double markerLat = marker.getPosition().latitude;
        double markerLong = marker.getPosition().longitude;

        Location markerLoc = new Location(LocationManager.GPS_PROVIDER);
        markerLoc.setLatitude(markerLat);
        markerLoc.setLongitude(markerLong);

        if(myLocation.distanceTo(markerLoc) < 50){
            Toast.makeText(getActivity().getApplicationContext(), "U're good", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getActivity().getApplicationContext(), "Get closer", Toast.LENGTH_LONG).show();
        }
        return true;
    }

}
