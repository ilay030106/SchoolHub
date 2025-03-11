package com.example.schoolhub;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.schoolhub.ui.fragment.Calculator.baseCalculator;
import com.example.schoolhub.ui.fragment.TimeTable.TimeTableFragment;
import com.example.schoolhub.ui.fragment.Home.homeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView menu;

    private int currentMenuItemId = R.id.navigation_home; // Default to home menu

    public void replaceFragment(Fragment fragment, boolean isMovingRight) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Set animations based on direction
        if (isMovingRight) {
            fragmentTransaction.setCustomAnimations(
                    R.anim.slide_in_left,  // Enter animation
                    R.anim.slide_out_right // Exit animation
            );
        } else {
            fragmentTransaction.setCustomAnimations(
                    R.anim.slide_in_right,  // Enter animation
                    R.anim.slide_out_left  // Exit animation
            );
        }

        fragmentTransaction.replace(R.id.AppFragmentContainer, fragment);
        fragmentTransaction.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menu = findViewById(R.id.menu);

        menu.setOnItemSelectedListener(item -> {
            Fragment fragment = null;


            int itemId = item.getItemId();
            boolean isMovingRight = itemId > currentMenuItemId;
            if (itemId == R.id.navigation_home) {
                fragment = new homeFragment();
            } else if (itemId == R.id.navigation_timetable) {
                fragment = new TimeTableFragment();
            } else if (itemId == R.id.navigation_calculator) {
                fragment = new baseCalculator();
            }


            if (fragment != null) {
                replaceFragment(fragment, isMovingRight);
                currentMenuItemId = itemId; // Update current item ID
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            menu.setSelectedItemId(R.id.navigation_home);
        }
    }


}
