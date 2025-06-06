package com.example.schoolhub.ui.login;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolhub.MainActivity;
import com.example.schoolhub.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class LoginScreen extends AppCompatActivity {

    private FirebaseAuth auth;
    private SignInButton googleSignInBtn;
    private MaterialButton emailSignInBtn;
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onStart() {
        super.onStart();
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }

        boolean rememberMe = getSharedPreferences("login", MODE_PRIVATE)
                .getBoolean("rememberMe", false);

        FirebaseUser user = auth.getCurrentUser();
        if (user != null && rememberMe) {
            goToMain();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        auth = FirebaseAuth.getInstance();

        googleSignInBtn = findViewById(R.id.googleSignIn);
        emailSignInBtn = findViewById(R.id.EmailSignIn);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInBtn.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        emailSignInBtn.setOnClickListener(v -> emailSignInDialog());
    }

    private void emailSignInDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.email_sign_in_dialog, null);
        TextInputEditText emailInput = dialogView.findViewById(R.id.emailInput);
        TextInputEditText passwordInput = dialogView.findViewById(R.id.passwordInput);
        MaterialSwitch rememberMeSwitch = dialogView.findViewById(R.id.rememberMeSwitch);
        MaterialButton signInButton = dialogView.findViewById(R.id.signIn);
        TextView signUp = dialogView.findViewById(R.id.signUp);
        Dialog dialog = new Dialog(this, R.style.CustomDialogTheme);
        dialog.setContentView(dialogView);

        rememberMeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int color = getResources().getColor(isChecked ? R.color.darkBlue : R.color.lightBlue);
            rememberMeSwitch.setTrackTintList(ColorStateList.valueOf(color));
        });

        signUp.setOnClickListener(v -> {
            signUpDialog();
            dialog.dismiss();
        });

        signInButton.setOnClickListener(v -> {
            String email = Objects.requireNonNull(emailInput.getText()).toString().trim();
            String password = Objects.requireNonNull(passwordInput.getText()).toString().trim();
            boolean rememberMe = rememberMeSwitch.isChecked();

            if (email.isEmpty() || password.isEmpty()) {
                Snackbar.make(v, "Email and password cannot be empty", Snackbar.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            getSharedPreferences("login", MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("rememberMe", rememberMe)
                                    .apply();

                            Snackbar.make(v, "Login success!", Snackbar.LENGTH_SHORT).show();
                            goToMain();
                            dialog.dismiss();
                        } else {
                            Snackbar.make(v, "Login failed: " + Objects.requireNonNull(task.getException()).getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    });
        });

        dialog.show();
    }

    private void signUpDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.sign_up_dialog, null);

        TextInputLayout nameLayout = dialogView.findViewById(R.id.NameLayout);
        TextInputLayout emailLayout = dialogView.findViewById(R.id.signUpEmailLayout);
        TextInputLayout passwordLayout = dialogView.findViewById(R.id.signUpPasswordLayout);
        TextInputLayout confirmLayout = dialogView.findViewById(R.id.confirm_passwordLayout);

        TextInputEditText nameInput = dialogView.findViewById(R.id.NameInput);
        TextInputEditText emailInput = dialogView.findViewById(R.id.signUpEmailInput);
        TextInputEditText passwordInput = dialogView.findViewById(R.id.signUp_passwordInput);
        TextInputEditText confirmInput = dialogView.findViewById(R.id.confirm_passwordInput);

        MaterialButton signUpBtn = dialogView.findViewById(R.id.signUpBtn);
        Dialog dialog = new Dialog(this, R.style.CustomDialogTheme);
        dialog.setContentView(dialogView);

        signUpBtn.setOnClickListener(v -> {
            nameLayout.setError(null);
            emailLayout.setError(null);
            passwordLayout.setError(null);
            confirmLayout.setError(null);

            String name = Objects.requireNonNull(nameInput.getText()).toString().trim();
            String email = Objects.requireNonNull(emailInput.getText()).toString().trim();
            String password = Objects.requireNonNull(passwordInput.getText()).toString().trim();
            String confirmPassword = Objects.requireNonNull(confirmInput.getText()).toString().trim();

            boolean valid = true;

            if (name.isEmpty()) {
                nameLayout.setError("Name is required");
                valid = false;
            } else if (name.matches(".*\\d.*")) {
                nameLayout.setError("Name cannot contain numbers");
                valid = false;
            }

            if (email.isEmpty()) {
                emailLayout.setError("Email is required");
                valid = false;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailLayout.setError("Invalid email format");
                valid = false;
            }

            if (password.isEmpty()) {
                passwordLayout.setError("Password is required");
                valid = false;
            } else if (password.length() < 6 ||
                    !password.matches(".*[A-Z].*") ||
                    !password.matches(".*[a-z].*")) {
                passwordLayout.setError("Password must be 6+ chars, with uppercase and lowercase");
                valid = false;
            }

            if (!confirmPassword.equals(password)) {
                confirmLayout.setError("Passwords do not match");
                valid = false;
            }

            if (!valid) return;

            auth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            boolean userExists = !Objects.requireNonNull(task.getResult().getSignInMethods()).isEmpty();
                            if (userExists) {
                                emailLayout.setError("This email is already in use");
                            } else {
                                auth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(this, signupTask -> {
                                            if (signupTask.isSuccessful()) {
                                                FirebaseUser user = auth.getCurrentUser();
                                                if (user != null) {
                                                    user.updateProfile(new UserProfileChangeRequest.Builder()
                                                                    .setDisplayName(name)
                                                                    .build())
                                                            .addOnCompleteListener(profileTask -> {
                                                                Snackbar.make(v, "Account created! Logging in...", Snackbar.LENGTH_SHORT).show();
                                                                getSharedPreferences("login", MODE_PRIVATE)
                                                                        .edit()
                                                                        .putBoolean("rememberMe", true)
                                                                        .apply();
                                                                dialog.dismiss();
                                                                goToMain();
                                                            });
                                                } else {
                                                    Snackbar.make(v, "Account created, but user is null!", Snackbar.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                    goToMain();
                                                }
                                            } else {
                                                Snackbar.make(v, "Error: " + signupTask.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Snackbar.make(v, "Failed to check email: " + task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    });
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LoginScreen.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
                View rootView = findViewById(android.R.id.content);
                Snackbar.make(rootView, "Google sign-in failed", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    View rootView = findViewById(android.R.id.content);
                    if (task.isSuccessful()) {
                        Snackbar.make(rootView, "Login success!", Snackbar.LENGTH_SHORT).show();
                        goToMain();
                    } else {
                        Snackbar.make(rootView, "Firebase auth failed: " + Objects.requireNonNull(task.getException()).getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
