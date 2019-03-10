package com.example.movies;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<Movie> mDataset;

    private Context mcontext;
    private static String DETAIL_INTENT_STRING = "MOVIE_DETAILS";

    public MyAdapter(Context context, ArrayList<Movie> myDataset) {
        mDataset = myDataset;
        this.mcontext = context;
    }

    @Override
    @NonNull
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                     int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.text.setText(mDataset.get(position).getTitle());
        Picasso.get()
                .load(URLS.IMAGE_BASE_URL + mDataset.get(position).getPosterPath())
                .error(R.drawable.ic_launcher_background)
                .into(holder.image);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, MovieDetailsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(DETAIL_INTENT_STRING, mDataset.get(position).getId());
                mcontext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private ImageView image;
        private TextView text;
        private LinearLayout linearLayout;

        private MyViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_id);
            text = itemView.findViewById(R.id.text_id);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }
}