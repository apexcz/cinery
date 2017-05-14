package com.example.chineduoty.cinery.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chineduoty.cinery.Constants;
import com.example.chineduoty.cinery.R;
import com.example.chineduoty.cinery.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chineduoty on 4/13/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private Context context;
    private List<Movie> lstMovies;
    private final MoviesAdapterOnClickHandler mClickHandler;

    public MoviesAdapter(List<Movie> movies, Context context, MoviesAdapterOnClickHandler clickHandler){
        lstMovies = movies;
        this.context =context;
        mClickHandler = clickHandler;
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.movie_list_item,parent,false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position) {
        Movie movie = lstMovies.get(position);
        String imagePath = Constants.IMAGE_BASE_URL+Constants.IMAGE_SIZE+movie.getPosterPath();

        holder.titleTV.setText(movie.getOriginalTitle());
        Picasso.with(context).load(imagePath).placeholder(R.drawable.film_reel).into(holder.poster);
    }

    @Override
    public int getItemCount() {
        if(lstMovies == null) return 0;
        return lstMovies.size();
    }

    public void updateAdapter(List<Movie> movies){
        lstMovies = movies;
        notifyDataSetChanged();
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView titleTV;
        public final ImageView poster;

        public MoviesViewHolder(View view){
            super(view);
            titleTV = (TextView)view.findViewById(R.id.title_tv);
            poster = (ImageView)view.findViewById(R.id.movie_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemPosition = getAdapterPosition();
            Movie movie = lstMovies.get(itemPosition);
            mClickHandler.onClick(movie);
        }
    }

    public interface MoviesAdapterOnClickHandler{
        void onClick(Movie movie);
    }
}
