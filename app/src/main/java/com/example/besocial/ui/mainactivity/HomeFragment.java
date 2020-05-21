package com.example.besocial.ui.mainactivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.besocial.R;
import com.example.besocial.data.Post;
import com.example.besocial.data.User;
import com.example.besocial.utils.ConstantValues;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static ArrayList<Post> posts = new ArrayList<Post>();
    private static RecyclerView postsRecyclerView;
    private ImageButton createNewPost, refreshPosts;
    private static DatabaseReference postsRef, likesRef, postLikesRef, usersRef;
    private ProgressDialog progressDialog;
    private Boolean wasLikeClicked;
    private User loggedUser;
    private List<String> displayedPostsId;
    private Boolean arePostsShown;
    private static UsersViewModel mViewModel;

    private FirebaseRecyclerAdapter<Post, HomeFragment.PostsViewHolder> firebaseRecyclerAdapter;
    //private String postKey;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayedPostsId = new ArrayList<String>();
        createNewPost = view.findViewById(R.id.create_new_post_button);
        postsRecyclerView = view.findViewById(R.id.posts_list_recycler_view);
        postsRecyclerView.setHasFixedSize(true);
        mViewModel = ViewModelProviders.of(getActivity()).get(UsersViewModel.class);

        //sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //editor = sharedPref.edit();
        progressDialog = new ProgressDialog(getActivity());
        //progressDialog.setTitle("Loading... Please wait");
        //progressDialog.show();
        loggedUser = MainActivity.getLoggedUser();

        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        postsRecyclerView.setLayoutManager(layoutManager);
        arePostsShown = false;
        displayPosts();


        createNewPost.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_nav_home_to_createNewPostFragment));


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //likesRef= FirebaseDatabase.getInstance().getReference().child(ConstantValues.LIKES);
        //postsRef= FirebaseDatabase.getInstance().getReference().child(ConstantValues.POSTS);
        //displayPosts();
    }

    public static ArrayList<Post> getPosts() {
        return posts;
    }

    public static void addPost(Post newPost) {
        posts.add(newPost);
    }

    public void displayPosts() {
        likesRef = FirebaseDatabase.getInstance().getReference().child(ConstantValues.LIKES);
        postsRef = FirebaseDatabase.getInstance().getReference().child(ConstantValues.POSTS);
        usersRef = FirebaseDatabase.getInstance().getReference().child(ConstantValues.USERS);


        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    Toast.makeText(getContext(), "No posts to display", Toast.LENGTH_LONG).show();
                } else if (arePostsShown.equals(false)) {
                    arePostsShown = true;
                    showPosts(dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void showPosts(final long numberOfPosts) {
        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions
                .Builder<Post>()
                .setQuery(postsRef, Post.class)
                .build();
        //FirebaseRecyclerAdapter<Post, HomeFragment.PostsViewHolder> firebaseRecyclerAdapter
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, HomeFragment.PostsViewHolder>(options) {
            @NonNull
            @Override
            public HomeFragment.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_in_recycler, parent, false);
                HomeFragment.PostsViewHolder viewHolder = new HomeFragment.PostsViewHolder(view);
                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull final HomeFragment.PostsViewHolder holder, int position, @NonNull final Post model) {
                //holder.benefitNode = model;
                //long numberOfLikes=model.getNumberOfLikes().longValue();
                Glide.with(getContext()).load(model.getUserProfilePicture()).placeholder(R.drawable.empty_profile_image).into(holder.postProfilePicture);
                if(model.getPostImage()==null){
                    holder.postPhoto.setVisibility(View.GONE);
                }
                else{
                    Glide.with(getContext()).load(model.getPostImage()).placeholder(R.drawable.social_event0).into(holder.postPhoto);
                }

                holder.postUserName.setText(model.getPostUserName());
                holder.postDescription.setText(model.getPostDescription());
                holder.postDate.setText(model.getPostDate());
                holder.numberOfLikes.setText("number of likes: "+model.getNumberOfLikes().longValue());
                holder.postIdentityLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        usersRef.child(model.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user= dataSnapshot.getValue(User.class);
                                Log.d(TAG, "onDataChange: User: "+user.getUserFirstName()+" , status: "+user.getAccountStatus());
                                mViewModel.setUser(user);
                                MainActivity.getNavController().navigate(R.id.action_nav_home_to_nav_my_profile);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                postLikesRef = likesRef.child(model.getPostId());
                postLikesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(MainActivity.getLoggedUser().getUserId())){
                            //Toast.makeText(getContext(),"liked the post",Toast.LENGTH_LONG).show();
                            holder.likeButton.setImageResource(R.drawable.full_like_button);

                        }
                        else
                            holder.likeButton.setImageResource(R.drawable.empty_like_button);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likeButton(holder.likeButton, model);
                    }
                });


                displayedPostsId.add(model.getPostId());

            }
        };
        RecyclerView newPostRecyclerView = HomeFragment.getPostsRecyclerView();
        newPostRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        Post postNode;
        ImageButton likeButton;
        RelativeLayout postIdentityLayout;
        ImageView postPhoto;
        TextView numberOfLikes;
        CircleImageView postProfilePicture;
        TextView postUserName, postDate, postDescription;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            numberOfLikes = itemView.findViewById(R.id.post_in_recycler_number_of_likes);
            likeButton = itemView.findViewById(R.id.post_in_recycler_like_button);
            postProfilePicture = itemView.findViewById(R.id.post_user_profile_image);
            postDate = itemView.findViewById(R.id.post_date);
            postDescription = itemView.findViewById(R.id.post_description);
            postUserName = itemView.findViewById(R.id.post_username);
            postIdentityLayout = itemView.findViewById(R.id.post_identity_layout);
            postPhoto = itemView.findViewById(R.id.post_imageview);
        }
    }

    public static RecyclerView getPostsRecyclerView() {
        return postsRecyclerView;
    }

    void likeButton(final ImageButton likeButton, final Post selectedPost) {

        wasLikeClicked = true;

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (wasLikeClicked.equals(true)) {
                    DataSnapshot ds = dataSnapshot.child(selectedPost.getPostId());
                    String postId = selectedPost.getPostId();
                    if (ds.hasChild(MainActivity.getLoggedUser().getUserId())) {

                        likesRef.child(selectedPost.getPostId()).child(MainActivity.getLoggedUser().getUserId()).removeValue();
                        wasLikeClicked = false;
                        updateNumberOfLikesTransaction(postId, -1);
                        //likeButton.setImageResource(R.drawable.empty_like_button);
                    } else {
                        //likeButton.setImageResource(R.drawable.full_like_button);
                        updateNumberOfLikesTransaction(postId, 1);
                        likesRef.child(postId).child(MainActivity.getLoggedUser().getUserId()).setValue(true);
                        wasLikeClicked = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void updateNumberOfLikesTransaction(final String postId, final long numberOfLikesToAdd) {
        postsRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull final MutableData mutableData) {
                postsRef.child(postId).child("numberOfLikes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Long oldNumberOfLikes = dataSnapshot.getValue(Long.class);
                        postsRef.child(postId).child("numberOfLikes").setValue(Long.valueOf(oldNumberOfLikes.longValue() + numberOfLikesToAdd));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return null;
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
    }

/*    @Override
    public void onStop() {
        super.onStop();
        if( firebaseRecyclerAdapter!=null){
            firebaseRecyclerAdapter.stopListening();
        }

    }*/


}