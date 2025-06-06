package com.example.schoolhub.ui.fragment.TimeTable;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.schoolhub.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TimeTableFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 viewPager;
    TimetablePagerAdapter adapter;
    ExtendedFloatingActionButton addLessonFab;

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
        addLessonFab = view.findViewById(R.id.add_lesson_fab);

        // Set up adapter and ViewPager
        adapter = new TimetablePagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Attach TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            @SuppressLint("InflateParams") TextView customTab = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, null);
            switch (position) {
                case 0:
                    customTab.setText("ראשון");
                    break;
                case 1:
                    customTab.setText("שני");
                    break;
                case 2:
                    customTab.setText("שלישי");
                    break;
                case 3:
                    customTab.setText("רביעי");
                    break;
                case 4:
                    customTab.setText("חמישי");
                    break;
            }
            tab.setCustomView(customTab);
        }).attach();

        // Handle FAB click to open AddClassDialogFragment
        addLessonFab.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            AddClassDialogFragment dialogFragment = new AddClassDialogFragment();
            dialogFragment.show(fragmentManager, "AddClassDialogFragment");
        });

        return view;
    }


}
