package com.tcl.myapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tcl.myapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lishiwei on 2017/3/17.
 */

public class ControlRecyclerAdapter extends RecyclerView.Adapter<ControlRecyclerAdapter.MyViewHolder> {
    private static final String TAG = ControlRecyclerAdapter.class.getSimpleName();
    List<Integer> mIntegerList = new ArrayList<>();
    Context mContext;

    public ControlRecyclerAdapter(Context context) {
        mContext = context;
        mIntegerList.add(R.drawable.forniture1);
        mIntegerList.add(R.drawable.forniture2);
        mIntegerList.add(R.drawable.forniture3);
//        mIntegerList.add(R.drawable.forniture4);
        mIntegerList.add(R.drawable.forniture1);
        mIntegerList.add(R.drawable.forniture2);
        mIntegerList.add(R.drawable.forniture3);
//        mIntegerList.add(R.drawable.forniture4);
        mIntegerList.add(R.drawable.forniture1);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_control_recyclerview, null));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: "+position);
//        holder.mImageView.setLayoutParams(new FrameLayout.LayoutParams(holder.mImageView.getWidth(), DensityUtils.dp2px(200)));
//        Glide.with(mContext).load(R.drawable.whiterectangle).fitCenter().into(holder.mImageView);
//        holder.mImageView.setImageResource(mIntegerList.get(position));
    }

    @Override
    public int getItemCount() {
        return mIntegerList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.forniture_device);
        }
    }
}
