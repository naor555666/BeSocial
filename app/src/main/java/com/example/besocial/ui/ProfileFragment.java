package com.example.besocial.ui;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.besocial.MainActivity;
import com.example.besocial.R;
import com.example.besocial.data.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private TextView profilePageUsername;
    private EditText profileFullName,profileEmail;
    User loggedUser;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loggedUser=MainActivity.getLoggedUser();
        profilePageUsername=view.findViewById(R.id.profile_page_username);
        profileFullName=view.findViewById(R.id.profile_full_name);
        profileEmail=view.findViewById(R.id.profile_email);
        profilePageUsername.setText(loggedUser.getUserFirstName()+"  "+loggedUser.getUserLastName());
        profileEmail.setText(loggedUser.getUserEmail());
        profileFullName.setText(loggedUser.getUserFirstName()+"  "+loggedUser.getUserLastName());
    }
}
