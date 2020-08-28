package com.example.fruitscape.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.example.fruitscape.Adapters.HistoryAdapter;
import com.example.fruitscape.R;
import com.example.fruitscape.POJOModels.HistoryListItems;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter listAdapter;
    private List<HistoryListItems> listItems;
    private String result;
    private Bitmap bitmap;
    private String stage;
    private BottomNavigationView bottomNav;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.history_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bottomNav = findViewById(R.id.bottom_nav);
        listItems = new ArrayList<>();

        checkIfUserLogged();
        setUpNav();


    }

    private void checkIfUserLogged() {

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    Toast.makeText(HistoryActivity.this, "Sign in or Sign up first!", Toast.LENGTH_SHORT).show();

                } else {
                    retriveData();
                }
            }
        };

    }

    private void retriveData() {
        {
            String userId = FirebaseAuth.getInstance().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("SavedData");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //        Setting the values
                    try {
                        if (dataSnapshot.exists()) {

                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                            for (int i = 0; i < map.size(); i++) {

                                Object firstKey = map.keySet().toArray()[i];
                                String randomNumber = firstKey.toString();
                                result = dataSnapshot.child(randomNumber).child("result").getValue().toString();
                                bitmap = StringToBitMap(dataSnapshot.child(randomNumber).child("bitmap").getValue().toString());
                                stage = dataSnapshot.child(randomNumber).child("stage").getValue().toString();

                                HistoryListItems listItem = new HistoryListItems(

                                        result, bitmap, stage

                                );
                                listItems.add(listItem);

                                listAdapter = new HistoryAdapter(listItems, HistoryActivity.this);
                                recyclerView.setAdapter(listAdapter);

                            }

                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
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