package com.example.fruitscape.Main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fruitscape.Adapters.ExpandableSuggestions;
import com.example.fruitscape.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private TextView mResultTextView, resultStageTV, confidenceTV;
    private Button savePredictionsBtn;
    private ImageView selectedImage, contouredImage;
    private Bitmap bitmap;
    private ExpandableListView listView;
    private ExpandableSuggestions listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;
    private List<String> preHarvest;
    private List<String> postHarvest;
    private List<String> nonChemical;
    private String stage;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private BottomNavigationView bottomNav;
    String[] splitString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        firebaseAuth = FirebaseAuth.getInstance();
        resultStageTV = findViewById(R.id.result_stage);
        confidenceTV = findViewById(R.id.prediction_confidence);
        // stageText = findViewById(R.id.stage_text);
        selectedImage = findViewById(R.id.selected_image);
        mResultTextView = findViewById(R.id.result_text_view);
        contouredImage = findViewById(R.id.contoured_image);
        savePredictionsBtn = findViewById(R.id.saveBtn);
        bottomNav = findViewById(R.id.bottom_nav);
        setUpPredictions();
        listView = (ExpandableListView) findViewById(R.id.expandableLv);
        listAdapter = new ExpandableSuggestions(this, listHash, listDataHeader);
        checkIfUserLogged();
        setUpNav();

    }

    private void setUpPredictions() {
        String result = getIntent().getStringExtra("result");
        bitmap = getIntent().getParcelableExtra("bitmap");
        String confidence = getIntent().getStringExtra("confidence");

        selectedImage.setImageBitmap(bitmap);
        String currentString = result;
        splitString = currentString.split("_");
        mResultTextView.setText(splitString[0]);
        confidenceTV.setText("Prediction Confidence : " + confidence);

        if (splitString[0].equals("Healthy")) {
            resultStageTV.setText("Fruit has no diseases");
            contouredImage.setImageBitmap(bitmap);
//            stageText.setVisibility(View.GONE);
        } else {
            resultStageTV.setVisibility((View.VISIBLE));
            //stageText.setVisibility(View.VISIBLE);
            resultStageTV.setText("Infection stage : " + splitString[1]);
            stage = splitString[1];
            contourImage();
            setControlMeasures();
        }


    }

    private void contourImage() {

        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY);

        Imgproc.Canny(gray, gray, 100, 170);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        // find contours in the image
        Imgproc.findContours(gray, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            Imgproc.drawContours(src, contours, contourIdx, new Scalar(0, 0, 255), -1);

        }
        // create a blank temporary bitmap
        Bitmap tempBmp1 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                bitmap.getConfig());

        Utils.matToBitmap(src, tempBmp1);
        contouredImage.setImageBitmap(tempBmp1);
    }


    private void setControlMeasures() {
        DatabaseReference ref;
        preHarvest = new ArrayList<>();
        postHarvest = new ArrayList<>();
        nonChemical = new ArrayList<>();

        if (stage.equals("stage1")) {
            ref = FirebaseDatabase.getInstance().getReference().child("Recommendations").child("Stage1");
        } else {
            ref = FirebaseDatabase.getInstance().getReference().child("Recommendations").child("Stage2");
        }

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {


                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.child("pre").getValue();
                    for (int i = 0; i < map.size(); i++) {
                        Object firstKey = map.keySet().toArray()[i];
                        Object valueForFirstKey = map.get(firstKey);
                        preHarvest.add(valueForFirstKey.toString());
                        listAdapter.notifyDataSetChanged();

                        listView.setAdapter(listAdapter);
                    }

                    Map<String, Object> map2 = (Map<String, Object>) dataSnapshot.child("post").getValue();
                    for (int i = 0; i < map2.size(); i++) {
                        Object firstKey = map2.keySet().toArray()[i];
                        Object valueForFirstKey = map2.get(firstKey);
                        postHarvest.add(valueForFirstKey.toString());
                        listAdapter.notifyDataSetChanged();

                        listView.setAdapter(listAdapter);
                    }


                    Map<String, Object> map3 = (Map<String, Object>) dataSnapshot.child("nonChemical").getValue();
                    for (int i = 0; i < map3.size(); i++) {
                        Object firstKey = map3.keySet().toArray()[i];
                        Object valueForFirstKey = map3.get(firstKey);
                        nonChemical.add(valueForFirstKey.toString());
                        listAdapter.notifyDataSetChanged();

                        listView.setAdapter(listAdapter);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("Pre Harvest Control Measures");
        listDataHeader.add("Post Harvest Control Measures");
        listDataHeader.add("Post Harvest Non Chemical Methods");

        listHash.put(listDataHeader.get(0), preHarvest);
        listHash.put(listDataHeader.get(1), postHarvest);
        listHash.put(listDataHeader.get(2), nonChemical);
    }

    private void savePredictions() {
        savePredictionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = firebaseAuth.getCurrentUser().getUid();
                if (!user_id.equals(null)){
                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                String i = timeStamp();

                current_user_db.child("SavedData").child(i).child("result").setValue(splitString[0]);
                current_user_db.child("SavedData").child(i).child("bitmap").setValue(BitMapToString(bitmap));

                if (splitString[0].equals("Healthy")) {
                    current_user_db.child("SavedData").child(i).child("stage").setValue("");
                    current_user_db.child("SavedData").child(i).child("pre").setValue("");
                    current_user_db.child("SavedData").child(i).child("post").setValue("");
                    current_user_db.child("SavedData").child(i).child("nonChemical").setValue("");
                } else {

                    current_user_db.child("SavedData").child(i).child("stage").setValue(stage);
                    current_user_db.child("SavedData").child(i).child("pre").setValue(preHarvest);
                    current_user_db.child("SavedData").child(i).child("post").setValue(postHarvest);
                    current_user_db.child("SavedData").child(i).child("nonChemical").setValue(nonChemical);
                }

                Toast.makeText(ResultActivity.this, "Prediction Saved", Toast.LENGTH_SHORT).show();
            }
                else {
                    Toast.makeText(ResultActivity.this, "Sign up first!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void checkIfUserLogged() {

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    Toast.makeText(ResultActivity.this, "Sign in or Sign up first!", Toast.LENGTH_SHORT).show();

                } else {
                    savePredictions();
                }
            }
        };

    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public String timeStamp() {
        Date date = new Date();
        long time = date.getTime();
        String timeS = Long.toString(time);

        System.out.println("Time in Milliseconds: " + timeS);

        return timeS;

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
