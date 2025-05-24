package com.example.chattinomessanger;

import android.content.Intent;
import android.media.MediaActionSound;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    Button loginBtn;
    Button registerBtn;

    MediaPlayer mediaPlayer, buttonClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = MediaPlayer.create(this, R.raw.welcome_sound);
        buttonClick = MediaPlayer.create(this, R.raw.button_click_sound);

        mediaPlayer.start();

        loginBtn = findViewById(R.id.login1);
        loginBtn.setOnClickListener(this);

        registerBtn = findViewById(R.id.register1);
        registerBtn.setOnClickListener(this);



    }


    @Override
    public void onClick(View view) {

        buttonClick.start();

        if(view.getId() == R.id.login1)
        {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        }

        if(view.getId() == R.id.register1)
        {
            Intent intent = new Intent(MainActivity.this, Register.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if(FirebaseAuth.getInstance().getCurrentUser() != null){
//            startActivity(new Intent(MainActivity.this, MainLandingPage.class));
//            finish();
//        }
    }
}