package com.example.android.udacity_popular_movies_part1;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;

public class MovieDetailsActivity extends AppCompatActivity {
     // For logging
    private final String TAG = MovieDetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ImageView ivPoster = (ImageView) findViewById(R.id.imageview_poster);
        TextView tvOriginalTitle = (TextView) findViewById(R.id.textview_original_title);
        TextView tvReleaseDate = (TextView) findViewById(R.id.textview_release_date);
        TextView tvVoteAverage = (TextView) findViewById(R.id.textview_vote_average);
        TextView tvDescription = (TextView) findViewById(R.id.textview_description);

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(getString(R.string.parcel_movie));

        // Populate Movie Original Title
        tvOriginalTitle.setText(movie.getOriginalTitle());

        // Display movie poster thumbnail
        Picasso.get()
                .load(movie.getPosterPath())
                .resize(getResources().getInteger(R.integer.tmdb_poster_w185_width),
                        getResources().getInteger(R.integer.tmdb_poster_w185_height))
                .error(R.drawable.not_found)
                .placeholder(R.drawable.searching)
                .into(ivPoster);

        // Populate Release Date
        String releaseDate = movie.getReleaseDate();
        if (releaseDate != null) {
            try {
                releaseDate = DateTimeUtils.getLocalizedDate(this, releaseDate, movie.getDateFormat());
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing release date", e);
            }
        } else {
            tvReleaseDate.setTypeface(null, Typeface.ITALIC);
            releaseDate = getResources().getString(R.string.no_release_date_found);
        }
        tvReleaseDate.setText(releaseDate);

        // Populate Vote Average
        tvVoteAverage.setText(movie.getDetailedVoteAverage());

        // Populate Movie Description
        String description = movie.getDescription();
        if (description == null) {
            tvDescription.setTypeface(null, Typeface.ITALIC);
            description = getResources().getString(R.string.no_description_found);
        }
        tvDescription.setText(description);
    }
}
