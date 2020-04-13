package com.example.notes2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<Note> notes;
    private ItemClickListener itemClickListener;

    public MyAdapter(List<Note> notes) {
        this.notes = notes;
    }

    // Stores and recycles views as they are scrolled off screen
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView myTextView;
        MyViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) itemClickListener.onItemClick(view, getAdapterPosition());
        }


        @Override
        public boolean onLongClick(View view) {
            if (itemClickListener != null) itemClickListener.onItemLongClick(view, getAdapterPosition());
            return true;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // Truncating the text is a pain, "android:singleLine="true"" was much simpler
        String note = notes.get(position).getNote();
        holder.myTextView.setText(note);
    }

    // https://developer.android.com/guide/topics/ui/layout/recyclerview
    // https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example
    @Override
    public int getItemCount() {
        return notes.size();
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    // Parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
}
