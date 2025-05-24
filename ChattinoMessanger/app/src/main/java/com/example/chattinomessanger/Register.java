package com.example.chattinomessanger;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.Objects;

public class Register extends AppCompatActivity {

    EditText userUsername, userPassword, userEmail;

    Button registerBtn;

    String username, password, email;
    DatabaseReference databaseReference;

    FirebaseAuth mAuth;

    MediaPlayer buttonClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buttonClick = MediaPlayer.create(this, R.raw.button_click_sound);

        setContentView(R.layout.activity_register);

        // Initialize Firebase components
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize App Check for debug (remove for production)
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
        );

        userUsername = findViewById(R.id.register_username);
        userPassword = findViewById(R.id.register_password);
        userEmail = findViewById(R.id.register_email);
        registerBtn = findViewById(R.id.register_btn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = userUsername.getText().toString().trim();
                password = userPassword.getText().toString().trim();
                email = userEmail.getText().toString().trim();

                buttonClick.start();

                if(!validateInputs()) {
                    return;
                }

                registerUser();
            }
        });




    }


    private boolean validateInputs() {
        boolean isValid = true;

        if (TextUtils.isEmpty(username)) {
            userUsername.setError("Unesite vase korisnicko ime");
            userUsername.requestFocus();
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            userEmail.setError("Unesite vas email");
            userEmail.requestFocus();
            isValid = false;

        }

        if (TextUtils.isEmpty(password)) {
            userPassword.setError("Unesite vasu sifru");
            userPassword.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    private void register() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.trim(), password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        assert firebaseUser != null;
                        firebaseUser.updateProfile(userProfileChangeRequest);

                        UserModel userModel = new UserModel(FirebaseAuth.getInstance().getUid(), username, email, password);
                        databaseReference.child(FirebaseAuth.getInstance().getUid()).setValue(userModel);
                        Intent intent = new Intent(Register.this, MainLandingPage.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "problem pri registraciji", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerUser() {
        // Show loading indicator if you have one

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            updateUserProfile(user);
                        } else {
                            Toast.makeText(Register.this,
                                    "Error: User not created", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        handleRegistrationError(task.getException());
                    }
                });
    }

    private void updateUserProfile(FirebaseUser user) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveUserToDatabase(user);
                    } else {
                        Toast.makeText(Register.this,
                                "Error updating profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToDatabase(FirebaseUser user) {
        UserModel userModel = new UserModel(
                user.getUid(),
                username,
                email,
                password);

        databaseReference.child(user.getUid())
                .setValue(userModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Register.this,
                                "Registration successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Register.this, MainLandingPage.class));
                        finish();
                    } else {
                        Toast.makeText(Register.this,
                                "Error saving data: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleRegistrationError(Exception exception) {
        String errorMessage = "Registration failed";
        if (exception != null) {
            if (exception instanceof FirebaseAuthWeakPasswordException) {
                errorMessage = "Password is too weak";
            } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                errorMessage = "Invalid email format";
            } else if (exception instanceof FirebaseAuthUserCollisionException) {
                errorMessage = "Email already in use";
            } else {
                errorMessage = exception.getMessage();
            }
        }
        Toast.makeText(Register.this, errorMessage, Toast.LENGTH_LONG).show();
        Log.e("RegistrationError", "Error during registration", exception);
    }

}