package com.example.chineduoty.cinery.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chineduoty on 4/13/17.
 */

public class Movie implements Parcelable {

    private String original_title;
    private String poster_path;
    private String overview;
    private String backdrop_path;
    private  String release_date;
    private float vote_average;

    protected Movie(Parcel in) {
        original_title = in.readString();
        poster_path = in.readString();
        overview = in.readString();
        backdrop_path = in.readString();
        release_date = in.readString();
        vote_average = in.readFloat();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public float getVote_average() {
        return vote_average;
    }

    public void setVote_average(float vote_average) {
        this.vote_average = vote_average;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(original_title);
        parcel.writeString(poster_path);
        parcel.writeString(overview);
        parcel.writeString(backdrop_path);
        parcel.writeString(release_date);
        parcel.writeFloat(vote_average);
    }

}
