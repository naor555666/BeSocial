package com.example.besocial.ui.mainactivity.socialcenter;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.besocial.R;
import com.example.besocial.data.Event;
import com.example.besocial.databinding.FragmentEventsListBinding;
import com.example.besocial.ui.mainactivity.MainActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
//public class EventsListFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
public class EventsListFragment extends Fragment{

    public static final String TAG = "EventsListFragment";

    private FragmentEventsListBinding binding;

    private NavController navController;
    private FloatingActionButton addEventFab;
    private DatabaseReference eventsRef;
    private String strEventCategory;
    private boolean isHelpEvent;


    private SocialCenterViewModel socialCenterViewModel;

    private EventsListPageAdapter eventsListPageAdapter;
    private TabLayout tabLayout;
    private ViewPager eventsListViewPager;

    public EventsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

/*        binding = FragmentEventsListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;*/

        return inflater.inflate(R.layout.fragment_events_list, container, false);
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_events_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        socialCenterViewModel = ViewModelProviders.of(getActivity()).get(SocialCenterViewModel.class);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = MainActivity.getNavController();

        //binding.eventsListRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        //binding.eventsListRecyclerView.setLayoutManager(linearLayoutManager);

        tabLayout = view.findViewById(R.id.events_list_tab_layout);
        eventsListViewPager = view.findViewById(R.id.events_list_view_pager);

        //binding.eventsListViewPager.setAdapter(eventsListPageAdapter);
        eventsListPageAdapter = new EventsListPageAdapter(getChildFragmentManager(),tabLayout.getTabCount());
        eventsListViewPager.setAdapter(eventsListPageAdapter);

        //initializing the spinner list of categories
/*        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.list_of_categories, R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        binding.eventsListCategorySpinner.setAdapter(arrayAdapter);
        if (!(getArguments().isEmpty())) {
            isHelpEvent = getArguments().getBoolean(SocialCenterFragment.IS_HELP_EVENT);
            binding.eventsListCategorySpinner.setSelection(binding.eventsListCategorySpinner.getAdapter().getCount() - 1);
            binding.eventsListCategorySpinner.setEnabled(false);
        }

        strEventCategory = (String) binding.eventsListCategorySpinner.getSelectedItem();
        binding.eventsListTitle.setText(strEventCategory + " Events");
        //eventsRef = FirebaseDatabase.getInstance().getReference().child("Events").child(strEventCategory);
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Events").child("Help Me!");*/

        setListeners();

        //addEventFab.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        //displayEventsList();
    }

    private void setListeners() {
        //binding.eventsListCategorySpinner.setOnItemSelectedListener(this);
        //binding.eventsListTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                eventsListViewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    eventsListPageAdapter.notifyDataSetChanged();
                } else if (tab.getPosition() == 1) {
                    eventsListPageAdapter.notifyDataSetChanged();
                } else if (tab.getPosition() == 2) {
                    eventsListPageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //
        //binding.eventsListViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.eventsListTabLayout));
        eventsListViewPager.setOffscreenPageLimit(0);
        eventsListViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

/*    private void displayEventsList() {
        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions
                .Builder<Event>()
                .setQuery(eventsRef, Event.class)
                .build();
        FirebaseRecyclerAdapter<Event, EventsViewHolder> firebaseRecyclerAdapter
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
                        MainActivity.getNavController().navigate(R.id.action_eventsListFragment_to_eventFragment);
                    }
                });
            }
        };
        binding.eventsListRecyclerView.setAdapter(firebaseRecyclerAdapter);
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
    }*/

/*    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        strEventCategory = (String) parent.getItemAtPosition(position);
        Log.d(TAG, "eventCategory is " + strEventCategory);
        binding.eventsListTitle.setText(strEventCategory + " Events");
        // displayEventsList();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        navController.navigate(R.id.action_eventsListFragment_to_createEventFragment);
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
