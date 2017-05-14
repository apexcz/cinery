package com.example.chineduoty.cinery.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chineduoty.cinery.R;
import com.example.chineduoty.cinery.models.Trailer;
import com.example.chineduoty.cinery.utilities.BaseUtils;

public class TrailerFragment extends Fragment {

    public static final String TRAILER_KEY="Trailer Key";
    private static Context mContext;
    private Trailer trailer;
    private BaseUtils baseUtils;

    public TrailerFragment() {
        baseUtils = new BaseUtils(mContext);
    }

    public static TrailerFragment newInstance(Context context, Trailer trailer) {
        mContext = context;
        TrailerFragment fragment = new TrailerFragment();
        Bundle args = new Bundle();
        args.putParcelable(TRAILER_KEY,trailer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            trailer = getArguments().getParcelable(TRAILER_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trailer, container, false);

        LinearLayout randomBg = (LinearLayout)view.findViewById(R.id.random_bg);
        ImageButton playButton = (ImageButton)view.findViewById(R.id.play_trailer);
        TextView trailerNameTV = (TextView)view.findViewById(R.id.trailer_name);

        //ContextCompat.getColor(this.getContext(),R.color.colorAccent);
        randomBg.setBackgroundColor(baseUtils.getRandomMaterialColor("400"));
        trailerNameTV.setText(trailer.getName());
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/watch?v=" + trailer.getKey()));
                startActivity(i);
            }
        });
        return view;
    }




}
