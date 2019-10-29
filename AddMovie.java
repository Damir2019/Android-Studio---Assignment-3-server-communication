package com.example.webserverapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddMovie extends AppCompatActivity {

    private EditText addName, addDescription, addActors, addScore;
    private ImageView addViewedImage;
    private Uri addImage = null;
    private String url, movieName, movieDescription, movieActors, movieScore, movieImage;
    private int newID;
    private final int PICK_IMAGE = 100;
    private final int STORAGE_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        // get intent and get the data from main activity
        Intent intent= getIntent();
        url = intent.getStringExtra("url");
        newID = intent.getIntExtra("newID", -1) + 1;

        addViewedImage = findViewById(R.id.add_movie_image);
        addName = findViewById(R.id.add_movie_title);
        addDescription = findViewById(R.id.add_movie_description);
        addActors = findViewById(R.id.add_movie_actors);
        addScore = findViewById(R.id.add_movie_score);

        final Button addMovieBtn = findViewById(R.id.add_movie_addBtn);

        addViewedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // asking permission to access the gallery
                // for granted -> open the gallery on phone, for denied -> start requestStoragePermission function
                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    requestStoragePermission();
                }
            }
        });

        addMovieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postRequest();
                Movie newMovie = new Movie(newID, movieName, movieDescription, movieScore, movieActors, movieImage);
                Intent backToMainActivity = new Intent();
                backToMainActivity.putExtra("newMovie", newMovie);
                setResult(RESULT_FIRST_USER, backToMainActivity);
                finish();
            }
        });

    }

    public void postRequest() {

        // get the data from edit text
        movieName = addName.getText().toString();
        movieDescription = addDescription.getText().toString();
        movieActors = addActors.getText().toString();
        movieScore = addScore.getText().toString();
        if (addImage != null) {
            movieImage = String.valueOf(addImage);
        } else {
            movieImage = "none";
        }

        if (movieName.equals("") || movieDescription.equals("") || movieActors.equals("") || movieScore.equals("")){
            Toast.makeText(this, "you have to fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // creating new JSON Object with the new movie data
        JSONObject js = new JSONObject();
        try {
            js.put("id", newID);
            js.put("name", movieName);
            js.put("description", movieDescription);
            js.put("score", movieScore);
            js.put("actors", movieActors);
            js.put("image", movieImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // make a request for POST
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest postRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(AddMovie.this, "" + response, Toast.LENGTH_SHORT).show();
                        Log.d("put: ", "loaded: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset-utf-8");
                return params;
            }
        };
        queue.add(postRequest);
    }

    // opening gallery after permission asked
    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    // using the result code got from gallery after choosing a picture and setting it on the date
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null){
            return;
        }
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            addImage = data.getData();
            addViewedImage.setImageURI(addImage);
        }
    }

    // if permission denied once this function activated and open a dialog
    // alert explaining why permission is needed at the first place
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this).setTitle("Permission Needed!")
                    .setMessage("Permission needed to select picture!")
                    .setPositiveButton("AGREE!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(AddMovie.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("NEVER!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    // result action for alert dialog
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }
    }
}
