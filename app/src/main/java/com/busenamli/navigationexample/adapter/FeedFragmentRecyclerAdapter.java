package com.busenamli.navigationexample.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.busenamli.navigationexample.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FeedFragmentRecyclerAdapter extends RecyclerView.Adapter<FeedFragmentRecyclerAdapter.PostHolder>{

    private ArrayList<String> userEmailList;
    private ArrayList<String> userCommentList;

    public FeedFragmentRecyclerAdapter(ArrayList<String> userEmailList, ArrayList<String> userCommentList, ArrayList<String> userImageList) {
        this.userEmailList = userEmailList;
        this.userCommentList = userCommentList;
        this.userImageList = userImageList;
    }

    private ArrayList<String> userImageList;

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.fragment_feed_recycler_row,parent,false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        holder.userEmailText.setText(userEmailList.get(position));
        holder.commentText.setText(userCommentList.get(position));
        Picasso.get().load(userImageList.get(position)).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return userEmailList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView userEmailText;
        TextView commentText;

        public PostHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.recyclerview_row_imageview);
            userEmailText = itemView.findViewById(R.id.recyclerview_row_email_text);
            commentText = itemView.findViewById(R.id.recyclerview_row_comment_text);
        }
    }
}
