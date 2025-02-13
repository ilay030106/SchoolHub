package com.example.schoolhub.ui.fragment.Home;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schoolhub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class homeFragment extends Fragment {
    TextView Welcome_back_Tv;

    FirebaseAuth auth;
    FirebaseUser user;

    FirebaseFirestore db;
    String firstName;


    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }

    private void fetchUserName() {
        user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String firstName = documentSnapshot.getString("firstName");
                            Welcome_back_Tv.setText("Welcome back " + firstName);
                        } else {
                            Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Failed to fetch user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getActivity(), "User is not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // אתחול TextView
        Welcome_back_Tv = view.findViewById(R.id.Welcome_back_Tv);
        fetchUserName();

        // בדיקה אם הרשת מופעלת


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

//    private void fetchUserName() {
//        // בדיקה אם המשתמש מחובר
//        String userId = auth.getUid();
//        if (userId == null) {
//            Toast.makeText(getActivity(), "User is not authenticated", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//
//        // שליפת נתונים מ-Firestore
//        db.collection("users").document(userId).get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        // שליפת השם הפרטי
//                        String firstName = documentSnapshot.getString("firstName");
//                        Welcome_back_Tv.setText("Welcome back " + firstName);
//                    } else {
//                        Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(getActivity(), "Failed to fetch user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    Log.e("HomeFragment", "Failed to fetch user details", e);
//                });
//    }

}