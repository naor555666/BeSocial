package com.example.besocial.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.besocial.MainActivity;
import com.example.besocial.R;
import com.example.besocial.data.User;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private CircleImageView profileProfilePicture;
    private TextView profilePageUsername;
    private EditText profileFullName,profileEmail,profileCity,profileAddress,profileBirthday;
    private User loggedUser;
    private final static int galleryPick=1;
    private ImageButton profileChangeProfilePicture,profileEditProfileDetails;
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
        profileProfilePicture=view.findViewById(R.id.profile_user_profile_picture);
        profileChangeProfilePicture=view.findViewById(R.id.profile_change_profile_picture);
        profilePageUsername=view.findViewById(R.id.profile_page_username);
        profileFullName=view.findViewById(R.id.profile_full_name);
        profileAddress=view.findViewById(R.id.profile_adress);
        profileCity=view.findViewById(R.id.profile_city);
        profileEmail=view.findViewById(R.id.profile_email);
        profileBirthday=view.findViewById(R.id.profile_birthday);
        profileEditProfileDetails=view.findViewById(R.id.profile_edit_profile_details);
        profilePageUsername.setText(loggedUser.getUserFirstName()+"  "+loggedUser.getUserLastName());
        profileEmail.setText(loggedUser.getUserEmail());
        profileFullName.setText(loggedUser.getUserFirstName()+"  "+loggedUser.getUserLastName());
        profileEditProfileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileAddress.setEnabled(true);
                profileCity.setEnabled(true);
                profileBirthday.setEnabled(true);
                profileFullName.setEnabled(true);
            }
        });

        profileChangeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                //CropImage.activity()
                      //  .start(getView().getContext(), this);

                 startActivityForResult(galleryIntent,galleryPick);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==galleryPick && resultCode== Activity.RESULT_OK && data!=null){
            Uri imageUri=data.getData();
           // CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(getActivity());
            profileProfilePicture.setImageURI(imageUri);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                Toast.makeText(getActivity(), "Change profile successfuly", Toast.LENGTH_LONG).show();

                profileProfilePicture.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        //hanan


    }
}
