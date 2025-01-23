package com.example.schoolhub.Login;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schoolhub.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginScreen extends AppCompatActivity {

    TextInputLayout emailLayout, passwordLayout;
    TextInputEditText emailInput, passwordInput;

    MaterialButton EmailSignIn;

    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 100; // אתה בוחר את המספר



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EmailSignIn = findViewById(R.id.EmailSignIn);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // קבל את ה-ID מ-Firebase Console
                .requestEmail()
                .build();

        // יצירת GoogleSignInClient
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // כפתור Google Sign-In
        findViewById(R.id.googleSignIn).setOnClickListener(v -> signInWithGoogle());
        EmailSignIn.setOnClickListener(view -> {
            Dialog dialog = new Dialog(this,R.style.CustomDialogTheme);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.email_sign_in_dialog);
            emailLayout = dialog.findViewById(R.id.emailLayout);
            emailInput = dialog.findViewById(R.id.emailInput);
            passwordLayout = dialog.findViewById(R.id.passwordLayout);
            passwordInput = dialog.findViewById(R.id.passwordInput);
            dialog.show();
        });


    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}