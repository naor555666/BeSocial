package com.example.besocial.ui.mainactivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.besocial.R;
import com.example.besocial.data.Post;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {
    private static ArrayList<Post> posts;


    public class PostViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView userProfilePicture;
        private TextView postUserName, postDate, postDescription;
        private ImageView postImage;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfilePicture = itemView.findViewById(R.id.post_user_profile_image);
            postUserName = itemView.findViewById(R.id.post_username);
            postDate = itemView.findViewById(R.id.post_date);
            postDescription = itemView.findViewById(R.id.post_description);
            postImage = itemView.findViewById(R.id.post_imageview);
        }
    }
public PostsAdapter(ArrayList<Post> posts){
        this.posts=posts;
}



    @NonNull
    @Override
    public PostsAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post_form, parent, false);
        PostViewHolder postViewHolder = new PostViewHolder(v);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.PostViewHolder holder, int position) {
        holder.userProfilePicture.setImageDrawable(posts.get(position).getUserProfilePicture());
        holder.postUserName.setText(posts.get(position).getPostUserName());
        holder.postDate.setText(posts.get(position).getPostDate());
        holder.postDescription.setText(posts.get(position).getPostDescription());
        if(posts.get(position).getPostImage()!=null) {
            holder.postImage.setImageDrawable(posts.get(position).getPostImage());
        }else {
           holder.postImage.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    //getters and setters

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }
}
