package com.example.dtugo;

import android.app.Dialog;
import android.app.ListActivity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {
    private Dialog informationWindow;
    private TextView title;
    private TextView info;
    private String[] titles;
    private String[] infoTexts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        titles = getResources().getStringArray(R.array.titles);
        infoTexts = getResources().getStringArray(R.array.infoTexts);

        informationWindow = new Dialog(getActivity());


        ListView listView = view.findViewById(R.id.list);

        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                titles
        );

        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                informationWindow.setContentView(R.layout.information_window);
                title = (TextView) informationWindow.findViewById(R.id.titleTextView);
                info = (TextView) informationWindow.findViewById(R.id.infoTextView);
                title.setText(titles[position]);
                info.setText(infoTexts[position]);
                informationWindow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                informationWindow.show();

            }
        });


        return view;
    }

}
