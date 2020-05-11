package com.example.besocial.ui.mainactivity;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.besocial.utils.ConstantValues;
import com.example.besocial.R;
import com.example.besocial.data.ChatConversation;
import com.example.besocial.data.User;
import com.example.besocial.databinding.FragmentProfileBinding;
import com.example.besocial.utils.BitmapUtils;
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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private CircleImageView profileProfilePicture;
    private TextView profilePageUsername;
    private EditText profileFullName, profileEmail, profileCity, profileAddress, profileBirthday, profileSocialLevel, profileSocialPoints;
    private User loggedUser,userData;
    private Button profileSaveDetails, profileFollowList, profileMyPictures;
    private final static int galleryPick = 1;
    private ImageButton profileChangeProfilePicture, profileEditProfileDetails;
    private ImageButton newChatButton;
    private FirebaseDatabase firebaseDatabase;
    private static DatabaseReference userRef,chatRef;
    private StorageReference userPicturesRef;
    private NavController navController;
    private static UsersViewModel mViewModel;


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
        mViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);
        profileProfilePicture = view.findViewById(R.id.profile_user_profile_picture);
        newChatButton=view.findViewById(R.id.new_chat_conversation_button);
        newChatButton.setVisibility(View.INVISIBLE);
        profileChangeProfilePicture = view.findViewById(R.id.profile_change_profile_picture);
        profilePageUsername = view.findViewById(R.id.profile_page_username);
        profileFullName = view.findViewById(R.id.profile_full_name);
        profileAddress = view.findViewById(R.id.profile_adress);
        profileSocialLevel = view.findViewById(R.id.profile_social_level);
        profileSocialPoints = view.findViewById(R.id.profile_social_points);
        profileCity = view.findViewById(R.id.profile_city);
        profileEmail = view.findViewById(R.id.profile_email);
        profileBirthday = view.findViewById(R.id.profile_birthday);
        profileSaveDetails = view.findViewById(R.id.profile_save_new_details);
        profileEditProfileDetails = view.findViewById(R.id.profile_edit_profile_details);
        profileMyPictures = view.findViewById(R.id.profile_my_pictures);
        profileFollowList = view.findViewById(R.id.profile_follow_list);
        userData=mViewModel.getUser().getValue();
        firebaseDatabase = FirebaseDatabase.getInstance();
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        loggedUser = MainActivity.getLoggedUser();
        userPicturesRef = FirebaseStorage.getInstance().getReference().child(MainActivity.getCurrentUser().getUid());

        profilePageUsername.setText(userData.getUserFirstName() + " " + userData.getUserLastName());
        profileEmail.setText(userData.getUserEmail());
        profileFullName.setText(userData.getUserFirstName() + " " + userData.getUserLastName());
        profileAddress.setText(userData.getUserAddress());
        profileCity.setText(userData.getUserCity());
        profileSocialLevel.setText(userData.getSocialLevel());
        profileSocialPoints.setText(userData.getSocialPoints().toString());
        profileBirthday.setText(userData.getBirthday());
        String myProfileImage = userData.getProfileImage();
        if(!loggedUser.getUserId().equals(userData.getUserId())){
            profileChangeProfilePicture.setVisibility(View.INVISIBLE);
            profileEditProfileDetails.setVisibility(View.INVISIBLE);
            profileMyPictures.setText("UPLOADED PHOTOS");
            profileFollowList.setText("FOLLOW LIST");
            newChatButton.setVisibility(View.VISIBLE);
        }
        userRef = MainActivity.getCurrentUserDatabaseRef();
        Glide.with(getContext()).load(myProfileImage).placeholder(R.drawable.empty_profile_image).into(profileProfilePicture);
        setNewChatListener();

        profileMyPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_nav_my_profile_to_photosListFragment);
            }
        });


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

                boolean isFieldsValid = true;
                if (profileAddress.getText().toString().equals(""))
                    isFieldsValid = false;
                if (profileCity.getText().toString().equals(""))
                    isFieldsValid = false;
                if (isFieldsValid == true) {
                    HashMap userMap = new HashMap();
                    userMap.put("userAddress", profileAddress.getText().toString());
                    userMap.put("userCity", profileCity.getText().toString());
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
                            profileAddress.setEnabled(false);
                            profileCity.setEnabled(false);
                            profileBirthday.setEnabled(false);
                        }
                    });


                } else {
                    Toast.makeText(getActivity(), "Fields are not filled correctly", Toast.LENGTH_LONG).show();
                }
            }
        });

        profileChangeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                //CropImage.activity()
                //  .start(getView().getContext(), this);

                startActivityForResult(galleryIntent, galleryPick);
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == galleryPick && resultCode == Activity.RESULT_OK && data != null) {
            uploadImageToStorage(data);
        }

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
        //profileProfilePicture.setImageURI(imageUri);

    }

    private void uploadImageToStorage(Intent data) {
        Uri imageUri = data.getData();
        final StorageReference profileImagesRef = userPicturesRef.child("profileImages/" + imageUri.getLastPathSegment());
        BitmapUtils rotateNcompress = new BitmapUtils();
        byte[] compressedPhoto = new byte[0];
        try {
            compressedPhoto = rotateNcompress.compressAndRotateBitmap(getContext(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        profileImagesRef.putBytes(compressedPhoto).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profileImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference imageStore = FirebaseDatabase.getInstance().getReference().child("users")
                                .child(MainActivity.getLoggedUser().getUserId()).child("profileImage");
                        imageStore.setValue(uri.toString());
                        Toast.makeText(getContext(), "Image was uploaded successfully.", Toast.LENGTH_SHORT).show();
                        Glide.with(getContext()).load(uri).placeholder(R.drawable.empty_profile_image).into(profileProfilePicture);
                    }
                });
            }
        });
    }

/*        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                Toast.makeText(getActivity(), "Change profile successfuly", Toast.LENGTH_LONG).show();

                profileProfilePicture.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }*/


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        mViewModel.setUser(loggedUser);
    }

    void setNewChatListener(){
        newChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendFrom,sendTo,sendFromId,sendToId,chatId;
                sendTo=profileFullName.getText().toString();
                sendToId=userData.getUserId();
                sendFrom=loggedUser.getUserFirstName()+" "+loggedUser.getUserLastName();
                sendFromId= loggedUser.getUserId();

                chatRef = FirebaseDatabase.getInstance().getReference().child(ConstantValues.CHATS).child(sendFromId);
                if(chatRef!=null) {

                    chatRef = chatRef.push();
                }
                chatId=chatRef.getKey();
                ChatConversation newChatConversation1=new ChatConversation
                        (sendFrom,sendTo,chatId,userData.getProfileImage());
                chatRef.setValue(newChatConversation1);

                chatRef = FirebaseDatabase.getInstance().getReference().child(ConstantValues.CHATS).child(sendToId).child(chatId);
                ChatConversation newChatConversation2=new ChatConversation
                        (sendTo,sendFrom,chatId,loggedUser.getProfileImage());
                chatRef.setValue(newChatConversation2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            navController.navigate(R.id.action_nav_my_profile_to_nav_chat);
                        }
                    }
                });
            }
        });
    }


    public String generateRandomId() {
        byte[] array = new byte[20]; // length is bounded by 7
        new Random().nextBytes(array);
        String generatedId = new String(array, Charset.forName("UTF-8"));
        return generatedId;

    }
}

