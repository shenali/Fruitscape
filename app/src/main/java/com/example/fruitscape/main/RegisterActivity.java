package com.example.fruitscape.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fruitscape.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameVal, emailId, passwordId, contactNum;
    private Button registerBtn;
    private TextView loginTv;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.emailEt);
        passwordId = findViewById(R.id.passwordEt);
        registerBtn = findViewById(R.id.registerBtn);
        loginTv = findViewById(R.id.signinTv);
        contactNum = findViewById(R.id.contactEt);
        nameVal = findViewById(R.id.nameEt);

        //userAddedConfirmation();
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameVal.getText().toString();
                String emailVal = emailId.getText().toString();
                String password = passwordId.getText().toString();
                String contactNumber = contactNum.getText().toString();

                if(emailVal.isEmpty()){
                    emailId.setError("Please enter email id");
                    emailId.requestFocus();
                } else if(password.isEmpty()){
                    passwordId.setError("Please enter the password");
                    passwordId.requestFocus();
                } else {
                    if(emailVal.isEmpty() && password.isEmpty() && name.isEmpty() && contactNumber.isEmpty()){
                        Toast.makeText(RegisterActivity.this,"Fields are empty", Toast.LENGTH_SHORT);
                    } else if(!(emailVal.isEmpty() && password.isEmpty() && name.isEmpty() && contactNumber.isEmpty())){
                        firebaseAuth.createUserWithEmailAndPassword(emailVal,password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(!task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this,"Register unsuccessful,Please try again", Toast.LENGTH_SHORT);
                                } else {
    
                                    String user_id = firebaseAuth.getCurrentUser().getUid();
                                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                                    current_user_db.setValue(true);
                                    DatabaseReference userName = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("Name");
                                    userName.setValue(name);
                                    DatabaseReference email = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("Email");
                                    email.setValue(emailVal);
                                    DatabaseReference number = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("Phone");
                                    number.setValue(contactNumber);
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    Toast.makeText(RegisterActivity.this,"You are registered", Toast.LENGTH_SHORT);
                                   // userAddedConfirmation();
                                }
                            }
                        });
    
                    } else {
                        Toast.makeText(RegisterActivity.this,"Error occured!", Toast.LENGTH_SHORT);
                    }
                }

            }
        });

        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }
//
//    private void userAddedConfirmation() {
//        authStateListener = firebaseAuth -> {
//            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            if (user!=null){
//                startActivity(new Intent(RegisterActivity.this, UserAccountActivity.class));
//                finish();
//                return;
//            }
//        };
//    }


}
