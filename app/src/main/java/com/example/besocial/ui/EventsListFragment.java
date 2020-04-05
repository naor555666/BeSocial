package com.example.besocial.ui;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.besocial.MainActivity;
import com.example.besocial.R;
import com.example.besocial.data.Event;
import com.example.besocial.databinding.FragmentEventBinding;
import com.example.besocial.databinding.FragmentEventsListBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsListFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    public static final String TAG = "EventsListFragment";

    private FragmentEventsListBinding binding;
    private NavController navController;
    private FloatingActionButton addEventFab;
    private DatabaseReference eventsRef;
    private String strEventCategory;
    private boolean isHelpEvent;

    public EventsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventsListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_events_list, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = MainActivity.getNavController();

        binding.eventsListRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.eventsListRecyclerView.setLayoutManager(linearLayoutManager);


        //initializing the spinner list of categories
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.list_of_categories, R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        binding.eventsListCategorySpinner.setAdapter(arrayAdapter);
        if (!(getArguments().isEmpty())) {
            isHelpEvent = getArguments().getBoolean(SocialCenterFragment.IS_HELP_EVENT);
            binding.eventsListCategorySpinner.setSelection(binding.eventsListCategorySpinner.getAdapter().getCount() - 1);
            binding.eventsListCategorySpinner.setEnabled(false);
        }

        strEventCategory = (String) binding.eventsListCategorySpinner.getSelectedItem();

        //eventsRef= FirebaseDatabase.getInstance().getReference().child("Events").child(strEventCategory);

        setListeners();
        //displayEventsList();
        //addEventFab.setOnClickListener(this);
    }

    private void setListeners() {
        binding.eventsListCategorySpinner.setOnItemSelectedListener(this);
    }

    private void displayEventsList() {
/*        FirebaseRecyclerAdapter<Event, EventsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Event, EventsViewHolder>(Event.class,R.layout.event_node,EventsViewHolder.class,) {
            @NonNull
            @Override
            public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            protected void onBindViewHolder(@NonNull EventsViewHolder holder, int position, @NonNull Event model) {

            }
        }*/

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        strEventCategory = (String) parent.getItemAtPosition(position);
        Log.d(TAG, "eventCategory is " + strEventCategory);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public static class EventsViewHolder extends RecyclerView.ViewHolder {
        private View mview;

        public EventsViewHolder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;
        }
    }


    @Override
    public void onClick(View v) {
        navController.navigate(R.id.action_eventsListFragment_to_createEventFragment);
    }
}
