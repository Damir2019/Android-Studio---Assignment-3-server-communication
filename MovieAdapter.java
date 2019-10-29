package com.example.webserverapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends ArrayAdapter<Movie> {

    private Context _context;
    private ArrayList<Movie> _movies;
    private int _layout;

    MovieAdapter(Context _context , int _layout ,  ArrayList<Movie> _movies) {
        super(_context, _layout, _movies);
        this._context = _context;
        this._movies = _movies;
        this._layout = _layout;
    }

    @Override
    public View getView(int position, View convertView,@Nullable ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(_context);
            convertView = layoutInflater.inflate(_layout, parent, false);
        }

        // get object parameters
        Movie movie = _movies.get(position);

        String name = movie.getName();
        String description = movie.getDescription();
        String score = movie.getScore();
        String actors = movie.getActors();
        String image = movie.getImage();

        // get all views
        ImageView imageView = convertView.findViewById(R.id.movie_image);
        TextView nameView = convertView.findViewById(R.id.movie_title);
        TextView descriptionView = convertView.findViewById(R.id.movie_shortDes);
        TextView scoreView = convertView.findViewById(R.id.movie_score);
        TextView actorsView = convertView.findViewById(R.id.movie_actors);

        // set all data in view
        if (image.equals("none")){
            Picasso.with(_context).load(R.drawable.place_holder).into(imageView);
        } else{
            Picasso.with(_context).load(image).into(imageView);
        }
        nameView.setText(name);
        descriptionView.setText(description);
        scoreView.setText(score);
        actorsView.setText(actors);

        return convertView;
    }

}
