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
import androidx.lifecycle.ViewModelProvider;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginScreen extends AppCompatActivity {
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_REMEMBER_ME = "rememberMe";
    private static final String KEY_EMAIL = "email";

    TextInputLayout emailLayout, passwordLayout;
    TextInputEditText emailInput, passwordInput,nameInput;
    MaterialButton EmailSignIn;
    ProgressDialog progressDialog;

    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 100;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Dialog activeDialog;

    private SharedViewModel sharedViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

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
                .requestIdToken("433599334811-14p1hrshlf1nrb7nk7l7h8p92r69n8gk.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.googleSignIn).setOnClickListener(v -> signInWithGoogle());
        EmailSignIn.setOnClickListener(view -> openEmailSignInDialog(preferences));
    }

    private void openEmailSignInDialog(SharedPreferences preferences) {
        activeDialog = new Dialog(this, R.style.CustomDialogTheme);
        activeDialog.setCancelable(true);
        activeDialog.setContentView(R.layout.email_sign_in_dialog);

        emailLayout = activeDialog.findViewById(R.id.emailLayout);
        emailInput = activeDialog.findViewById(R.id.emailInput);
        passwordLayout = activeDialog.findViewById(R.id.passwordLayout);
        passwordInput = activeDialog.findViewById(R.id.passwordInput);
        MaterialSwitch rememberMeSwitch = activeDialog.findViewById(R.id.rememberMeSwitch);

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

        MaterialButton signIn = activeDialog.findViewById(R.id.signIn);
        signIn.setOnClickListener(v -> {
            if (validateInputs()) {
                String email = Objects.requireNonNull(emailInput.getText()).toString();
                proceedToMainScreen(email);
                activeDialog.dismiss();
            }
        });

        TextView signUp = activeDialog.findViewById(R.id.signUp);
        signUp.setOnClickListener(view -> {
            activeDialog.dismiss();
            openSignUpDialog();
        });

        activeDialog.show();
    }

    private void openSignUpDialog() {
        activeDialog = new Dialog(this, R.style.CustomDialogTheme);
        activeDialog.setCancelable(true);
        activeDialog.setContentView(R.layout.sign_up_dialog);

        TextInputLayout nameLayout = activeDialog.findViewById(R.id.NameLayout);
        TextInputLayout emailLayout = activeDialog.findViewById(R.id.signUpemailLayout);
        TextInputLayout passwordLayout = activeDialog.findViewById(R.id.signUppasswordLayout);
        TextInputLayout confirmPasswordLayout = activeDialog.findViewById(R.id.confirm_passwordLayout);

        nameInput = activeDialog.findViewById(R.id.NameInput);
        TextInputEditText emailInput = activeDialog.findViewById(R.id.signUpemailInput);
        TextInputEditText passwordInput = activeDialog.findViewById(R.id.signUp_passwordInput);
        TextInputEditText confirmPasswordInput = activeDialog.findViewById(R.id.confirm_passwordInput);

        MaterialButton signUpBtn = activeDialog.findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(v -> {
            String name = Objects.requireNonNull(nameInput.getText()).toString();
            String email = Objects.requireNonNull(emailInput.getText()).toString();
            String password = Objects.requireNonNull(passwordInput.getText()).toString();
            String confirmPassword = Objects.requireNonNull(confirmPasswordInput.getText()).toString();

            if (validateSignUpInputs(name, email, password, confirmPassword, nameLayout, emailLayout, passwordLayout, confirmPasswordLayout)) {
                createNewUser(email, password, name, activeDialog);
                sharedViewModel.setFirstName(name);

            }
        });

        activeDialog.show();
    }
    private boolean validateSignUpInputs(String name, String email, String password, String confirmPassword, TextInputLayout nameLayout, TextInputLayout emailLayout, TextInputLayout passwordLayout, TextInputLayout confirmPasswordLayout) {
        boolean isValid = true;

        if (name.isEmpty()) {
            nameLayout.setError("Name cannot be empty");
            isValid = false;
        } else {
            nameLayout.setError(null);
        }

        if (email.isEmpty()) {
            emailLayout.setError("Email cannot be empty");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Invalid email format");
            isValid = false;
        } else {
            emailLayout.setError(null);
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Password cannot be empty");
            isValid = false;
        } else {
            passwordLayout.setError(null);
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordLayout.setError("Confirm Password cannot be empty");
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            confirmPasswordLayout.setError("Passwords do not match");
            isValid = false;
        } else {
            confirmPasswordLayout.setError(null);
        }

        return isValid;
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
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            fetchUserNameAndProceed(user.getUid());
                        }
                    } else {
                        passwordLayout.setError("Authentication failed: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });

        return false; // Validation completes asynchronously
    }

    private void fetchUserNameAndProceed(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("firstName");
                        sharedViewModel.setFirstName(firstName);
                        proceedToMainScreen(firstName);
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createNewUser(String email, String password, String name, Dialog dialog) {
        showProgressDialog("Creating account...");
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    dismissProgressDialog();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            String userId = user.getUid();
                            saveUserDetailsToFirestore(userId, email, name, dialog);
                        }
                    } else {
                        Toast.makeText(this, "Sign-Up failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void saveUserDetailsToFirestore(String userId, String email, String name, Dialog dialog) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("email", email);
        userDetails.put("firstName", name);

        db.collection("users").document(userId)
                .set(userDetails)
                .addOnSuccessListener(aVoid -> {
                    dialog.dismiss();
                    proceedToMainScreen(name);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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


    private void proceedToMainScreen(String name) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("firstName", name);

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
                        if (user != null) {
                            fetchUserNameAndProceed(user.getUid());
                        }
                    } else {
                        Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (activeDialog != null && activeDialog.isShowing()) {
            activeDialog.dismiss();
            activeDialog = null; // איפוס הרפרנס כדי למנוע זליגות זיכרון
        }
    }

}
