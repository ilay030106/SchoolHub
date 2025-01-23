package com.example.schoolhub.Login;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schoolhub.MainActivity;
import com.example.schoolhub.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginScreen extends AppCompatActivity {
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_REMEMBER_ME = "rememberMe";
    private static final String KEY_EMAIL = "email";

    TextInputLayout emailLayout, passwordLayout;
    TextInputEditText emailInput, passwordInput;
    MaterialButton EmailSignIn;

    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 100;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

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

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isRemembered = preferences.getBoolean(KEY_REMEMBER_ME, false);

        if (isRemembered) {
            String savedEmail = preferences.getString(KEY_EMAIL, "");
            proceedToMainScreen(savedEmail);
            return;
        }

        EmailSignIn = findViewById(R.id.EmailSignIn);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.googleSignIn).setOnClickListener(v -> signInWithGoogle());
        EmailSignIn.setOnClickListener(view -> openEmailSignInDialog(preferences));
    }

    private void openEmailSignInDialog(SharedPreferences preferences) {
        Dialog dialog = new Dialog(this, R.style.CustomDialogTheme);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.email_sign_in_dialog);

        emailLayout = dialog.findViewById(R.id.emailLayout);
        emailInput = dialog.findViewById(R.id.emailInput);
        passwordLayout = dialog.findViewById(R.id.passwordLayout);
        passwordInput = dialog.findViewById(R.id.passwordInput);
        MaterialSwitch rememberMeSwitch = dialog.findViewById(R.id.rememberMeSwitch);

        rememberMeSwitch.setOnCheckedChangeListener((switchView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_REMEMBER_ME, isChecked);
            if (isChecked) {
                editor.putString(KEY_EMAIL, Objects.requireNonNull(emailInput.getText()).toString());
            } else {
                editor.remove(KEY_EMAIL);
            }
            editor.apply();
        });

        MaterialButton signIn = dialog.findViewById(R.id.signIn);
        signIn.setOnClickListener(v -> {
            if (validateInputs()) {
                String email = Objects.requireNonNull(emailInput.getText()).toString();
                proceedToMainScreen(email);
                dialog.dismiss();
            }
        });

        TextView signUp = dialog.findViewById(R.id.signUp);
        signUp.setOnClickListener(view -> {
            dialog.dismiss();
            openSignUpDialog();
        });

        dialog.show();
    }

    private void openSignUpDialog() {
        Dialog dialog = new Dialog(this, R.style.CustomDialogTheme);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.sign_up_dialog);

        TextInputLayout nameLayout = dialog.findViewById(R.id.NameLayout);
        TextInputLayout emailLayout = dialog.findViewById(R.id.signUpemailLayout);
        TextInputLayout passwordLayout = dialog.findViewById(R.id.signUppasswordLayout);
        TextInputLayout confirmPasswordLayout = dialog.findViewById(R.id.confirm_passwordLayout);

        TextInputEditText nameInput = dialog.findViewById(R.id.NameInput);
        TextInputEditText emailInput = dialog.findViewById(R.id.signUpemailInput);
        TextInputEditText passwordInput = dialog.findViewById(R.id.signUp_passwordInput);
        TextInputEditText confirmPasswordInput = dialog.findViewById(R.id.confirm_passwordInput);

        MaterialButton signUpBtn = dialog.findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(v -> {
            String name = Objects.requireNonNull(nameInput.getText()).toString();
            String email = Objects.requireNonNull(emailInput.getText()).toString();
            String password = Objects.requireNonNull(passwordInput.getText()).toString();
            String confirmPassword = Objects.requireNonNull(confirmPasswordInput.getText()).toString();

            if (validateSignUpInputs(name, email, password, confirmPassword, nameLayout, emailLayout, passwordLayout, confirmPasswordLayout)) {
                createNewUser(email, password, dialog);
            }
        });

        dialog.show();
    }

    private boolean validateSignUpInputs(String name, String email, String password, String confirmPassword,
                                         TextInputLayout nameLayout, TextInputLayout emailLayout,
                                         TextInputLayout passwordLayout, TextInputLayout confirmPasswordLayout) {
        if (name.isEmpty()) {
            nameLayout.setError("Name is required");
            return false;
        } else {
            nameLayout.setError(null);
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Invalid email format");
            return false;
        } else {
            emailLayout.setError(null);
        }

        if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            return false;
        } else {
            passwordLayout.setError(null);
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Passwords do not match");
            return false;
        } else {
            confirmPasswordLayout.setError(null);
        }

        return true;
    }

    private void createNewUser(String email, String password, Dialog dialog) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating account...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        sendVerificationEmail();
                        dialog.dismiss();
                        proceedToMainScreen(email);
                    } else {
                        Toast.makeText(this, "Sign-Up failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Verification email sent. Please verify your email before logging in.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean validateInputs() {
        String email = Objects.requireNonNull(emailInput.getText()).toString();
        String password = Objects.requireNonNull(passwordInput.getText()).toString();

        if (email.isEmpty()) {
            emailLayout.setError("Email cannot be empty");
            return false;
        } else {
            emailLayout.setError(null);
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Invalid email format");
            return false;
        } else {
            emailLayout.setError(null);
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Password cannot be empty");
            return false;
        } else {
            passwordLayout.setError(null);
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing in...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        proceedToMainScreen(email);
                    } else {
                        passwordLayout.setError("Authentication failed: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });

        return false; // Validation completes asynchronously
    }

    private void proceedToMainScreen(String email) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        proceedToMainScreen(Objects.requireNonNull(user).getEmail());
                    } else {
                        Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
