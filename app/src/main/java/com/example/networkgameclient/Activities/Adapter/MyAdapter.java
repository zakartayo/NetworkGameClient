package com.example.networkgameclient.Activities.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.networkgameclient.Activities.Classes.Item;
import com.example.networkgameclient.R;

import java.util.ArrayList;

/**
 * Created by 이승헌 on 2017-11-27.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Item> mItems;
    private int lastPosition = -1;

    public MyAdapter(ArrayList items, Context mContext)
    {
        mItems = items;
        context = mContext;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, parent, false);
        ViewHolder holder = new ViewHolder(v); return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.roomState.setText(mItems.get(position).getRoomState());
        holder.state.setText(mItems.get(position).getState());
        holder.numCount.setText(mItems.get(position).getNumCount());
        holder.roomNum.setText(mItems.get(position).getRoomNum());
    }
    @Override public int getItemCount() {
        return mItems.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView roomState;
        public TextView state;
        public TextView numCount;
        public TextView roomNum;
        public ViewHolder(View view) {
            super(view);
            roomState = (TextView) view.findViewById(R.id.roomState);
            state = (TextView) view.findViewById(R.id.state);
            numCount = (TextView) view.findViewById(R.id.numCount);
            roomNum = (TextView) view.findViewById(R.id.roomNum);
        }
    }
}
