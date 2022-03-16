package com.example.uipractice.recyclerview.layoutmanger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uipractice.R;

import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {


    private List<Bean> mList;

    public RecyclerAdapter(List<Bean> list) {
        mList = list;
    }


    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.itemView.setBackgroundColor(mList.get(position).getColor());
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView viewById;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            viewById = itemView.findViewById(R.id.textView);
//            viewById.setText(mList.get(getAdapterPosition()).getContent());
        }

        public void bind(Bean bean) {
            viewById.setText(bean.getContent());
        }

    }
}
