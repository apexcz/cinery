package com.example.chineduoty.cinery.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chineduoty.cinery.R;
import com.example.chineduoty.cinery.models.Trailer;

import java.util.List;

/**
 * Created by chineduoty on 5/11/17.
 */

public class TrailerAdapterOld extends RecyclerView.Adapter<TrailerAdapterOld.TrailerViewHolder>{

    private Context context;
    private List<Trailer> trailerList;
    private final TrailerAdapterOnClickHandler listClickHandler;

    public TrailerAdapterOld(Context context, List<Trailer> trailers, TrailerAdapterOnClickHandler clickHandler){
        this.context = context;
        trailerList = trailers;
        listClickHandler = clickHandler;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_list_item,parent,false);
        return new  TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        Trailer trailer = trailerList.get(position);

        holder.trailerNameTV.setText(trailer.getName());
        //holder.trailerSizeTV.setText(trailer.getSize());
    }

    @Override
    public int getItemCount() {
        if(trailerList ==  null) return 0;
        return trailerList.size();
    }

    public void updateData(List<Trailer> trailers){
        trailerList = trailers;
        notifyDataSetChanged();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView trailerNameTV;
        //private final TextView trailerSizeTV;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            trailerNameTV = (TextView)itemView.findViewById(R.id.trailer_name);
            //trailerSizeTV = (TextView)itemView.findViewById(R.id.trailer_size);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemPosition = getAdapterPosition();
            Trailer trailer = trailerList.get(itemPosition);
            listClickHandler.onClick(trailer);
        }
    }

    public interface TrailerAdapterOnClickHandler{
        void onClick(Trailer trailer);
    }
}
