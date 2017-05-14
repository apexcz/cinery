package com.example.chineduoty.cinery;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import com.example.chineduoty.cinery.data.FavouriteContract;
import com.example.chineduoty.cinery.models.Movie;
import com.example.chineduoty.cinery.models.MovieResponse;
import com.example.chineduoty.cinery.utilities.ApiInterface;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, MoviesAdapter.MoviesAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView moviesRV;
    private TextView errorTv;
    private ProgressBar moviesLoader;
    private MoviesAdapter moviesAdapter;
    private List<Movie> movies = new ArrayList<Movie>();
    BaseUtils baseUtils;
    PrefUtils prefUtils;
    Gson gson = new Gson();
    String modePref;
    ApiInterface apiService;

    public static final String[] FAVOURITE_PROJECTION = new String[]{
            FavouriteContract.FavouriteEntry.POSTER_PATH,
            FavouriteContract.FavouriteEntry.ORIGINAL_TITLE
    };

    public static final int INDEX_POSTER_PATH = 0;
    public static final int INDEX_ORIGINAL_TITLE = 1;

    private static final int ID_FAVOURITE_LOADER = 321;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baseUtils = new BaseUtils(this);
        prefUtils = new PrefUtils(this);

        if (TextUtils.isEmpty(NetworkUtils.API_KEY_VALUE)) {
            Toast.makeText(this, "Invalid Api key", Toast.LENGTH_LONG).show();
            return;
        }
        moviesRV = (RecyclerView) findViewById(R.id.recyclerview_movies);
        errorTv = (TextView) findViewById(R.id.tv_error_message);
        moviesLoader = (ProgressBar) findViewById(R.id.pb_movies_loader);

        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(MainActivity.this, R.dimen.item_offset);
        int orient = getResources().getConfiguration().orientation;
        if (orient == Configuration.ORIENTATION_PORTRAIT) {
            moviesRV.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        } else {
            moviesRV.setLayoutManager(new GridLayoutManager(MainActivity.this, 4));
        }
        moviesAdapter = new MoviesAdapter(movies, MainActivity.this, MainActivity.this);
        moviesRV.setHasFixedSize(true);
        moviesRV.addItemDecoration(itemDecoration);
        moviesRV.setAdapter(moviesAdapter);

        apiService = NetworkUtils.getClient().create(ApiInterface.class);
        modePref = prefUtils.readPrefString(this.getString(R.string.movies_mode_pref), Constants.POPULAR);

        if (savedInstanceState != null && savedInstanceState.containsKey("moviesStateKey") && savedInstanceState.getParcelableArrayList("moviesStateKey") != null) {
            movies = savedInstanceState.getParcelableArrayList("moviesStateKey");
            showMovies();
            moviesAdapter.updateAdapter(movies);
        } else if (modePref == Constants.FAVOURITE) {
            loadFavourites();
        } else {
            loadMovieData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movies_menu, menu);
        MenuItem mitem;
        switch (modePref) {
            case Constants.TOP_RATED:
                mitem = menu.findItem(R.id.top_rated);
                mitem.setChecked(true);
                break;
            case Constants.FAVOURITE:
                mitem = menu.findItem(R.id.favourites);
                mitem.setChecked(true);
                break;
            default:
                mitem = menu.findItem(R.id.popular);
                mitem.setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        item.setChecked(true);
        if (itemId == R.id.popular && modePref != Constants.POPULAR) {
            prefUtils.savePref(this.getString(R.string.movies_mode_pref), Constants.POPULAR);
            modePref = Constants.POPULAR;
            loadMovieData();
        } else if (itemId == R.id.top_rated && modePref != Constants.TOP_RATED) {
            prefUtils.savePref(this.getString(R.string.movies_mode_pref), Constants.TOP_RATED);
            modePref = Constants.TOP_RATED;
            loadMovieData();
        } else if (itemId == R.id.favourites && modePref != Constants.FAVOURITE) {
            prefUtils.savePref(this.getString(R.string.movies_mode_pref), Constants.FAVOURITE);
            modePref = Constants.FAVOURITE;
            loadFavourites();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFavourites() {
        moviesLoader.setVisibility(View.VISIBLE);
        getSupportLoaderManager().initLoader(ID_FAVOURITE_LOADER, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("moviesStateKey", (ArrayList<Movie>) movies);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case ID_FAVOURITE_LOADER:
                Uri getAllFavourites = FavouriteContract.FavouriteEntry.CONTENT_URI;
                return new CursorLoader(this, getAllFavourites, null, null, null, null);
            default:
                throw new RuntimeException("Loader not implemented : " + loaderId);

        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        moviesAdapter.updateAdapter(movieCursorToList(data));
        moviesLoader.setVisibility(View.INVISIBLE);
        if (data.getCount() != 0) showMovies();
    }

    public List<Movie> movieCursorToList(Cursor cursor) {
        List<Movie> movieList = new ArrayList<Movie>();

        while (cursor.moveToNext()) {

            int movieId = cursor.getInt(cursor.getColumnIndex(FavouriteContract.FavouriteEntry.MOVIE_ID));
            String originalTitle = cursor.getString(cursor.getColumnIndex(FavouriteContract.FavouriteEntry.ORIGINAL_TITLE));
            String overView = cursor.getString(cursor.getColumnIndex(FavouriteContract.FavouriteEntry.OVERVIEW));
            String posterPath = cursor.getString(cursor.getColumnIndex(FavouriteContract.FavouriteEntry.POSTER_PATH));
            String backdropPath = cursor.getString(cursor.getColumnIndex(FavouriteContract.FavouriteEntry.BACKDROP_PATH));
            String releaseDate = cursor.getString(cursor.getColumnIndex(FavouriteContract.FavouriteEntry.RELEASE_DATE));
            Double voteAverage = cursor.getDouble(cursor.getColumnIndex(FavouriteContract.FavouriteEntry.VOTE_AVERAGE));

            Movie movie = new Movie(movieId, originalTitle, overView, posterPath, backdropPath, releaseDate, voteAverage);
            movieList.add(movie);
        }
        return movieList;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesAdapter.updateAdapter(null);
    }

    public void loadMovieData() {
        if (!baseUtils.isOnline()) {
            Toast.makeText(this, "Sorry cannot retrieve movies due to poor internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        Call<MovieResponse> call;
        if (modePref == Constants.TOP_RATED) {
            call = apiService.getTopRatedMovies(NetworkUtils.API_KEY_VALUE);
        } else {
            call = apiService.getPopularMovies(NetworkUtils.API_KEY_VALUE);
        }

        moviesLoader.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                int statusCode = response.code();
                movies.clear();
                movies = response.body().getResults();
                if (movies != null && movies.size() > 0) {
                    showMovies();
                    moviesAdapter.updateAdapter(movies);
                }
                moviesLoader.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                moviesLoader.setVisibility(View.INVISIBLE);
                showError();
            }
        });
    }

    private void showMovies() {
        errorTv.setVisibility(View.INVISIBLE);
        moviesRV.setVisibility(View.VISIBLE);
    }

    private void showError() {
        moviesRV.setVisibility(View.INVISIBLE);
        errorTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        String movieString = gson.toJson(movie);
        intent.putExtra(Intent.EXTRA_TEXT, movieString);
        startActivity(intent);
    }
}

