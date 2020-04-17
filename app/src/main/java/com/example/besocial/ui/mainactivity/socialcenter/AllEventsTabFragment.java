package com.example.besocial.ui.mainactivity.socialcenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import com.bumptech.glide.Glide;
import com.example.besocial.R;
import com.example.besocial.data.Event;
import com.example.besocial.databinding.FragmentAllEventsTabBinding;
import com.example.besocial.ui.mainactivity.MainActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllEventsTabFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    //    private final String TAG = getClass().getName();
    private static final String TAG = "AllEventsTabFragment";
    private FragmentAllEventsTabBinding binding;

    private SocialCenterViewModel socialCenterViewModel;


    private DatabaseReference eventsRef;
    private String strEventCategory;
    private boolean isHelpEvent;
    FirebaseRecyclerAdapter<Event, EventsViewHolder> firebaseRecyclerAdapter;

    public AllEventsTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
/*        binding.allEventsTabRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.allEventsTabRecyclerView.setLayoutManager(linearLayoutManager);

        //initializing the spinner list of categories
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.list_of_categories, R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        binding.allEventsTabCategorySpinner.setAdapter(arrayAdapter);*/
/*        if (!(getArguments().isEmpty())) {
            isHelpEvent = getArguments().getBoolean(SocialCenterFragment.IS_HELP_EVENT);
            binding.allEventsTabCategorySpinner.setSelection(binding.allEventsTabCategorySpinner.getAdapter().getCount() - 1);
            binding.allEventsTabCategorySpinner.setEnabled(false);
        }*/

        strEventCategory = (String) binding.allEventsTabCategorySpinner.getSelectedItem();
        //binding.eventsListTitle.setText(strEventCategory + " Events");
        //eventsRef = FirebaseDatabase.getInstance().getReference().child("Events").child(strEventCategory);
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Events").child("Help Me!");
        setListeners();
    }

    private void setListeners() {
        binding.allEventsTabCategorySpinner.setOnItemSelectedListener(this);
    }

    private void displayEventsList() {
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
                Glide.with(getContext()).load(model.getStrEventPhotoUrl()).placeholder(R.drawable.social_event0).into(holder.eventPhoto);
                holder.eventDateAndTime.setText(model.getBeginDate() + " at " + model.getBeginTime());
                holder.eventTitle.setText(model.getTitle());
                holder.eventLocation.setText(model.getLocationTitle());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        socialCenterViewModel.setEvent(model);
                        MainActivity.getNavController().navigate(R.id.action_allEventsTabFragment_to_eventFragment);
                    }
                });
            }
        };
        binding.allEventsTabRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onClick(View v) {
        //navController.navigate(R.id.action_eventsListFragment_to_createEventFragment);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        strEventCategory = (String) parent.getItemAtPosition(position);
        Log.d(TAG, "eventCategory is " + strEventCategory);
//        binding.eventsListTitle.setText(strEventCategory + " Events");
        displayEventsList();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,TAG+"onStart");
        //displayEventsList();
    }

    @Override
    public void onStop() {
        super.onStop();
        //firebaseRecyclerAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
