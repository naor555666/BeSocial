package com.example.besocial.ui.mainactivity;


import android.app.Activity;
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

import com.example.besocial.R;
import com.example.besocial.data.User;
import com.example.besocial.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

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
    private static DatabaseReference userRef;
    private StorageReference userPicturesRef;

    // an instance of the layout
    private FragmentProfileBinding binding;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
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
        userPicturesRef= FirebaseStorage.getInstance().getReference().child(MainActivity.getCurrentUser().getUid());
        profilePageUsername.setText(loggedUser.getUserFirstName()+"  "+loggedUser.getUserLastName());
        profileEmail.setText(loggedUser.getUserEmail());
        profileFullName.setText(loggedUser.getUserFirstName()+"  "+loggedUser.getUserLastName());
        profileAddress.setText(loggedUser.getUserAddress());
        profileCity.setText(loggedUser.getUserCity());
        profileSocialLevel.setText(loggedUser.getSocialLevel());
        profileSocialPoints.setText(loggedUser.getSocialPoints());
        profileBirthday.setText(loggedUser.getBirthday());
        userRef=MainActivity.getCurrentUserDatabaseRef();






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

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String myProfileImage=dataSnapshot.child("profileImage").getValue().toString();
                    Picasso.with(getContext()).load(myProfileImage).into(profileProfilePicture);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==galleryPick && resultCode== Activity.RESULT_OK && data!=null){
            Uri imageUri=data.getData();
            final StorageReference imageName=userPicturesRef.child("image "+imageUri.getLastPathSegment() );
            imageName.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getActivity(), "Profile picture changed", Toast.LENGTH_LONG).show();
                    imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            DatabaseReference imageStore= FirebaseDatabase.getInstance().getReference().child("users").child(MainActivity.getCurrentUser().getUid()).child("profileImage");
                            imageStore.setValue((String)uri.toString());
                        }
                    });
                }
            });
            //userPicturesRef.child("profilePicture"+".jpg");
            /*
            userPicturesRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()){
                        final String downloadUrl=task.getResult().getUploadSessionUri().toString();
                        userRef.child("profileImage").setValue(downloadUrl);
                        Toast.makeText(getActivity(), "Profile picture changed", Toast.LENGTH_LONG).show();
                    }

                }
            });

             */
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}

