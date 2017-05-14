package com.example.chineduoty.cinery.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chineduoty.cinery.R;
import com.example.chineduoty.cinery.models.Review;

import java.util.List;

/**
 * Created by chineduoty on 5/10/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<Review> reviews;

    public ReviewAdapter(Context context, List<Review> lstReviews) {
        this.context = context;
        reviews = lstReviews;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item, parent, false);
        return new ReviewViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);

        holder.authorTV.setText(review.getAuthor());
        holder.contentTV.setText(review.getContent());
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        public final TextView authorTV;
        public final TextView contentTV;

        public ReviewViewHolder(View view) {
            super(view);
            authorTV = (TextView) view.findViewById(R.id.author);
            contentTV = (TextView) view.findViewById(R.id.comment);
        }
    }

    @Override
    public int getItemCount() {
        if (reviews == null) return 0;
        return reviews.size();
    }

    public void updateData(List<Review> revs) {
        reviews = revs;
        notifyDataSetChanged();
    }
}
