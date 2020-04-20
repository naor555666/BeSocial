package com.example.besocial;

import android.app.DownloadManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.besocial.ui.mainactivity.CreateNewPostFragment;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class SearchUsersFragment extends Fragment {

    private Query query;
    public SearchUsersFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        query= FirebaseDatabase.getInstance().getReference().child(ConstantValues.USERS);
        
    }
}
