package com.example.besocial.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.besocial.R;
import com.example.besocial.data.Post;

import java.util.ArrayList;

public class HomeFragment extends Fragment{

    private static ArrayList<Post> posts= new ArrayList<Post>();
    private RecyclerView postsRecyclerView;
    private ImageButton createNewPost,refreshPosts;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createNewPost=view.findViewById(R.id.create_new_post_button);
        RecyclerView postsRecyclerView = view.findViewById(R.id.events_list_recycler_view);
        postsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        postsRecyclerView.setLayoutManager(layoutManager);
        //posts = new ArrayList<Post>();

        RecyclerView.Adapter postsAdapter = new PostsAdapter(posts);
        postsRecyclerView.setAdapter(postsAdapter);

        createNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().
                        replace(R.id.nav_host_fragment,  CreateNewPostFragment.getInstance()).//add on top of the static fragment
                        addToBackStack("").//cause the back button scrolling through the loaded fragments
                        commit();
                getFragmentManager().executePendingTransactions();

            }
        });
    }

    public static ArrayList<Post> getPosts(){
        return posts;
    }
    public static void addPost(Post newPost) {
        posts.add(newPost);
    }

}