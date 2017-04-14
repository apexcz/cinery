package com.example.chineduoty.cinery;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chineduoty.cinery.adapters.ItemOffsetDecoration;
import com.example.chineduoty.cinery.adapters.MoviesAdapter;
import com.example.chineduoty.cinery.models.Movie;
import com.example.chineduoty.cinery.utilities.BaseUtils;
import com.example.chineduoty.cinery.utilities.NetworkUtils;
import com.example.chineduoty.cinery.utilities.PrefUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView moviesRV;
    private TextView errorTv;
    private ProgressBar moviesLoader;
    private MoviesAdapter moviesAdapter;
    ArrayList<Movie> movies;
    BaseUtils baseUtils;
    PrefUtils prefUtils;
    Gson gson = new Gson();
    String modePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        baseUtils = new BaseUtils(this);
        prefUtils = new PrefUtils(this);

        moviesRV = (RecyclerView)findViewById(R.id.recyclerview_movies);
        errorTv = (TextView)findViewById(R.id.tv_error_message);
        moviesLoader = (ProgressBar)findViewById(R.id.pb_movies_loader);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        moviesAdapter = new MoviesAdapter(this.getApplicationContext(),this);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);

        moviesRV.setLayoutManager(gridLayoutManager);
        moviesRV.setHasFixedSize(true);
        moviesRV.setAdapter(moviesAdapter);
        moviesRV.addItemDecoration(itemDecoration);


        modePref = prefUtils.readPrefString(this.getString(R.string.movies_mode_pref), Constants.POPULAR);
        if(savedInstanceState != null && savedInstanceState.containsKey("moviesStateKey"))
        {
            movies = savedInstanceState.getParcelableArrayList("moviesStateKey");
            showMovies();
            moviesAdapter.updateAdapter(movies);
        }
        else {
            loadMovieData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.movies_menu,menu);

        MenuItem mitem;
        switch (modePref){
            case Constants.TOP_RATED:
                mitem=menu.findItem(R.id.top_rated);
                mitem.setChecked(true);
                break;
            default:
                mitem=menu.findItem(R.id.popular);
                mitem.setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        item.setChecked(true);

        if(itemId == R.id.popular && modePref != Constants.POPULAR)
        {
            prefUtils.savePref(this.getString(R.string.movies_mode_pref),Constants.POPULAR);
            modePref = Constants.POPULAR;
            loadMovieData();
        }
        else if(itemId == R.id.top_rated && modePref != Constants.TOP_RATED)
        {
            prefUtils.savePref(this.getString(R.string.movies_mode_pref),Constants.TOP_RATED);
            modePref = Constants.TOP_RATED;
            loadMovieData();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("moviesStateKey",movies);
        super.onSaveInstanceState(outState);
    }

    private void loadMovieData(){
        if(baseUtils.isOnline()) {
            new FetchMoviesTask().execute(modePref);
        }
        else
            Toast.makeText(this,"Sorry cannot retrieve movies due to poor internet connection",Toast.LENGTH_LONG).show();
    }

    private void showMovies(){
        errorTv.setVisibility(View.INVISIBLE);
        moviesRV.setVisibility(View.VISIBLE);
    }

    private void showError(){
        moviesRV.setVisibility(View.INVISIBLE);
        errorTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(MainActivity.this,DetailActivity.class);
        String movieString = gson.toJson(movie);
        intent.putExtra(Intent.EXTRA_TEXT,movieString);
        startActivity(intent);
    }

    public class FetchMoviesTask extends AsyncTask<String,Void,ArrayList<Movie>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            moviesLoader.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            ArrayList<Movie> lstMovie = new ArrayList<Movie>();

            String mode = Constants.POPULAR;
            if(params.length == 1)
                mode = params[0];

            URL moviesRequestUrl = NetworkUtils.buildUrl(mode);

            try{
                String jsonResponse = NetworkUtils.makeHttpCall(moviesRequestUrl);

                JSONObject responseObject = new JSONObject(jsonResponse);
                JSONArray resultsArray = responseObject.getJSONArray("results");
                if(resultsArray == null)
                    return null;
                for (int i = 0; i < resultsArray.length(); i++) {
                    lstMovie.add(gson.fromJson(resultsArray.getJSONObject(i).toString(), Movie.class));
                }
                movies = lstMovie;
                return lstMovie;
            }
            catch (IOException ex){
                ex.printStackTrace();
                return null;
            }
            catch (JSONException ex){
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);
            moviesLoader.setVisibility(View.INVISIBLE);
            if(movies != null && movies.size() > 0){
                showMovies();
                moviesAdapter.updateAdapter(movies);
            }
            else {
                showError();
            }
        }
    }
}
