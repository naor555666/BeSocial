package com.example.besocial.ui.mainactivity.socialcenter;

import android.content.Context;
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
import com.example.besocial.utils.ConstantValues;
import com.example.besocial.R;
import com.example.besocial.data.Event;
import com.example.besocial.databinding.FragmentHostingEventsTabBinding;
import com.example.besocial.ui.mainactivity.MainActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class HostingEventsTabFragment extends Fragment {
    private static final String TAG = "HostingEventsTab";
    private FragmentHostingEventsTabBinding binding;
    private FirebaseRecyclerAdapter<Event, AllEventsTabFragment.EventsViewHolder> firebaseRecyclerAdapter;

    public HostingEventsTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG,"onCreateView");
        binding = FragmentHostingEventsTabBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG,"onActivityCreated");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.hostingEventRecyclerview.setLayoutManager(linearLayoutManager);
                Query query = FirebaseDatabase.getInstance().getReference()
                .child(ConstantValues.EVENTS)
                .orderByChild(ConstantValues.EVENT_HOST_UID)
                .equalTo(MainActivity.getLoggedUser().getUserId());

       displayEventsList(binding.hostingEventRecyclerview, query);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"onViewCreated");
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG,"onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG,"onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        firebaseRecyclerAdapter.stopListening();
        binding = null;
    }
}
