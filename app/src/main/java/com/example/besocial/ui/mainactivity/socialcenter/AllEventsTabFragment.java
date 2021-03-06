package com.example.besocial.ui.mainactivity.socialcenter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.besocial.utils.ConstantValues;
import com.example.besocial.R;
import com.example.besocial.data.Event;
import com.example.besocial.databinding.FragmentAllEventsTabBinding;
import com.example.besocial.ui.mainactivity.MainActivity;
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
public class AllEventsTabFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "AllEventsTabFragment";

    private FragmentAllEventsTabBinding binding;
    static SocialCenterViewModel socialCenterViewModel;
    private Query eventsRef;
    static FirebaseRecyclerAdapter<Event, EventsViewHolder> firebaseRecyclerAdapter;

    private String strEventCategory;

    public AllEventsTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_all_events_tab, container, false);

        binding = FragmentAllEventsTabBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.allEventsTabRecyclerView.setLayoutManager(linearLayoutManager);

        //initializing the spinner list of categories
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.list_of_categories, R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        binding.allEventsTabCategorySpinner.setAdapter(arrayAdapter);
        //
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        socialCenterViewModel = ViewModelProviders.of(getActivity()).get(SocialCenterViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        strEventCategory = (String) binding.allEventsTabCategorySpinner.getSelectedItem();
        setListeners();
    }

    private void setListeners() {
        binding.allEventsTabCategorySpinner.setOnItemSelectedListener(this);
    }

    public void displayEventsList(RecyclerView eventsRecyclerView, Query eventsRef) {
        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions
                .Builder<Event>()
                .setQuery(eventsRef, Event.class)
                .build();
        firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Event, EventsViewHolder>(options) {
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
                holder.eventDateAndTime.setText(String.format("%s at %s", model.getBeginDate(), model.getBeginTime()));
                holder.eventTitle.setText(model.getTitle());
                holder.eventLocation.setText(model.getLocationTitle());
                if (model.getEventCreatorUid().equals(MainActivity.getLoggedUser().getUserId())) {
                    holder.host.setText(String.format("You are the host"));
                } else {
                    holder.host.setText(String.format("Host: %s", model.getEventCreatorUserName()));
                }
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
        TextView eventDateAndTime, eventTitle, eventLocation, host;

        public EventsViewHolder(@NonNull final View itemView) {
            super(itemView);
            eventPhoto = itemView.findViewById(R.id.event_node_eventPhoto);
            eventDateAndTime = itemView.findViewById(R.id.event_node_eventDateAndTime);
            eventTitle = itemView.findViewById(R.id.event_node_EventTitle);
            eventLocation = itemView.findViewById(R.id.event_node_eventLocation);
            host = itemView.findViewById(R.id.event_node_host);
        }
    }

    @Override
    public void onItemSelected(final AdapterView<?> parent, View view, int position, long id) {
        strEventCategory = (String) parent.getItemAtPosition(position);
        Log.d(TAG, "eventCategory is " + strEventCategory);
        eventsRef = FirebaseDatabase.getInstance().getReference()
                .child(ConstantValues.EVENTS);
        if (!strEventCategory.equals(ConstantValues.GENERAL)) {
            eventsRef = eventsRef
                    .orderByChild(ConstantValues.CATEGORY)
                    .equalTo(strEventCategory);
        }
        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    Toast.makeText(getContext(), "No events to display in this category.", Toast.LENGTH_LONG).show();
                    firebaseRecyclerAdapter.stopListening();
                } else {
                    displayEventsList(binding.allEventsTabRecyclerView, eventsRef);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }

    }
}
