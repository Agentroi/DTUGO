package com.example.dtugo;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class NavigationFragment extends Fragment {
    private View view;
    private FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_navigation, container, false);
        fragmentManager = getFragmentManager();

        Button mapButton = (Button) view.findViewById(R.id.toMapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(fragmentManager.findFragmentById(R.id.fragment_container) instanceof DtuMapFragment)){
                DtuMapFragment dtuMapFragment = new DtuMapFragment();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, dtuMapFragment).commit();
                }
            }
        });

        Button listButton = (Button) view.findViewById(R.id.toListButton);
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(fragmentManager.findFragmentById(R.id.fragment_container) instanceof LocationListFragment)) {
                    LocationListFragment locationListFragment = new LocationListFragment();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, locationListFragment).commit();
                }
            }
        });

        return view;
    }
}
