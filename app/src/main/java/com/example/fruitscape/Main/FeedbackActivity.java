package com.example.fruitscape.Main;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fruitscape.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FeedbackActivity extends AppCompatActivity {

    private Button submitFeedbackBtn;
    private EditText feedback;
    private FirebaseAuth firebaseAuth;
    private BottomNavigationView bottomNav;
    private FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        firebaseAuth = FirebaseAuth.getInstance();
        submitFeedbackBtn = findViewById(R.id.feedbackBtn);
        feedback = findViewById(R.id.feedbackEt);
        bottomNav = findViewById(R.id.bottom_nav);
        checkIfUserLogged();
        setUpNav();
    }

    private void submitFeedback (){
        submitFeedbackBtn.setOnClickListener(v -> {
            String feedbackVal = feedback.getText().toString();
            String user_id = firebaseAuth.getCurrentUser().getUid();
            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Feedback").child(user_id);
            current_user_db.setValue(feedbackVal);

        });

    }
    private void checkIfUserLogged() {

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    Toast.makeText(FeedbackActivity.this, "Sign in or Sign up first!", Toast.LENGTH_SHORT).show();

                } else {
                    submitFeedback();
                }
            }
        };

    }

    private void setUpNav(){
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_home:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    return true;
                case R.id.nav_account:
                    startActivity(new Intent(getApplicationContext(), UserAccountActivity.class));
                    return true;
                case R.id.nav_history:
                    startActivity(new Intent(getApplicationContext(), HistoryActivity.class));
                    return true;
                case R.id.nav_feedback:
                    startActivity(new Intent(getApplicationContext(), FeedbackActivity.class));
                    return true;
            }
            return false;
        });
    }
}
