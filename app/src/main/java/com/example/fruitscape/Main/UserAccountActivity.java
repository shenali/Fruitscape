package com.example.fruitscape.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.fruitscape.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserAccountActivity extends AppCompatActivity {
    private Button logout, register ;
    private TextView name, email, contactNumber;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);
        firebaseAuth = FirebaseAuth.getInstance();
        logout = findViewById(R.id.logOutBtn);
        name = findViewById(R.id.namePreview);
        email = findViewById(R.id.emailPreview);
        contactNumber = findViewById(R.id.contactNumPreview);
        bottomNav = findViewById(R.id.bottom_nav);

        setupFirebase();
        login();
        setUpNav();
    }

    private void setupFirebase(){
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    Intent newIntent = new Intent(UserAccountActivity.this, LoginActivity.class);
                    startActivity(newIntent);
                } else {

                    String userId = FirebaseAuth.getInstance().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //  Setting the values
                            try {
                                if (dataSnapshot.exists()) {
                                    name.setText(dataSnapshot.child("Name").getValue().toString());
                                    email.setText(dataSnapshot.child("Email").getValue().toString());
                                    contactNumber.setText(dataSnapshot.child("Phone").getValue().toString());
                                }
                            }catch (Exception e) {
                                System.out.println(e);
                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        };
    }

    private void login(){
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent newIntent = new Intent(UserAccountActivity.this, LoginActivity.class);
                startActivity(newIntent);

            }
        });
    }


    private void setUpNav() {
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
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


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
