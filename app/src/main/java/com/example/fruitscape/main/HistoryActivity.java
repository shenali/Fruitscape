package com.example.fruitscape.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import com.example.fruitscape.Adapters.HistoryAdapter;
import com.example.fruitscape.R;
import com.example.fruitscape.models.HistoryListItems;
import com.google.firebase.auth.FirebaseAuth;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.history_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();




        retriveData();

    }

    private void retriveData(){
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

                                             result,bitmap,stage

                                    );
                                    listItems.add(listItem);

                                listAdapter = new HistoryAdapter(listItems,HistoryActivity.this);
                                recyclerView.setAdapter(listAdapter);



//                                    Object valueForFirstKey = map.get(firstKey);
//                                    preHarvest.add(valueForFirstKey.toString());
//                                    listAdapter.notifyDataSetChanged();
//
//                                    listView.setAdapter(listAdapter);

                                }
//
//
//                            name.setText(dataSnapshot.child("Name").getValue().toString());
//                            email.setText(dataSnapshot.child("Email").getValue().toString());
//                            contactNumber.setText(dataSnapshot.child("Phone").getValue().toString());
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

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}