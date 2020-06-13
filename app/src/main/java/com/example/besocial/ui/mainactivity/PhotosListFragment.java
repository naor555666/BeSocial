package com.example.besocial.ui.mainactivity;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.besocial.R;
import com.example.besocial.data.Notification;
import com.example.besocial.data.User;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotosListFragment extends Fragment {

    private static DatabaseReference userPhotosRef;
    private static RecyclerView photosRecyclerView;
    private FirebaseRecyclerAdapter<UserPhoto, PhotosListFragment.PhotosListViewHolder> firebaseRecyclerAdapter;
    private static final String TAG = "PhotosListViewHolder";
    private TextView myPhotosText;
    private static UsersViewModel mViewModel;
    private User userData;


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
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setReverseLayout(true);
        photosRecyclerView.setLayoutManager(layoutManager);
        myPhotosText=view.findViewById(R.id.text_my_photos);
        mViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);
        userData = mViewModel.getUser().getValue();
        displayPhotos();
    }

    private void displayPhotos() {
        if(ProfileFragment.getTempId().equals(""))
            userPhotosRef = FirebaseDatabase.getInstance().getReference().child(ConstantValues.USER_PHOTOS).child(MainActivity.getFireBaseAuth().getUid());
        else {
            userPhotosRef = FirebaseDatabase.getInstance().getReference().child(ConstantValues.USER_PHOTOS).child(ProfileFragment.getTempId());
            myPhotosText.setText(userData.getUserFullName()+" Photos");
        }
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
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setReverseLayout(true);
        photosRecyclerView.setLayoutManager(layoutManager);
/*        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        photosRecyclerView.setLayoutManager(linearLayoutManager);
        photosRecyclerView.setLayoutManager(linearLayoutManager);*/
    }

    private void showphotos() {
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
                Log.d(TAG, "onBindViewHolder: Uid= "+model.getUserId());
                Log.d(TAG, "onBindViewHolder: User photo= "+model.getUserPhoto());
            }
        };

        photosRecyclerView.setAdapter(firebaseRecyclerAdapter);
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
