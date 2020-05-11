package com.example.besocial.ui.mainactivity.socialcenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.besocial.R;
import com.example.besocial.data.Event;
import com.example.besocial.data.LiteUserDetails;
import com.example.besocial.databinding.FragmentEventAttendantsBinding;
import com.example.besocial.ui.mainactivity.MainActivity;
import com.example.besocial.utils.ConstantValues;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventAttendantsFragment extends Fragment {
    private FragmentEventAttendantsBinding binding;

    static SocialCenterViewModel socialCenterViewModel;
    private Query attendantsRef;
    static FirebaseRecyclerAdapter<LiteUserDetails, LiteUserDetailsViewHolder> firebaseRecyclerAdapter;
    private Event chosenEvent;

    public EventAttendantsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEventAttendantsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.userNodesRecyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        socialCenterViewModel = ViewModelProviders.of(getActivity()).get(SocialCenterViewModel.class);
        chosenEvent=socialCenterViewModel.getEvent().getValue();
        prepareDatabaseQuery();
    }
    private void prepareDatabaseQuery() {
        attendantsRef= FirebaseDatabase.getInstance().getReference()
                .child(ConstantValues.EVENTS_WITH_ATTENDINGS)
                .child(chosenEvent.getEventId());

        attendantsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    Toast.makeText(getContext(), "No attendants for now!", Toast.LENGTH_LONG).show();
                    if (firebaseRecyclerAdapter != null) {
                        firebaseRecyclerAdapter.stopListening();
                    }
                } else {
                    displayUsersList(binding.userNodesRecyclerView, attendantsRef);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void displayUsersList(RecyclerView usersRecyclerView, Query attendantsRef) {
        FirebaseRecyclerOptions<LiteUserDetails> options = new FirebaseRecyclerOptions
                .Builder<LiteUserDetails>()
                .setQuery(attendantsRef, LiteUserDetails.class)
                .build();
        firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<LiteUserDetails, LiteUserDetailsViewHolder>(options) {
            @NonNull
            @Override
            public LiteUserDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_in_recycler, parent, false);
                LiteUserDetailsViewHolder viewHolder = new LiteUserDetailsViewHolder(view);
                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull LiteUserDetailsViewHolder holder, int position, @NonNull final LiteUserDetails model) {

                holder.userNode = model;
                Glide.with(getActivity()).load(model.getUserProfileImage()).placeholder(R.drawable.empty_profile_image).into(holder.userPhoto);
                holder.userName.setText(String.format("%s %s", model.getUserFirstName(), model.getUserLastName()));
                if (!model.getUserId().equals(chosenEvent.getEventCreatorUid())) {
                    holder.giveCreditsbtn.setVisibility(View.VISIBLE);
                    holder.giveCreditsbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            giveUserCredits(model.getUserId());
                        }
                    });
                }

            }

        };
        usersRecyclerView.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();
    }

    private void giveUserCredits(String userId) {
    }

    public static class LiteUserDetailsViewHolder extends RecyclerView.ViewHolder {
        LiteUserDetails userNode;
        ImageView userPhoto;
        TextView userName;
        Button giveCreditsbtn;

        public LiteUserDetailsViewHolder(@NonNull final View itemView) {
            super(itemView);
            userPhoto = itemView.findViewById(R.id.searched_user_profile_image);
            userName = itemView.findViewById(R.id.searched_user_user_name);
            giveCreditsbtn = itemView.findViewById(R.id.user_in_recycler_give_credits);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null){
            firebaseRecyclerAdapter.stopListening();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
