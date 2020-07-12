package com.example.besocial.ui.mainactivity.socialcenter;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.example.besocial.R;
import com.example.besocial.databinding.FragmentSocialCenterBinding;
import com.example.besocial.ui.mainactivity.MainActivity;
import com.example.besocial.utils.ConstantValues;


/**
 * A simple {@link Fragment} subclass.
 */
public class SocialCenterFragment extends Fragment implements View.OnClickListener {
    private FragmentSocialCenterBinding binding;

    NavController navController;
    public SocialCenterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSocialCenterBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListeners();
        navController = MainActivity.getNavController();
    }

    private void setListeners() {
        binding.socialEventButton.setOnClickListener(this);
        binding.bonusAreaButton.setOnClickListener(this);
        binding.volunteerButton.setOnClickListener(this);
        binding.getHelpButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        if (v.getId() == R.id.getHelpButton) {
            navController.navigate(R.id.getHelpFragment,bundle);
        }
        if (v.getId() == R.id.socialEventButton) {
            navController.navigate(R.id.action_nav_social_center_to_eventsListFragment,bundle);
        }
        if (v.getId() == R.id.volunteerButton) {
            //bundle.putBoolean(ConstantValues.IS_HELP_EVENT, true);
            navController.navigate(R.id.volunteerFragment,bundle);
        }
        if(v.getId() == R.id.bonusAreaButton){
            navController.navigate(R.id.action_nav_social_center_to_nav_bonus_area);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding=null;
    }
}
