package com.example.schoolhub;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TimeTableFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 viewPager;
    TimetablePagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_table, container, false);

        // Initialize views
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        // Set up adapter and ViewPager
        adapter = new TimetablePagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Attach TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("ראשון");
                    break;
                case 1:
                    tab.setText("שני");
                    break;
                case 2:
                    tab.setText("שלישי");
                    break;
                case 3:
                    tab.setText("רביעי");
                    break;
                case 4:
                    tab.setText("חמישי");
                    break;
            }
        }).attach();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
