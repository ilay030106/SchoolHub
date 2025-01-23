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

import com.example.schoolhub.Login.LoginScreen;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView menu;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.AppFragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    public void replaceFragmentWithData(Fragment fragment, Bundle data) {
        fragment.setArguments(data);
        replaceFragment(fragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (currentUser == null) {
            redirectToLogin();
            return;
        }

        menu = findViewById(R.id.menu);

        menu.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                fragment = new homeFragment();
                Bundle data = new Bundle();
                data.putString("userId", currentUser.getUid());
                replaceFragmentWithData(fragment, data);
                return true;
            } else if (itemId == R.id.navigation_timetable) {
                fragment = new TimeTableFragment();
                Bundle data = new Bundle();
                data.putString("userId", currentUser.getUid());
                replaceFragmentWithData(fragment, data);
                return true;
            }
            if (fragment != null) {
                replaceFragment(fragment);
                return true;
            }
            return false;
        });

        // Restore fragment state or set default
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
