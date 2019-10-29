package com.example.webserverapp;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    public RequestQueue queue;
    public ArrayList<Movie> movies;
    public MovieAdapter adapter;
    public ListView moviesListView;
    public String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AddMovie.class);
                intent.putExtra("url", url);
                intent.putExtra("newID" , movies.size());
                startActivityForResult(intent, 2);
            }
        });

        movies = new ArrayList<>();

        webServerRequest();

        adapter = new MovieAdapter(getBaseContext(), R.layout.movie_item ,movies);
        moviesListView = findViewById(R.id.main_listView);
        moviesListView.setClickable(true);
        setClickEventListener();
        setLongEventListener();
        moviesListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void webServerRequest(){
        // instantiate the RequestQueue
        queue = Volley.newRequestQueue(this);
        url = "https://damirmovieapp.herokuapp.com/movie";

        // making the Json Request from the server
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // for request success

                        for (int i = 0; i < response.length() ; i++) {
                            JSONObject movie;
                            try {
                                // get a movie object
                                movie = response.getJSONObject(i);

                                // get all movies parameters
                                int id = movie.getInt("id");
                                String image = movie.getString("image");
                                String name = movie.getString("name");
                                String score = movie.getString("score");
                                String description = movie.getString("description");
                                String actors = movie.getString("actors");

                                // creating a new movie
                                Movie newMovie = new Movie(id, name, description, score, actors, image);

                                Log.d("movie name: " , name);
                                adapter.notifyDataSetChanged();

                                // add to movie to the movies list
                                movies.add(newMovie);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("Catch Note: ", "Cached an Exception");
                            }
                        }
                        // a note if request succeed
                        Toast.makeText(MainActivity.this, "success", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(MainActivity.this, "Request Error", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(jsonObjectRequest);
    }

    public void setClickEventListener(){
        moviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             Intent intent = new Intent(getBaseContext(), MovieDescription.class);
             intent.putExtra("movie", adapter.getItem(position));
             intent.putExtra("index", position);
             intent.putExtra("url", url);
             startActivityForResult(intent, 1);
            }
        });
    }

    public void setLongEventListener() {
        moviesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("The movie will be deleted from the list!")
                        .setConfirmText("Delete!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                Movie movie = (Movie) moviesListView.getItemAtPosition(position);
                                adapter.remove(movie);
                                deleteRequest(movie);
                                for (int i = movie.getId() - 1; i < movies.size() ; i++) {
                                    movies.get(i).setId(i + 1);
                                }
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    public void deleteRequest(Movie movie) {

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
                Request.Method.DELETE,
                url + "/" + movie.getId(),
                js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MainActivity.this, "" + response, Toast.LENGTH_SHORT).show();
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
        queue.add(putRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        // request code for updating -- 1
        if (requestCode == 1){
            if (resultCode == RESULT_FIRST_USER){
                // get data from editing activity ad result
                Movie newMovie = (Movie) data.getSerializableExtra("movie");
                int index = data.getIntExtra("index", -1);

                Log.d("result index: ", "" + index);
                // get the same index movie
                Movie oldMovie = adapter.getItem(index);

                // replace the data from old movie data to the new movie data
                assert newMovie != null;
                assert oldMovie != null;
                oldMovie.setName(newMovie.getName());
                oldMovie.setDescription(newMovie.getDescription());
                oldMovie.setActors(newMovie.getActors());

                // updating the list
                adapter.notifyDataSetChanged();

                Toast.makeText(this, "UPADATED!", Toast.LENGTH_SHORT).show();
            }
            if (resultCode == RESULT_CANCELED){
                Toast.makeText(getBaseContext(), "changes not made",Toast.LENGTH_SHORT).show();
            }
        }
        // request code for adding -- 2
        if (requestCode == 2){
            if (resultCode == RESULT_FIRST_USER){
                // get data from editing activity ad result
                Movie newMovie = (Movie) data.getSerializableExtra("newMovie");

                if (newMovie != null) {
                    // add new movie to the list and updating the list
                    adapter.add(newMovie);

                    Toast.makeText(getBaseContext(), "movie added!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "adapter not updated!",Toast.LENGTH_SHORT).show();
                }

            }
            if (resultCode == RESULT_CANCELED){
                Toast.makeText(getBaseContext(), "not movie added!",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
