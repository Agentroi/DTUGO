package com.example.dtugo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.example.dtugo.challenges.ChallengeTemplate;
import com.example.dtugo.challenges.RunActivity;
import com.example.dtugo.challenges.ResultActivity;
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
    private Dialog informationWindow;
    private TextView title;
    private TextView info;
    private String[] titles;
    private String[] infoTexts;

    LocationManager locationManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //initialize view
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        informationWindow = new Dialog(getActivity());
        titles = getResources().getStringArray(R.array.titles);
        infoTexts = getResources().getStringArray(R.array.infoTexts);

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
                addMarker("Biblioteket", 55.786741, 12.523164);
                //S-Huset
                addMarker("S-Huset",55.7865393,12.5253112);
                //Skylab
                addMarker("SkyLab", 55.7814779,12.5112552);
                //Netto
                addMarker("Netto", 55.783776, 12.524045);

                mMap.setOnMarkerClickListener(MapFragment.this::onMarkerClick);
            }
        });

        //Return view
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        //We check for permissions
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
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
        //Show marker information
        marker.showInfoWindow();

        //Get latitude-longitude from the marker
        double markerLat = marker.getPosition().latitude;
        double markerLong = marker.getPosition().longitude;

        //Create new location for marker
        Location markerLoc = new Location(LocationManager.GPS_PROVIDER);
        markerLoc.setLatitude(markerLat);
        markerLoc.setLongitude(markerLong);

        int position = 0;
        Intent intent = new Intent(getActivity(), MapActivity.class);;

        setupPopup();
        Button startChallengeButton = createChallengeButton();

        if(marker.getTitle().equals("Biblioteket")){
            position = 0;
            intent = new Intent(getActivity(), ChallengeTemplate.class);

        } else if(marker.getTitle().equals("S-Huset")){
            position = 1;
            intent = new Intent(getActivity(), ChallengeTemplate.class);

        } else if(marker.getTitle().equals("Netto")){
            position = 2;
            intent = new Intent(getActivity(), ChallengeTemplate.class);
            title.setText(titles[position]);
            info.setText(infoTexts[position]);

            startChallengeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(myLocation.distanceTo(markerLoc) < 100000){
                        Intent intent = new Intent(getActivity(), RunActivity.class);
                        startActivity(intent);
                        informationWindow.dismiss();

        } else if(marker.getTitle().equals("SkyLab")) {
            position = 3;
            intent = new Intent(getActivity(), ChallengeTemplate.class);
        }
        title.setText(titles[position]);
        info.setText(infoTexts[position]);

        Intent finalIntent = intent;
        startChallengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myLocation.distanceTo(markerLoc) < 10000000){
                    startActivity(finalIntent);
                    informationWindow.dismiss();

                }else {
                    Toast.makeText(getActivity().getApplicationContext(), "Du skal gå tættere på en markør for at starte en udfordring!", Toast.LENGTH_LONG).show();
                }
            }
        });

        informationWindow.show();


        //Check if current location is 100 m away from marker
//        if(myLocation.distanceTo(markerLoc) < 100){
//            Toast.makeText(getActivity().getApplicationContext(), "Du er klar til en udfordring!", Toast.LENGTH_LONG).show();
//        }else{
//            Toast.makeText(getActivity().getApplicationContext(), "Du skal gå tættere på en markør for at starte en udfordring!", Toast.LENGTH_LONG).show();
//        }
        return true;
    }

    private Button createChallengeButton() {
        Button startChallengeButton = new Button(getActivity());
        startChallengeButton.setText("Start udfordringen!");
        LinearLayout linearLayout = (LinearLayout) informationWindow.findViewById(R.id.generalLinearLayout);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        linearLayout.addView(startChallengeButton, params);
        return startChallengeButton;
    }

    private void setupPopup() {
        informationWindow.setContentView(R.layout.information_window);
        title = (TextView) informationWindow.findViewById(R.id.titleTextView);
        info = (TextView) informationWindow.findViewById(R.id.infoTextView);
        informationWindow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

}
