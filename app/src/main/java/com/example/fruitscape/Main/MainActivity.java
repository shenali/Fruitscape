package com.example.fruitscape.Main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fruitscape.R;
import com.example.fruitscape.Classifiers.ModelClassifier;
import com.example.fruitscape.Classifiers.Configuration;
import com.example.fruitscape.utils.ImageUtils;
import com.example.fruitscape.POJOModels.PredictionResult;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    private int mIndex = 0;
    private static final String IMAGE_DIRECTORY = "/FruitScape";
    private int GALLERY = 1, CAMERA = 2;

    private ImageView papayaIv;
    private Button selectImageBtn, classifyBtn;
    private ModelClassifier modelClassifier;
    private View view;
    private BottomNavigationView bottomNav;

    private static String TAG= "MainActivity";

    static {
        if(OpenCVLoader.initDebug()){
            Log.d(TAG, "OPENCV CONNECTED");
        } else {
            Log.d(TAG, "OPENCV not working");
        }
    }

    public static Intent getIntent(Context packageContext) {
        return new Intent(packageContext, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        papayaIv = findViewById(R.id.plant_image_view);
        selectImageBtn = findViewById(R.id.random_choose_button);
        classifyBtn = findViewById(R.id.classifiy_button);
        bottomNav = findViewById(R.id.bottom_nav);

        requestMultiplePermissions();
        loadImageClassifier();
        setBtnListeners();
        setUpNav();
    }

    private void loadImageClassifier() {
        try {
            modelClassifier = ModelClassifier.classifierGenerator(getAssets(), Configuration.TFLITE_MODEL);
        } catch (IOException e) {
            Toast.makeText(this, "Couldn't load model", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void setBtnListeners() {
        selectImageBtn.setOnClickListener(v -> {
            showPictureDialog();
        });

        classifyBtn.setOnClickListener(v -> predictInputImage());
    }

    public void predictInputImage() {
        if (papayaIv.getDrawable() != null) {
            Bitmap bitmapVal = ((BitmapDrawable) papayaIv.getDrawable()).getBitmap();
            bitmapVal = ThumbnailUtils.extractThumbnail(bitmapVal, getScreenWidth(), getScreenWidth());
            bitmapVal = ImageUtils.prepareImageForClassification(bitmapVal);

            List<PredictionResult> resultList = modelClassifier.predictImage(bitmapVal);
            PredictionResult predictionResult = resultList.get(0);
            String confidence = String.format(Locale.getDefault(), "%.2f %%", predictionResult.predictionConfidence * 100);

            Intent intent = new Intent(getBaseContext(), ResultActivity.class);
            intent.putExtra("result", predictionResult.predictionLabel);
            intent.putExtra("bitmap", bitmapVal);
            intent.putExtra("confidence", confidence);

            startActivity(intent);

        } else {
            Toast.makeText(MainActivity.this, "Choose an Image first", Toast.LENGTH_SHORT).show();
        }
    }



    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }


    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    saveImage(bitmap);
                    Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    papayaIv.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            papayaIv.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();

            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::---&gt;" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
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
