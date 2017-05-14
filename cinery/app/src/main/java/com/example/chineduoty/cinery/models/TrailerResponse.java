package com.example.chineduoty.cinery.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chineduoty on 5/11/17.
 */

public class TrailerResponse {
    private int id;

    private List<Trailer> results = new ArrayList<Trailer>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Trailer> getResults() {
        return results;
    }

    public void setResults(List<Trailer> results) {
        this.results = results;
    }
}
