package com.example.besocial.ui.mainactivity.socialcenter;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.besocial.R;
import com.example.besocial.data.Event;
import com.example.besocial.databinding.FragmentEventBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {
    private FragmentEventBinding binding;
    private Event chosenEvent;
    private SocialCenterViewModel socialCenterViewModel;

    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        socialCenterViewModel = ViewModelProviders.of(getActivity()).get(SocialCenterViewModel.class);
        chosenEvent=socialCenterViewModel.getEvent().getValue();
    }

    @Override
    public void onStart() {
        super.onStart();
        setEventDetails();
    }

    private void setEventDetails() {
        Glide.with(getContext()).load(chosenEvent.getStrEventPhotoUrl()).placeholder(R.drawable.social_event0).into(binding.fragmentEventPhoto);
        binding.fragmentEventTitle.setText(chosenEvent.getTitle());
        binding.fragmentEventHostFullName.setText("Hosted by: "+chosenEvent.getEventCreatorUserName());
        binding.fragmentEventDateTime.setText(chosenEvent.getBeginDate() + "," + chosenEvent.getBeginTime()
                + "-" + chosenEvent.getFinishDate() + "," + chosenEvent.getFinishTime());
        binding.fragmentEventLocation.setText(chosenEvent.getLocationTitle());
        binding.fragmentEventDescription.setText(chosenEvent.getDescription());
    }
}
