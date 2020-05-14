package com.example.besocial.ui.mainactivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.besocial.R;
import com.example.besocial.data.Post;
import com.example.besocial.utils.ConstantValues;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment{

    private static ArrayList<Post> posts= new ArrayList<Post>();
    private static RecyclerView postsRecyclerView;
    private ImageButton createNewPost,refreshPosts;
    private DatabaseReference postsRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createNewPost=view.findViewById(R.id.create_new_post_button);
        postsRecyclerView = view.findViewById(R.id.posts_list_recycler_view);
        postsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        postsRecyclerView.setLayoutManager(layoutManager);
        createNewPost.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_nav_home_to_createNewPostFragment));


        displayPosts();

    }

    public static ArrayList<Post> getPosts(){
        return posts;
    }
    public static void addPost(Post newPost) {
        posts.add(newPost);
    }

    public void displayPosts(){
        postsRef= FirebaseDatabase.getInstance().getReference().child(ConstantValues.POSTS);

        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChildren()){
                    Toast.makeText(getContext(),"No posts to display",Toast.LENGTH_LONG).show();
                }else{
                    FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions
                            .Builder<Post>()
                            .setQuery(postsRef, Post.class)
                            .build();
                    FirebaseRecyclerAdapter<Post, HomeFragment.PostsViewHolder> firebaseRecyclerAdapter
                            = new FirebaseRecyclerAdapter<Post, HomeFragment.PostsViewHolder>(options) {
                        @NonNull
                        @Override
                        public HomeFragment.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post_form, parent, false);
                            HomeFragment.PostsViewHolder viewHolder = new HomeFragment.PostsViewHolder(view);
                            return viewHolder;
                        }

                        @Override
                        protected void onBindViewHolder(@NonNull HomeFragment.PostsViewHolder holder, int position, @NonNull final Post model) {
                            //holder.benefitNode = model;
                            Glide.with(getContext()).load(model.getUserProfilePicture()).placeholder(R.drawable.social_event0).into(holder.postProfilePicture);
                            Glide.with(getContext()).load(model.getPostImage()).placeholder(R.drawable.social_event0).into(holder.postPhoto);
                            holder.postUserName.setText(model.getPostUserName());
                            holder.postDescription.setText(model.getPostDescription());
                            holder.postDate.setText(model.getPostDate());
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //socialCenterViewModel.setBenefit(model);
                                    //MainActivity.getNavController().navigate(R.id.action_nav_bonus_area_to_benefitFragment);
                                }
                            });
                        }
                    };
                    RecyclerView newPostRecyclerView= HomeFragment.getPostsRecyclerView();
                    newPostRecyclerView.setAdapter(firebaseRecyclerAdapter);
                    firebaseRecyclerAdapter.startListening();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        Post postNode;
        ImageView postPhoto;
        CircleImageView postProfilePicture;
        TextView postUserName, postDate, postDescription;
        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            postProfilePicture=itemView.findViewById(R.id.post_user_profile_image);
            postDate= itemView.findViewById(R.id.post_date);
            postDescription= itemView.findViewById(R.id.post_description);
            postUserName= itemView.findViewById(R.id.post_username);
            postPhoto= itemView.findViewById(R.id.post_imageview);
        }
    }

    public static RecyclerView getPostsRecyclerView(){
        return postsRecyclerView;
    }

}