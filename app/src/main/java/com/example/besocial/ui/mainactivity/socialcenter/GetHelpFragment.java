package com.example.besocial.ui.mainactivity.socialcenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.besocial.R;
import com.example.besocial.data.Event;
import com.example.besocial.databinding.FragmentGetHelpBinding;
import com.example.besocial.ui.mainactivity.MainActivity;
import com.example.besocial.utils.ConstantValues;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class GetHelpFragment extends Fragment {
    private FragmentGetHelpBinding binding;
    private SocialCenterViewModel socialCenterViewModel;
    private FirebaseRecyclerAdapter<Event, EventsViewHolder> firebaseRecyclerAdapter;

    public GetHelpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =FragmentGetHelpBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.fragmentGetHelpRecyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListeners();
        prepareDatabaseQuery();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        socialCenterViewModel = ViewModelProviders.of(getActivity()).get(SocialCenterViewModel.class);
    }
    private void setListeners() {
        binding.fragmentGetHelpAskHelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(ConstantValues.IS_HELP_EVENT, true);
                MainActivity.getNavController().navigate(R.id.createEventFragment,bundle);
            }
        });
    }
    private void prepareDatabaseQuery() {
        final Query eventsRef = FirebaseDatabase.getInstance().getReference()
                .child(ConstantValues.HELP_ME)
                .orderByChild(ConstantValues.EVENT_HOST_UID)
                .equalTo(MainActivity.getLoggedUser().getUserId());

        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    Toast.makeText(getContext(), "No events to display in this category.", Toast.LENGTH_LONG).show();
                    firebaseRecyclerAdapter.stopListening();
                } else {
                    displayEventsList(binding.fragmentGetHelpRecyclerView, eventsRef);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void displayEventsList(RecyclerView eventsRecyclerView, Query eventsRef) {
        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions
                .Builder<Event>()
                .setQuery(eventsRef, Event.class)
                .build();
        firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Event, GetHelpFragment.EventsViewHolder>(options) {
            @NonNull
            @Override
            public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_node, parent, false);
                EventsViewHolder viewHolder = new EventsViewHolder(view);
                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull EventsViewHolder holder, int position, @NonNull final Event model) {
                holder.eventNode = model;
                Glide.with(getActivity()).load(model.getStrEventPhotoUrl()).placeholder(R.drawable.social_event0).into(holder.eventPhoto);
                holder.eventDateAndTime.setText(model.getBeginDate() + " at " + model.getBeginTime());
                holder.eventTitle.setText(model.getTitle());
                holder.eventLocation.setText(model.getLocationTitle());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        socialCenterViewModel.setEvent(model);
                        MainActivity.getNavController().navigate(R.id.eventFragment);
                    }
                });
            }
        };
        eventsRecyclerView.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();
    }
    public static class EventsViewHolder extends RecyclerView.ViewHolder {
        Event eventNode;
        ImageView eventPhoto;
        TextView eventDateAndTime, eventTitle, eventLocation;

        public EventsViewHolder(@NonNull final View itemView) {
            super(itemView);
            eventPhoto = itemView.findViewById(R.id.event_node_eventPhoto);
            eventDateAndTime = itemView.findViewById(R.id.event_node_eventDateAndTime);
            eventTitle = itemView.findViewById(R.id.event_node_EventTitle);
            eventLocation = itemView.findViewById(R.id.event_node_eventLocation);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding=null;
        firebaseRecyclerAdapter.stopListening();
    }
}
