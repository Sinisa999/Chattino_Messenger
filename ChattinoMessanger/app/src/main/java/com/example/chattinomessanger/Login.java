package com.example.chattinomessanger;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    DatabaseReference databaseReference;

    EditText userEmail, userPassword;

    Button loginBtn;

    String password, email;

    MediaPlayer buttonClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        buttonClick = MediaPlayer.create(this, R.raw.button_click_sound);

        userEmail = findViewById(R.id.login_email);
        userPassword = findViewById(R.id.login_password);

        loginBtn = findViewById(R.id.login_loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = userEmail.getText().toString().trim();
                password = userPassword.getText().toString().trim();

                buttonClick.start();

                if(!validateInputs()) {
                    return;
                }

                Login();


            }
        });




    }


    private boolean validateInputs() {
        boolean isValid = true;

        if(TextUtils.isEmpty(email)) {
            userEmail.setError("Unesite vase korisnicko ime");
            userEmail.requestFocus();
            isValid = false;
        }

        if(TextUtils.isEmpty(password)) {
            userPassword.setError("Unesite vasu sifru");
            userPassword.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    private void Login() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.trim(), password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

                        Intent intent = new Intent(Login.this, MainLandingPage.class);
                        intent.putExtra("name", username);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof FirebaseAuthInvalidUserException){
                            Toast.makeText(Login.this, "korisnik ne postoji", Toast.LENGTH_SHORT)
                                    .show();
                        }
                        else{
                            Toast.makeText(Login.this, "prijava nije uspela", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }
}