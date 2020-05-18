package com.example.besocial.ui.mainactivity.socialcenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.besocial.utils.ConstantValues;
import com.example.besocial.R;
import com.example.besocial.data.RedeemableBenefit;
import com.example.besocial.databinding.FragmentAddNewRedeemableBonusBinding;
import com.example.besocial.utils.BitmapUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;


public class AddNewRedeemableBonusFragment extends Fragment implements View.OnClickListener {
    private FragmentAddNewRedeemableBonusBinding binding;

    private String name, description, costString, category;
    private long cost;

    private final static int galleryPick = 1;
    private RedeemableBenefit newRedeemableBenefit;
    private Uri imageUri;
    private ProgressDialog loadingBar;
    private boolean photoSet;
    private StorageReference benefitsPicturesRef;
    private byte[] imageInByte = null;

    public AddNewRedeemableBonusFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddNewRedeemableBonusBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        benefitsPicturesRef = FirebaseStorage.getInstance().getReference().child(ConstantValues.BENEFITS);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.list_of_bonus_area_categories, R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        binding.newBenefitCategories.setAdapter((arrayAdapter));
        arrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.list_of_social_levels, R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        binding.newBenefitSocialLevelRequired.setAdapter(arrayAdapter);
        loadingBar = new ProgressDialog(getContext());
        photoSet = false;
        setListeners();
    }


    void clearFields() {
        binding.newBenefitName.setText("");
        binding.newBenefitDescription.setText("");
        binding.newBenefitCost.setText("");
        binding.newBenefitCategories.setSelection(0);
    }

    boolean checkFields(String name, String description, String costString) {
        boolean isOk = true;
        long cost = 0;
        int categoryPosition = 0,socialLevelPosition=0;
        socialLevelPosition=binding.newBenefitSocialLevelRequired.getSelectedItemPosition();
        categoryPosition =binding.newBenefitCategories.getSelectedItemPosition();
        if (costString.equals(""))
            name = "";
        else cost = Long.parseLong(costString);
        if (categoryPosition == 0 || socialLevelPosition==0|| name.equals("") || description.equals("") || cost < 1) {
            isOk = false;
            Toast.makeText(getActivity(), "Incorrect Fields", Toast.LENGTH_SHORT).show();
        } else if (photoSet == false) {
            isOk = false;
            Toast.makeText(getActivity(), "You have to set a photo for this benefit", Toast.LENGTH_SHORT).show();
        }
        return isOk;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == binding.newBenefitPhoto.getId()) {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");

            startActivityForResult(galleryIntent, galleryPick);
        }
        if (v.getId() == binding.newBenefitClearFields.getId()) {
            clearFields();
        }
        if (v.getId() == binding.newBenefitSaveBenefit.getId()) {
            storeImageToFirebaseStorage();
        }
    }

    void storeImageToFirebaseStorage() {
        if (photoSet == false) {
            Toast.makeText(getActivity(), "You have to set a photo for this benefit", Toast.LENGTH_SHORT).show();
        } else {
            showLoadingBar();
            final StorageReference filePath = benefitsPicturesRef.child("image " + imageUri.getLastPathSegment());

            filePath.putBytes(imageInByte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageUri = uri;
                                saveBenefitInformationToDatabase();
                            }
                        });

                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(getContext(), "Error occured: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void saveBenefitInformationToDatabase() {
        name = binding.newBenefitName.getText().toString();
        description = binding.newBenefitDescription.getText().toString();
        costString = binding.newBenefitCost.getText().toString();
        category = binding.newBenefitCategories.getSelectedItem().toString();
        if (checkFields(name, description, costString) == true) {
            DatabaseReference benefitsRef = FirebaseDatabase.getInstance().getReference();
            newRedeemableBenefit = new RedeemableBenefit(name, description, category, Long.parseLong(costString), imageUri.toString());
            benefitsRef.child(ConstantValues.BENEFITS).child(category).child(name)
                    .setValue(newRedeemableBenefit).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "New benefit is updated successfully.", Toast.LENGTH_SHORT).show();
                        getFragmentManager().popBackStack();
                        loadingBar.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Error Occured while updating the new benefit", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    void setListeners() {
        binding.newBenefitPhoto.setOnClickListener(this);
        binding.newBenefitClearFields.setOnClickListener(this);
        binding.newBenefitSaveBenefit.setOnClickListener(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == galleryPick && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            // make compressed image

            try {
                BitmapUtils rotateBitmap = new BitmapUtils();
                imageInByte = rotateBitmap.compressAndRotateBitmap(getActivity(), imageUri);
                Glide.with(getContext()).load(imageInByte).centerCrop().into(binding.newBenefitPhoto);
                photoSet = true;

            } catch (IOException e) {
                e.printStackTrace();
            }


            //

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
            //newBenefitPhoto.setImageURI(imageUri);
        }
    }

    private void showLoadingBar() {
        loadingBar.setTitle("Add New Benefit");
        loadingBar.setMessage("Please wait, while we are updating the new benefit...");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
    //        imageName.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        // DatabaseReference imageStore= FirebaseDatabase.getInstance().getReference().child(ConstantValues.BENEFITS).child(MainActivity.getCurrentUser().getUid()).child("profileImage");
//                        //imageStore.setValue((String)uri.toString());
//                    }
//                });
//            }
//        });
}

