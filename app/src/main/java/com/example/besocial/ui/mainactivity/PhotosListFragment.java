package com.example.besocial.ui.mainactivity;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.besocial.R;
import com.example.besocial.data.Notification;
import com.example.besocial.data.UserPhoto;
import com.example.besocial.utils.ConstantValues;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotosListFragment extends Fragment {

    private static DatabaseReference userPhotosRef;
    private static RecyclerView photosRecyclerView;
    private FirebaseRecyclerAdapter<UserPhoto, PhotosListFragment.PhotosListViewHolder> firebaseRecyclerAdapter;


    public PhotosListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photos_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        photosRecyclerView = view.findViewById(R.id.my_photos_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        layoutManager.setReverseLayout(true);
        photosRecyclerView.setLayoutManager(layoutManager);
        displayPhotos();
    }

    private void displayPhotos(){
        userPhotosRef = FirebaseDatabase.getInstance().getReference().child(ConstantValues.USER_PHOTOS).child(MainActivity.getFireBaseAuth().getUid());
        userPhotosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    Toast.makeText(getContext(), "No photos to display", Toast.LENGTH_SHORT).show();
                } else {
                    showphotos();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showphotos(){
        FirebaseRecyclerOptions<UserPhoto> options = new FirebaseRecyclerOptions
                .Builder<UserPhoto>()
                .setQuery(userPhotosRef, UserPhoto.class)
                .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserPhoto, PhotosListViewHolder>(options) {
            @NonNull
            @Override
            public PhotosListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_in_recycler, parent, false);
                PhotosListViewHolder viewHolder = new PhotosListViewHolder(view);
                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull final PhotosListViewHolder holder, int position, @NonNull final UserPhoto model) {

                Glide.with(getContext()).load(model.getUserPhoto()).into(holder.photo);
            }
        };
        RecyclerView newNotificationsRecyclerView = PhotosListFragment.photosRecyclerView;
        newNotificationsRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class PhotosListViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        public PhotosListViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo_in_recycler_imageview);
            Log.d(TAG, "PhotosListViewHolder: ");
        }
    }
}
