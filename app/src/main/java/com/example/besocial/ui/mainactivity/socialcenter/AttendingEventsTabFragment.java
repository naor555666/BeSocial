package com.example.besocial.ui.mainactivity.socialcenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.besocial.R;
import com.example.besocial.data.Event;
import com.example.besocial.databinding.FragmentAttendingEventsTabBinding;
import com.example.besocial.ui.mainactivity.MainActivity;
import com.example.besocial.utils.ConstantValues;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendingEventsTabFragment extends Fragment {

    private static final String TAG = "AttendingEventsTab";

    private FragmentAttendingEventsTabBinding binding;
    private FirebaseRecyclerAdapter<Event, AllEventsTabFragment.EventsViewHolder> firebaseRecyclerAdapter;

    public AttendingEventsTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAttendingEventsTabBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.attendingEventsRecyclerview.setLayoutManager(linearLayoutManager);

        Query query = FirebaseDatabase.getInstance().getReference()
                .child(ConstantValues.USERS_ATTENDING_TO_EVENTS)
                .child(MainActivity.getLoggedUser().getUserId());


        displayEventsList(binding.attendingEventsRecyclerview, query);
    }
    public void displayEventsList(RecyclerView eventsRecyclerView, Query eventsRef) {
        //if(mActivity==null){ return;}
        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions
                .Builder<Event>()
                .setQuery(eventsRef, Event.class)
                .build();
        firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Event, AllEventsTabFragment.EventsViewHolder>(options) {
            @NonNull
            @Override
            public AllEventsTabFragment.EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_node, parent, false);
                AllEventsTabFragment.EventsViewHolder viewHolder = new AllEventsTabFragment.EventsViewHolder(view);
                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull AllEventsTabFragment.EventsViewHolder holder, int position, @NonNull final Event model) {
                holder.eventNode = model;
                Glide.with(getActivity()).load(model.getStrEventPhotoUrl()).placeholder(R.drawable.social_event0).into(holder.eventPhoto);
                holder.eventDateAndTime.setText(model.getBeginDate() + " at " + model.getBeginTime());
                holder.eventTitle.setText(model.getTitle());
                holder.eventLocation.setText(model.getLocationTitle());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AllEventsTabFragment.socialCenterViewModel.setEvent(model);
                        MainActivity.getNavController().navigate(R.id.action_eventsListFragment_to_eventFragment);
                    }
                });
            }
        };
//                    binding.allEventsTabRecyclerView.setAdapter(firebaseRecyclerAdapter);
        eventsRecyclerView.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, TAG + "onStart");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        firebaseRecyclerAdapter.stopListening();
        binding=null;
    }
}
