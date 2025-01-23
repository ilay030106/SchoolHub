package com.example.schoolhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.schoolhub.Login.LoginScreen;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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
            boolean isMovingRight = false;

            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                fragment = new homeFragment();
                isMovingRight = itemId > currentMenuItemId; // Determine direction
            } else if (itemId == R.id.navigation_timetable) {
                fragment = new TimeTableFragment();
                isMovingRight = itemId > currentMenuItemId; // Determine direction
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

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
