package com.example.webserverapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MovieDescription extends AppCompatActivity {

    TextView desTitle, desDescription, desActors;
    ImageView desImage;
    Movie movie;
    int index;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get all views from layout
        setContentView(R.layout.activity_movie_description);
        desImage = findViewById(R.id.movieDes_image);
        desTitle = findViewById(R.id.movieDes_name);
        desDescription = findViewById(R.id.movieDes_description);
        desActors = findViewById(R.id.movieDes_actors);
        Button saveBtn = findViewById(R.id.movieDes_save);

        // get the movie object from main intent
        final Intent intent = getIntent();
        movie = (Movie) intent.getSerializableExtra("movie");
        index = intent.getIntExtra("index", -1);
        url = intent.getStringExtra("url");

        // put the original information about the movie in the Edit Text
        assert movie != null;
        if (movie.getImage().equals("none")){
            Picasso.with(getBaseContext()).load(R.drawable.place_holder).into(desImage);
        } else {
            Picasso.with(getBaseContext()).load(movie.getImage()).into(desImage);
        }
        desTitle.setText(movie.getName());
        desDescription.setText(movie.getDescription());
        desActors.setText(movie.getActors());

        // create a listener for the save button
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnNewData();
            }
        });
    }

    public void returnNewData(){
        // get all edited data from edit text
        String newTitle = desTitle.getText().toString();
        String newDescription = desDescription.getText().toString();
        String newActors = desActors.getText().toString();

        if (newTitle.equals("") || newDescription.equals("") || newActors.equals("")) {
            Toast.makeText(MovieDescription.this, "Missing Values", Toast.LENGTH_SHORT).show();
        }else if (newTitle.equals(movie.getName()) && newDescription.equals(movie.getDescription()) && newActors.equals(movie.getActors())){
            Toast.makeText(this, "No changes has made", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // put new data on movie object
            movie.setName(newTitle);
            movie.setDescription(newDescription);
            movie.setActors(newActors);

            // put all the information back to main activity
            Intent intentBack =new Intent();
            intentBack.putExtra("movie", movie);
            intentBack.putExtra("index", index);
            Log.d("index before: " , "" + index);
            setResult(RESULT_FIRST_USER, intentBack);
            putRequestToServer();
            finish();
        }
    }

    public void putRequestToServer() {

        JSONObject js = new JSONObject();
        try {
            js.put("id", String.valueOf(movie.getId()));
            js.put("name", movie.getName());
            js.put("description", movie.getDescription());
            js.put("score", movie.getScore());
            js.put("actors", movie.getActors());
            js.put("image", movie.getImage());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest putRequest = new JsonObjectRequest(
                Request.Method.PUT,
                url + "/" + movie.getId(),
                js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MovieDescription.this, "" + response, Toast.LENGTH_SHORT).show();
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
                Log.d("put 2: " , "loaded 2");
                return params;
            }
        };
        queue.add(putRequest);
    }
}
