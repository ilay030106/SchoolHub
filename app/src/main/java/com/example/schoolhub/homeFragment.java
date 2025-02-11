package com.example.schoolhub;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;


public class homeFragment extends Fragment {
    TextView Welcome_back_Tv;

    FirebaseAuth auth;
    FirebaseUser user;

    FirebaseFirestore db;
    String firstName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // אתחול TextView
        Welcome_back_Tv = view.findViewById(R.id.Welcome_back_Tv);

        // בדיקה אם הרשת מופעלת
        db.enableNetwork()
                .addOnSuccessListener(aVoid -> {
                    // קריאה לפונקציה לשליפת נתוני המשתמש
                    fetchUserName();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to enable network: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void fetchUserName() {
        // בדיקה אם המשתמש מחובר
        String userId = auth.getUid();
        if (userId == null) {
            Toast.makeText(getActivity(), "User is not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // שליפת נתונים מ-Firestore
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // שליפת השם הפרטי
                        String firstName = documentSnapshot.getString("firstName");
                        Welcome_back_Tv.setText("Welcome back " + firstName);
                    } else {
                        Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to fetch user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("HomeFragment", "Failed to fetch user details", e);
                });
    }

}