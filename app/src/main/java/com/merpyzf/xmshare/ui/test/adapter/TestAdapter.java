package com.merpyzf.xmshare.ui.test.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.merpyzf.xmshare.R;

import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<MyViewHolder> {
    Context mContext;
    List<String> mDatas;

    public TestAdapter(Context context, List<String> datas) {
        this.mContext = context;
        this.mDatas = datas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_rv_music, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setBindPosition(position);
        holder.setText(mDatas.get(position));

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


}

class MyViewHolder extends RecyclerView.ViewHolder {
    private int mPosition;
    public TextView mTextView;
    public MyViewHolder(View itemView) {
        super(itemView);
        Log.i("WW2K","created MyViewHolder: "+hashCode()+"itemView: "+itemView.hashCode());
        mTextView = itemView.findViewById(R.id.tv_title);
    }

    public void setBindPosition(int position){
        this.mPosition = position;
    }

    public void setText(String text){
        mTextView.setText(text);
    }


}
