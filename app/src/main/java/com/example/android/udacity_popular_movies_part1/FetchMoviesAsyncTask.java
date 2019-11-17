package com.example.android.udacity_popular_movies_part1;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class FetchMoviesAsyncTask extends AsyncTask<String, Void, Movie[]> {
    private final String TAG = FetchMoviesAsyncTask.class.getSimpleName(); // For logging
    private final String mApiKey; // TMDb API key
    private final OnTaskCompleted mListener;

    // Constructor
    public FetchMoviesAsyncTask(OnTaskCompleted listener, String apiKey) {
        super();

        mListener = listener;
        mApiKey = apiKey;
    }

    @Override
    protected Movie[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // String to hold movie data returned from the API
        String moviesJsonStr = null;

        try {
            URL url = buildUrl(params);

            // Start connecting to the TMDb server to get the movie JSON
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                return null;
            }

            StringBuilder builder = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            if (builder.length() == 0) {
                // If there's no data, nothing else to do here.
                return null;
            }

            moviesJsonStr = builder.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error getting movie JSON ", e);
            return null;
        } finally {
            // Tidy up: release url connection and buffered reader
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing reader ", e);
                }
            }
        }

        try {
            // Parse the JSON to get the movie data
            return extractMovieDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        super.onPostExecute(movies);

        // Notify the UI using the OnTaskCompleted listener
        mListener.onFetchMoviesTaskCompleted(movies);
    }

    // Function to build and return a querry URL.
    private URL buildUrl(String[] parameters) throws MalformedURLException {
        final String TMDB_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
        final String SORT_BY_PARAM = "sort_by";
        final String API_KEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendQueryParameter(SORT_BY_PARAM, parameters[0])
                .appendQueryParameter(API_KEY_PARAM, mApiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);
        return url;
    }

    // Function to parse the JSON string and return an array of Movie objects.
    private Movie[] extractMovieDataFromJson(String moviesJsonStr) throws JSONException {
        final String TAG_RESULTS = "results";
        final String TAG_ORIGINAL_TITLE = "original_title";
        final String TAG_POSTER_PATH = "poster_path";
        final String TAG_OVERVIEW = "overview";
        final String TAG_RELEASE_DATE = "release_date";
        final String TAG_VOTE_AVERAGE = "vote_average";

        // Get the array containing the movie data
        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray resultsArray = moviesJson.getJSONArray(TAG_RESULTS);

        // Create the array of Movie objects
        Movie[] movies = new Movie[resultsArray.length()];

        // Traverse the movie JSON array one by one and get the data for each movie
        for (int i = 0; i < resultsArray.length(); i++) {
            // Initialize each Movie object before it can be populated
            movies[i] = new Movie();

            // Object contains all the tags that we are looking for
            JSONObject movieInfo = resultsArray.getJSONObject(i);

            // Populate the data in movie object
            movies[i].setOriginalTitle(movieInfo.getString(TAG_ORIGINAL_TITLE));
            movies[i].setPosterPath(movieInfo.getString(TAG_POSTER_PATH));
            movies[i].setDescription(movieInfo.getString(TAG_OVERVIEW));
            movies[i].setReleaseDate(movieInfo.getString(TAG_RELEASE_DATE));
            movies[i].setVoteAverage(movieInfo.getDouble(TAG_VOTE_AVERAGE));
        }

        return movies;
    }
}
