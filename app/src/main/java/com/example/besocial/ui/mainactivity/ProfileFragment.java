package com.example.besocial.ui.mainactivity;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.besocial.R;
import com.example.besocial.data.User;
import com.example.besocial.databinding.FragmentProfileBinding;
import com.example.besocial.ui.chatactivity.ChatActivity;
import com.example.besocial.utils.BitmapUtils;
import com.example.besocial.utils.ConstantValues;
import com.example.besocial.utils.DatePickerFragment;
import com.example.besocial.utils.EventDatePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;


    private CircleImageView profileProfilePicture;
    private TextView profilePageUsername;
    private EditText profileFullName, profileEmail, profileCity, profileAddress, profileSocialLevel, profileSocialPoints;
    private User loggedUser, userData;
    private Button profileSaveDetails, profileMyPictures, blockUserButton;
    private final static int galleryPick = 1;
    private ImageButton profileChangeProfilePicture, profileEditProfileDetails;
    private ImageButton newChatButton;
    private FirebaseDatabase firebaseDatabase;
    private static DatabaseReference userRef, userDataRef;
    private StorageReference userPicturesRef;
    private NavController navController;
    private static UsersViewModel mViewModel;
    private TextView myProfileTextView;
    String TAG = "ProfileFragment";
    private boolean wasMyPicturesClicked = false;
    private String chosenBirthdayDate;
    private static String tempId;

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
        newChatButton = view.findViewById(R.id.profile_open_chat_conversation);
        newChatButton.setVisibility(View.INVISIBLE);
        myProfileTextView = view.findViewById(R.id.profile_my_profile_text_view);
        profileChangeProfilePicture = view.findViewById(R.id.profile_change_profile_picture);
        profilePageUsername = view.findViewById(R.id.profile_page_username);
        profileFullName = view.findViewById(R.id.profile_full_name);
        profileAddress = view.findViewById(R.id.profile_adress);
        profileSocialLevel = view.findViewById(R.id.profile_social_level);
        profileSocialPoints = view.findViewById(R.id.profile_social_points);
        profileCity = view.findViewById(R.id.profile_city);
        profileEmail = view.findViewById(R.id.profile_email);
        blockUserButton = view.findViewById(R.id.profile_block_user_button);
        profileSaveDetails = view.findViewById(R.id.profile_save_new_details);
        profileEditProfileDetails = view.findViewById(R.id.profile_edit_profile_details);
        profileMyPictures = view.findViewById(R.id.profile_my_pictures);
        userData = mViewModel.getUser().getValue();
        firebaseDatabase = FirebaseDatabase.getInstance();
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        loggedUser = MainActivity.getLoggedUser();
        userPicturesRef = FirebaseStorage.getInstance().getReference().child(MainActivity.getCurrentUser().getUid());
        if (userData != null) {
            setProfileDetails();
        }

        if ((userData != null) && !MainActivity.getFireBaseAuth().getUid().equals(userData.getUserId())) {
            myProfileTextView.setVisibility(View.INVISIBLE);
            profileChangeProfilePicture.setVisibility(View.INVISIBLE);
            profileEditProfileDetails.setVisibility(View.INVISIBLE);
            profileMyPictures.setText("UPLOADED PHOTOS");
            myProfileTextView.setVisibility(View.INVISIBLE);
            newChatButton.setVisibility(View.VISIBLE);
            String status, name = "";
            if (MainActivity.getLoggedUser().getIsManager().booleanValue() == true) {
                name = userData.getUserFirstName();
                status = userData.getAccountStatus();
                if (userData.getAccountStatus().equals("Blocked")) {
                    blockUserButton.setText("RETRIEVE USER");
                    blockUserButton.setBackgroundColor(getResources().getColor(R.color.greenRetrieveUserButton));
                } else if (userData.getAccountStatus().equals("Active")) {
                    blockUserButton.setText("BLOCK USER");
                    blockUserButton.setBackgroundColor(getResources().getColor(R.color.redBlockUserButton));
                }
                blockUserButton.setVisibility(View.VISIBLE);
            }

        }
        userRef = MainActivity.getCurrentUserDatabaseRef();
        setListeners();

    }

    void setProfileDetails() {
        if(userData.getUserId().equals(MainActivity.getLoggedUser().getUserId())){
            tempId="";
        } else{
            tempId=userData.getUserId();
        }
        profileEmail.setText(userData.getUserEmail());
        profilePageUsername.setText(userData.getUserFirstName() + " " + userData.getUserLastName());
        profileFullName.setText(userData.getUserFirstName() + " " + userData.getUserLastName());
        profileAddress.setText(userData.getUserAddress());
        profileCity.setText(userData.getUserCity());
        profileSocialLevel.setText(userData.getSocialLevel());
        profileSocialPoints.setText(userData.getSocialPoints().toString());
        binding.profileBirthday.setText(userData.getBirthday());
        String myProfileImage = userData.getProfileImage();
        Glide.with(getContext()).load(myProfileImage).placeholder(R.drawable.empty_profile_image).into(profileProfilePicture);
    }

    private void setListeners() {
        profileMyPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wasMyPicturesClicked = true;
                navController.navigate(R.id.action_nav_my_profile_to_photosListFragment);
            }
        });

        profileEditProfileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loggedUser != null) {
                    profileAddress.setEnabled(true);
                    profileCity.setEnabled(true);
                    binding.profileBirthday.setEnabled(true);
                    profileSaveDetails.setVisibility(View.VISIBLE);
                    profileEditProfileDetails.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(getActivity(), "There was a problem, please check your connection to the internet", Toast.LENGTH_SHORT).show();
                }
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
                    userMap.put("birthday", binding.profileBirthday.getText().toString());
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
                            binding.profileBirthday.setEnabled(false);
                        }
                    });


                } else {
                    Toast.makeText(getActivity(), "Fields are not filled correctly", Toast.LENGTH_LONG).show();
                }
            }
        });
        binding.profileBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        profileChangeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, galleryPick);
            }
        });

        binding.profileOpenChatConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChatConversation();
            }
        });

        blockUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userData = mViewModel.getUser().getValue();
                if (userData.getAccountStatus().equals("Active"))
                    changeUserStatus(userData.getUserId(), "Blocked");
                else
                    changeUserStatus(userData.getUserId(), "Active");
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerFragment datePickerFragment = new BirthdayPicker(binding.profileBirthday);
        datePickerFragment.show(getFragmentManager(), null);
    }

    public static class BirthdayPicker extends DatePickerFragment {
        private TextView profileBirthday;
        public BirthdayPicker(TextView profileBirthday) {
            super();
            this.profileBirthday=profileBirthday;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return super.onCreateDialog(savedInstanceState);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            super.onDateSet(view, year, month, day);
            this.profileBirthday.setText(strChosenDate);

        }
    }


    private void openChatConversation() {
        Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
        chatIntent.putExtra(ConstantValues.LOGGED_USER_ID, MainActivity.getLoggedUser().getUserId());
        chatIntent.putExtra("chosenUid", userData.getUserId());
        startActivity(chatIntent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == galleryPick && resultCode == Activity.RESULT_OK && data != null) {
            uploadImageToStorage(data);
        }
    }

    private void uploadImageToStorage(Intent data) {
        Uri imageUri = data.getData();
        Glide.with(getContext()).load(imageUri).into(binding.profileUserProfilePicture);
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
        if (wasMyPicturesClicked == false) {
            mViewModel.setUser(loggedUser);
        } else {
            wasMyPicturesClicked = false;
        }
    }


    public String generateRandomId() {
        byte[] array = new byte[20]; // length is bounded by 7
        new Random().nextBytes(array);
        String generatedId = new String(array, Charset.forName("UTF-8"));
        return generatedId;

    }

    private void changeUserStatus(String userIdToChangeStatus, String newStatus) {
        userDataRef = FirebaseDatabase.getInstance().getReference().child(ConstantValues.USERS).child(userIdToChangeStatus).child("accountStatus");
        if (newStatus.equals("Blocked")) {
            blockUserButton.setText("RETRIEVE USER");
            blockUserButton.setBackgroundColor(getResources().getColor(R.color.greenRetrieveUserButton));
        } else if (newStatus.equals("Active")) {
            blockUserButton.setText("BLOCK USER");
            blockUserButton.setBackgroundColor(getResources().getColor(R.color.redBlockUserButton));
        }
        userDataRef.setValue(newStatus);
        User user = mViewModel.getUser().getValue();
        user.setAccountStatus(newStatus);
        mViewModel.setUser(user);
    }

    public static String getTempId() {
        return tempId;
    }

}

