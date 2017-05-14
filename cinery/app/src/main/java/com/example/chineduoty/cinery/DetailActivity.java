package com.example.chineduoty.cinery;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chineduoty.cinery.adapters.ItemOffsetDecoration;
import com.example.chineduoty.cinery.adapters.ReviewAdapter;
import com.example.chineduoty.cinery.adapters.TrailerAdapter;
import com.example.chineduoty.cinery.adapters.TrailerAdapterOld;
import com.example.chineduoty.cinery.data.FavouriteContract;
import com.example.chineduoty.cinery.models.Movie;
import com.example.chineduoty.cinery.models.Review;
import com.example.chineduoty.cinery.models.ReviewResponse;
import com.example.chineduoty.cinery.models.Trailer;
import com.example.chineduoty.cinery.models.TrailerResponse;
import com.example.chineduoty.cinery.utilities.ApiInterface;
import com.example.chineduoty.cinery.utilities.BaseUtils;
import com.example.chineduoty.cinery.utilities.NetworkUtils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements TrailerAdapterOld.TrailerAdapterOnClickHandler {

    public static final String TAG  = DetailActivity.class.getSimpleName();
    private Movie movie;
    private RecyclerView reviewRV;
    private ViewPager trailerPager;
    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;
    private List<Review> reviewList = new ArrayList<Review>();
    private List<Trailer> trailerList = new ArrayList<Trailer>();
    private ApiInterface apiService;
    private boolean isFavourite = false;
    private Uri uri;
    private ImageButton favouriteButton;
    BaseUtils baseUtils;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        if(intent.resolveActivity(getPackageManager()) != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            movie = gson.fromJson(intent.getStringExtra(Intent.EXTRA_TEXT),Movie.class);
        }

        baseUtils = new BaseUtils(this);

        ImageView posterBg = (ImageView)findViewById(R.id.big_poster_bg);
        ImageView poster = (ImageView)findViewById(R.id.big_poster);
        TextView title = (TextView)findViewById(R.id.movie_title);
        TextView rating = (TextView)findViewById(R.id.rating);
        TextView releaseDate = (TextView)findViewById(R.id.release_date);
        TextView overview = (TextView)findViewById(R.id.overview);
        FloatingActionButton shareButton = (FloatingActionButton)findViewById(R.id.shareBtn);
        favouriteButton = (ImageButton)findViewById(R.id.favourite);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareMovie();
            }
        });

        uri = FavouriteContract.FavouriteEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(movie.getId())).build();

        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        if(cursor != null && cursor.moveToFirst()){
            isFavourite  = true;
            favouriteButton.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_star_black_24dp));
        }

        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isFavourite) // Movie is already favourited, so delete
                {
                    int numDeleted = getContentResolver().delete(uri,null,null);
                    if(numDeleted > 0){
                        favouriteButton.setImageDrawable(ContextCompat.getDrawable(DetailActivity.this,R.drawable.ic_star_border_black_24dp));
                        Toast.makeText(DetailActivity.this, "Unfavourited!", Toast.LENGTH_LONG).show();
                    }
                }else  {
                    ContentValues contentValues = new ContentValues();

                    contentValues.put(FavouriteContract.FavouriteEntry.MOVIE_ID, movie.getId());
                    contentValues.put(FavouriteContract.FavouriteEntry.ORIGINAL_TITLE, movie.getOriginalTitle());
                    contentValues.put(FavouriteContract.FavouriteEntry.OVERVIEW, movie.getOverview());
                    contentValues.put(FavouriteContract.FavouriteEntry.POSTER_PATH, movie.getPosterPath());
                    contentValues.put(FavouriteContract.FavouriteEntry.BACKDROP_PATH, movie.getBackdropPath());
                    contentValues.put(FavouriteContract.FavouriteEntry.RELEASE_DATE, movie.getReleaseDate());
                    contentValues.put(FavouriteContract.FavouriteEntry.VOTE_AVERAGE, movie.getVoteAverage());

                    Uri uriResult = getContentResolver().insert(FavouriteContract.FavouriteEntry.CONTENT_URI, contentValues);

                    if (uriResult != null) {
                        favouriteButton.setImageDrawable(ContextCompat.getDrawable(DetailActivity.this,R.drawable.ic_star_black_24dp));
                        Toast.makeText(DetailActivity.this, "Favourited!", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        //trailerAdapter = new TrailerAdapterOld(this,trailerList,DetailActivity.this);
        trailerAdapter = new TrailerAdapter(this,getSupportFragmentManager(),trailerList);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(DetailActivity.this, R.dimen.item_offset);
        trailerPager = (ViewPager) findViewById(R.id.trailer_pager);
trailerPager.setClipToPadding(false);
        trailerPager.setPadding(60,0,60,0);
        trailerPager.setPageMargin(20);
        trailerPager.setAdapter(trailerAdapter);

        reviewAdapter = new ReviewAdapter(this,reviewList);
        reviewRV = (RecyclerView)findViewById(R.id.review_rv);
        reviewRV.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        reviewRV.setHasFixedSize(true);
        reviewRV.setAdapter(reviewAdapter);

        apiService = NetworkUtils.getClient().create(ApiInterface.class);

        String bgPath = Constants.IMAGE_BASE_URL+Constants.BIG_IMAGE_SIZE+movie.getBackdropPath();
        String imagePath = Constants.IMAGE_BASE_URL+Constants.BIG_IMAGE_SIZE+movie.getPosterPath();

        Picasso.with(this).load(bgPath).placeholder(R.color.colorPrimary).into(posterBg);
        Picasso.with(this).load(imagePath).placeholder(R.drawable.film_reel).into(poster);
        title.setText(movie.getOriginalTitle());
        rating.setText("Rating: "+movie.getVoteAverage());
        releaseDate.setText(movie.getReleaseDate());
        overview.setText(movie.getOverview());

        loadTrailers();
        loadReviews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.action_share){
            shareMovie();
        }
        return super.onOptionsItemSelected(item);
    }

    public void shareMovie(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, String.format("Hi, checkout %s, and it is rated %.1f", movie.getOriginalTitle(), movie.getVoteAverage()));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    public void onClick(Trailer trailer) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/watch?v=" + trailer.getKey()));
        startActivity(i);
    }

    private void loadTrailers(){
        checkNetwork();

        Call<TrailerResponse> call = apiService.getTrailers(movie.getId(),NetworkUtils.API_KEY_VALUE);
        call.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                trailerList.clear();
                trailerList = response.body().getResults();
                if(trailerList.size() > 0)
                    trailerAdapter.updateData(trailerList);
            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {
                Log.e(TAG,t.getMessage());
            }
        });
    }

    private void loadReviews(){
        checkNetwork();

        Call<ReviewResponse> call = apiService.getReviews(movie.getId(),NetworkUtils.API_KEY_VALUE);
        call.enqueue(new Callback<ReviewResponse>(){
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                reviewList.clear();
                reviewList = response.body().getResults();
                if(reviewList.size() > 0)
                    reviewAdapter.updateData(reviewList);
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                Log.e(TAG,t.getMessage());
            }
        });
    }

    private void checkNetwork(){
        if (!baseUtils.isOnline()) {
            Toast.makeText(this, "Sorry cannot retrieve movies due to poor internet connection", Toast.LENGTH_LONG).show();
            return;
        }
    }
}
