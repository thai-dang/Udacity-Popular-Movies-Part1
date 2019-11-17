package com.example.android.udacity_popular_movies_part1;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private static final String DATE_FORMAT = "yyyy-mm-dd";
    private static final String TMDB_POSTER_BASE_URL = "https://image.tmdb.org/t/p/w185";
    private String mOriginalTitle;
    private String mPosterPath;
    private String mDescription;
    private Double mVoteAverage;
    private String mReleaseDate;

    // Constructor
    public Movie() {
    }

    public void setOriginalTitle(String originalTitle) {
        mOriginalTitle = originalTitle;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    public void setDescription(String description) {
        if(!description.equals("null")) {
            mDescription = description;
        }
    }

    public void setVoteAverage(Double voteAverage) {
        mVoteAverage = voteAverage;
    }

    public void setReleaseDate(String releaseDate) {
        if(!releaseDate.equals("null")) {
            mReleaseDate = releaseDate;
        }
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getPosterPath() {
        return TMDB_POSTER_BASE_URL + mPosterPath;
    }

    public String getDescription() {
        return mDescription;
    }

    public Double getVoteAverage() {
        return mVoteAverage;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getDetailedVoteAverage() {
        return String.valueOf(getVoteAverage()) + " / 10";
    }

    public String getDateFormat() {
        return DATE_FORMAT;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mOriginalTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mDescription);
        dest.writeValue(mVoteAverage);
        dest.writeString(mReleaseDate);
    }

    private Movie(Parcel in) {
        mOriginalTitle = in.readString();
        mPosterPath = in.readString();
        mDescription = in.readString();
        mVoteAverage = (Double)in.readValue(Double.class.getClassLoader());
        mReleaseDate = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
