package com.example.schoolhub.TimeTable;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.schoolhub.Login.SharedViewModel;
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
    private SharedViewModel sharedViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_table, container, false);

        // Initialize views
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        addLessonFab = view.findViewById(R.id.add_lesson_fab);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Set up adapter and ViewPager
        sharedViewModel.getUserID().observe(getViewLifecycleOwner(), userId -> {
            // Set up adapter and ViewPager with the userId
            adapter = new TimetablePagerAdapter(this, userId);
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
        });

        // Handle FAB click to open AddClassDialogFragment
        addLessonFab.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                FragmentManager fragmentManager = getParentFragmentManager();
                AddClassDialogFragment dialogFragment = new AddClassDialogFragment(userId);
                dialogFragment.show(fragmentManager, "AddClassDialogFragment");
            } else {
                // Handle the case where the user is not logged in
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
