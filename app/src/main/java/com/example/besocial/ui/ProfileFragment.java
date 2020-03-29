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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.besocial.MainActivity;
import com.example.besocial.R;
import com.example.besocial.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private CircleImageView profileProfilePicture;
    private TextView profilePageUsername;
    private EditText profileFullName,profileEmail,profileCity,profileAddress,profileBirthday,profileSocialLevel,profileSocialPoints;
    private User loggedUser;
    private Button profileSaveDetails;
    private final static int galleryPick=1;
    private ImageButton profileChangeProfilePicture,profileEditProfileDetails;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;
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
        profileProfilePicture=view.findViewById(R.id.profile_user_profile_picture);
        profileChangeProfilePicture=view.findViewById(R.id.profile_change_profile_picture);
        profilePageUsername=view.findViewById(R.id.profile_page_username);
        profileFullName=view.findViewById(R.id.profile_full_name);
        profileAddress=view.findViewById(R.id.profile_adress);
        profileSocialLevel=view.findViewById(R.id.profile_social_level);
        profileSocialPoints=view.findViewById(R.id.profile_social_points);
        profileCity=view.findViewById(R.id.profile_city);
        profileEmail=view.findViewById(R.id.profile_email);
        profileBirthday=view.findViewById(R.id.profile_birthday);
        profileSaveDetails=view.findViewById(R.id.profile_save_new_details);
        profileEditProfileDetails=view.findViewById(R.id.profile_edit_profile_details);
        firebaseDatabase = FirebaseDatabase.getInstance();
        loggedUser=MainActivity.getLoggedUser();
        profilePageUsername.setText(loggedUser.getUserFirstName()+"  "+loggedUser.getUserLastName());
        profileEmail.setText(loggedUser.getUserEmail());
        profileFullName.setText(loggedUser.getUserFirstName()+"  "+loggedUser.getUserLastName());
        profileAddress.setText(loggedUser.getUserAddress());
        profileCity.setText(loggedUser.getUserCity());
        profileSocialLevel.setText(loggedUser.getSocialLevel());
        //profileSocialPoints.setText(loggedUser.getSocialPoints());



        profileEditProfileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileAddress.setEnabled(true);
                profileCity.setEnabled(true);
                profileBirthday.setEnabled(true);
                profileSaveDetails.setVisibility(View.VISIBLE);
                profileEditProfileDetails.setVisibility(View.INVISIBLE);
            }
        });

        profileSaveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef=MainActivity.getCurrentUserDatabaseRef();

                boolean isFieldsValid=true;
               // String[] name=profileFullName.getText().toString().split(" ");
               // if(name[0].equals("") || name[1].equals(""))
                    //isFieldsValid=false;
                if(profileAddress.getText().toString().equals(""))
                    isFieldsValid=false;
                if(profileCity.getText().toString().equals(""))
                    isFieldsValid=false;
                if(isFieldsValid==true) {
                    HashMap userMap = new HashMap();
                    //userMap.put("userFirstName", name[0]);
                    //userMap.put("userLastName", name[1]);
                    userMap.put("userAddress",profileAddress.getText().toString());
                    userMap.put("userCity",profileCity.getText().toString());
                    userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Changed details successfuly", Toast.LENGTH_SHORT).show();
                            } else {
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(getActivity(), "Could not change details: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                            profileSaveDetails.setVisibility(View.INVISIBLE);
                            profileEditProfileDetails.setVisibility(View.VISIBLE);
                        }
                    });
                }
                else{
                    Toast.makeText(getActivity(), "Fields are not filled correctly", Toast.LENGTH_LONG).show();
                }
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


    }



}

