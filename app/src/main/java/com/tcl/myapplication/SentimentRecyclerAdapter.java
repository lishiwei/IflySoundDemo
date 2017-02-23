package com.tcl.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by TCL SHBC-02 on 2017/2/14.
 */

public class SentimentRecyclerAdapter extends RecyclerView.Adapter<SentimentRecyclerAdapter.MyViewHolder> {
    Context context;
    ArrayList<String> data = new ArrayList<>();

    public SentimentRecyclerAdapter(Context context, ArrayList<String> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.recycleritem,null));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
holder.sentiment.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView sentiment;
        public MyViewHolder(View itemView) {
            super(itemView);
            sentiment = (TextView) itemView.findViewById(R.id.Tv_Sentiment);
        }
    }
}
