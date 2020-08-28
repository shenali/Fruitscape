package com.example.fruitscape.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fruitscape.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText emailId, passwordId;
    private Button loginBtn;
    private TextView registerTv,backTv;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private BottomNavigationView bottomNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.emailEt);
        passwordId = findViewById(R.id.passwordEt);
        loginBtn = findViewById(R.id.loginBtn);
        registerTv = findViewById(R.id.signupTv);
        backTv= findViewById(R.id.backBtn);
        bottomNav = findViewById(R.id.bottom_nav);

        setUpFirebase();
        login();
        register();
        back();
      //  setUpNav();

    }


    private void login(){
        loginBtn.setOnClickListener(v -> {
            String email = emailId.getText().toString();
            String password = passwordId.getText().toString();
            if(email.isEmpty()){
                emailId.setError("Please enter email id");
                emailId.requestFocus();
            } else if(password.isEmpty()){
                passwordId.setError("Please enter the password");
                passwordId.requestFocus();
            } else if(email.isEmpty() && password.isEmpty()){
                Toast.makeText(LoginActivity.this,"Fields are empty", Toast.LENGTH_SHORT);
            } else if(!(email.isEmpty() && password.isEmpty())){
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Error, Please Login Again", Toast.LENGTH_SHORT);

                        } else {
                            Intent newIntent = new Intent(LoginActivity.this, UserAccountActivity.class);
                            startActivity(newIntent);

                        }
                    }
                });

            } else {
                Toast.makeText(LoginActivity.this,"Error occured!", Toast.LENGTH_SHORT);
            }
        });
    }

    private void setUpFirebase(){
        authStateListener = firebaseAuth -> {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                Toast.makeText(LoginActivity.this, "You are logged in",Toast.LENGTH_SHORT).show();
                Intent newIntent = new Intent(LoginActivity.this, UserAccountActivity.class);
                startActivity(newIntent);

            } else {
                Toast.makeText(LoginActivity.this,"Please Login", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void register(){
        registerTv.setOnClickListener(v -> {
            Intent newIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(newIntent);
        });

    }

    private void back(){
        backTv.setOnClickListener(v -> {
            Intent newIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(newIntent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
