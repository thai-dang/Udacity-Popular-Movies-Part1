package com.example.android.udacity_popular_movies_part1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class MainActivity extends AppCompatActivity {
    // For logging
    private final String TAG = MainActivity.class.getSimpleName();

    private GridView mGridView;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridView = (GridView)findViewById(R.id.gridview);
        mGridView.setOnItemClickListener(moviePosterClickListener);

        if (savedInstanceState == null) {
            getMoviesFromTMDb(getSortMethod());
        } else {
            Parcelable[] parcelable = savedInstanceState.getParcelableArray(getString(R.string.parcel_movie));

            if (parcelable != null) {
                int nMovieObjects = parcelable.length;
                Movie[] movies = new Movie[nMovieObjects];
                for (int i = 0; i < nMovieObjects; i++) {
                    movies[i] = (Movie)parcelable[i];
                }

                // Load movie objects into the view
                mGridView.setAdapter(new ImageAdapter(this, movies));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, mMenu);

        // Save the menu to the member variable
        mMenu = menu;

        // Add menu items
        mMenu.add(Menu.NONE,
                R.string.pref_sort_pop_desc_key,
                Menu.NONE,
                null)
                .setVisible(false)
                .setIcon(R.drawable.ic_action_whatshot)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        mMenu.add(Menu.NONE,
                R.string.pref_sort_vote_avg_desc_key,
                Menu.NONE,
                null)
                .setVisible(false)
                .setIcon(R.drawable.ic_action_poll)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        // Update menu to show relevant items
        updateMenu();

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int nMovieObjects = mGridView.getCount();
        if (nMovieObjects > 0) {
            // Retrieve all Movie objects from the gridview
            Movie[] movies = new Movie[nMovieObjects];
            for (int i = 0; i < nMovieObjects; i++) {
                movies[i] = (Movie)mGridView.getItemAtPosition(i);
            }

            // Save all Movie objects to the output bundle
            outState.putParcelableArray(getString(R.string.parcel_movie), movies);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.string.pref_sort_pop_desc_key:
                saveSortMethod(getString(R.string.tmdb_sort_pop_desc));
                updateMenu();
                getMoviesFromTMDb(getSortMethod());
                return true;
            case R.string.pref_sort_vote_avg_desc_key:
                saveSortMethod(getString(R.string.tmdb_sort_vote_avg_desc));
                updateMenu();
                getMoviesFromTMDb(getSortMethod());
                return true;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    // Listener for click on the movie poster in GridView
    private final GridView.OnItemClickListener moviePosterClickListener = new GridView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Movie movie = (Movie) parent.getItemAtPosition(position);

            Intent intent = new Intent(getApplicationContext(), MovieDetailsActivity.class);
            intent.putExtra(getResources().getString(R.string.parcel_movie), movie);

            startActivity(intent);
        }
    };

    // Activate the async task to retrieve the movie data from TMDb server
    private void getMoviesFromTMDb(String sortMethod) {
        // This is the API key needed to get data from TMDb server
        String apiKey = getString(R.string.my_moviedb_key);

        // Listener for when the async task is done and ready to update the UI
        OnTaskCompleted taskCompleted = new OnTaskCompleted() {
            @Override
            public void onFetchMoviesTaskCompleted(Movie[] movies) {
                mGridView.setAdapter(new ImageAdapter(getApplicationContext(), movies));
            }
        };

        // Execute the task
        FetchMoviesAsyncTask movieTask = new FetchMoviesAsyncTask(taskCompleted, apiKey);
        movieTask.execute(sortMethod);
    }

    // Update the menu based on the sort method saved in SharedPreferences
    private void updateMenu() {
        String sortMethod = getSortMethod();

        if (sortMethod.equals(getString(R.string.tmdb_sort_pop_desc))) {
            mMenu.findItem(R.string.pref_sort_pop_desc_key).setVisible(false);
            mMenu.findItem(R.string.pref_sort_vote_avg_desc_key).setVisible(true);
        } else {
            mMenu.findItem(R.string.pref_sort_vote_avg_desc_key).setVisible(false);
            mMenu.findItem(R.string.pref_sort_pop_desc_key).setVisible(true);
        }
    }

    // Get the sort method saved in SharedPreferences
    private String getSortMethod() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        return sharedPrefs.getString(
                getString(R.string.pref_sort_method_key),
                getString(R.string.tmdb_sort_pop_desc));
    }

    // Save the selected sort method into SharedPreferences
    private void saveSortMethod(String sortMethod) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(getString(R.string.pref_sort_method_key), sortMethod);
        editor.apply();
    }
}
