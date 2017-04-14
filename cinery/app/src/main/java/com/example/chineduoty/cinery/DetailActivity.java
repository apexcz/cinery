package com.example.chineduoty.cinery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chineduoty.cinery.models.Movie;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private Movie movie;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        if(intent.resolveActivity(getPackageManager()) != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            movie = gson.fromJson(intent.getStringExtra(Intent.EXTRA_TEXT),Movie.class);
        }

        ImageView posterBg = (ImageView)findViewById(R.id.big_poster_bg);
        ImageView poster = (ImageView)findViewById(R.id.big_poster);
        TextView title = (TextView)findViewById(R.id.movie_title);
        TextView rating = (TextView)findViewById(R.id.rating);
        TextView releaseDate = (TextView)findViewById(R.id.release_date);
        TextView overview = (TextView)findViewById(R.id.overview);

        String bgPath = Constants.IMAGE_BASE_URL+Constants.BIG_IMAGE_SIZE+movie.getBackdrop_path();
        String imagePath = Constants.IMAGE_BASE_URL+Constants.BIG_IMAGE_SIZE+movie.getPoster_path();

        Picasso.with(this).load(bgPath).placeholder(R.color.colorPrimary).into(posterBg);
        Picasso.with(this).load(imagePath).placeholder(R.drawable.film_reel).into(poster);
        title.setText(movie.getOriginal_title());
        rating.setText("Rating: "+movie.getVote_average());
        releaseDate.setText(movie.getRelease_date());
        overview.setText(movie.getOverview());
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
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, String.format("Hi, checkout %s, and it is rated %.1f", movie.getOriginal_title(), movie.getVote_average()));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
