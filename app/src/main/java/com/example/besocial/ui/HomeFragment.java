package com.example.besocial.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.besocial.MainActivity;
import com.example.besocial.R;
import com.example.besocial.data.Post;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private ArrayList<Post> posts;
    private RecyclerView postsRecyclerView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView postsRecyclerView = view.findViewById(R.id.posts_recycler_view);
        postsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        postsRecyclerView.setLayoutManager(layoutManager);
        posts = new ArrayList<Post>();
        posts.add(new Post(getResources().getDrawable(R.drawable.naor_profile_picture),
                "Naor Ohana", "12.1.20", "Hi everyone !", getResources().getDrawable(R.drawable.naor_profile_picture)));
        posts.add(new Post(getResources().getDrawable(R.drawable.besociallogo),
                "BeSocial", "12.1.20", "Hello !", null));
        posts.add(new Post(getResources().getDrawable(R.drawable.or_profile),
                "Or Magogi", "12.1.20", "I am here too", null));
        RecyclerView.Adapter postsAdapter = new PostsAdapter(posts);
        postsRecyclerView.setAdapter(postsAdapter);
    }
}