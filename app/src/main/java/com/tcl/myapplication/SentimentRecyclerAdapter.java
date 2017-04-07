package com.tcl.myapplication;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
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
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.recycleritem,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(dataBinding.getRoot());
        myViewHolder.setViewDataBinding(dataBinding);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.getViewDataBinding().setVariable(BR.sentiment,data.get(position));
  }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ViewDataBinding mViewDataBinding;

        public ViewDataBinding getViewDataBinding() {
            return mViewDataBinding;
        }

        public void setViewDataBinding(ViewDataBinding viewDataBinding) {
            mViewDataBinding = viewDataBinding;
        }

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
