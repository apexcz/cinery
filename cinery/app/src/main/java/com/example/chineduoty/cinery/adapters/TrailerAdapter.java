package com.example.chineduoty.cinery.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.chineduoty.cinery.fragments.TrailerFragment;
import com.example.chineduoty.cinery.models.Trailer;

import java.util.List;

/**
 * Created by chineduoty on 5/12/17.
 */

public class TrailerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private List<Trailer> trailerList;

    public  TrailerAdapter(Context context, FragmentManager fm, List<Trailer> trailers){
        super(fm);
        mContext = context;
        trailerList = trailers;
    }

    @Override
    public Fragment getItem(int position) {
        return TrailerFragment.newInstance(mContext,trailerList.get(position));
    }

    @Override
    public int getCount() {
        if(trailerList == null) return 0;
        return trailerList.size();
    }

    public void updateData(List<Trailer> trailers){
        trailerList = trailers;
        notifyDataSetChanged();
    }
}
