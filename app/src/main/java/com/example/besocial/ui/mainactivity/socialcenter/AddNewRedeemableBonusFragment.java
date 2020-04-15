package com.example.besocial.ui.mainactivity.socialcenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.besocial.ConstantValues;
import com.example.besocial.R;
import com.example.besocial.data.RedeemableBenefit;
import com.example.besocial.ui.mainactivity.MainActivity;
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

    private ImageView newBenefitPhoto;
    private Button clearFields,saveNewBenefit;
    private String name,description,costString,category;
    private long cost;
    private Spinner listOfCategories;
    private final static int galleryPick=1;
    private EditText newBenefitName,newBenefitDescription,newBenefitCost;
    private RedeemableBenefit newRedeemableBenefit;
    private Uri imageUri;
    private ProgressDialog loadingBar;
    private boolean photoSet;
    private StorageReference benefitsPicturesRef;
    private byte[] imageInByte=null;

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
        return inflater.inflate(R.layout.fragment_add_new_redeemable_bonus, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newBenefitPhoto=view.findViewById(R.id.new_benefit_photo);
        saveNewBenefit=view.findViewById(R.id.new_benefit_save_benefit);
        clearFields=view.findViewById(R.id.new_benefit_clear_fields);
        newBenefitCost=view.findViewById(R.id.new_benefit_cost);
        newBenefitDescription=view.findViewById(R.id.new_benefit_description);
        newBenefitName=view.findViewById(R.id.new_benefit_name);
        listOfCategories=view.findViewById(R.id.new_benefit_categories);
        benefitsPicturesRef= FirebaseStorage.getInstance().getReference().child(ConstantValues.BENEFITS);
        ArrayAdapter<CharSequence> arrayAdapter= ArrayAdapter.createFromResource(getContext(),R.array.list_of_bonus_area_categories,R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        listOfCategories.setAdapter(arrayAdapter);
        loadingBar = new ProgressDialog(getContext());
        photoSet=false;
        setListeners();
    }



    void clearFields() {
        newBenefitName.setText("");
        newBenefitDescription.setText("");
        newBenefitCost.setText("");
        listOfCategories.setSelection(0);
    }

    boolean checkFields(String name,String description,String costString){
        boolean isOk=true;
        long cost=0;
        int categoryPosition=0;
        categoryPosition=listOfCategories.getSelectedItemPosition();
        if(costString.equals(""))
            name="";
        else cost=Long.parseLong(costString);
        if(categoryPosition==0||name.equals("")||description.equals("")||cost<1 ){
            isOk = false;
            Toast.makeText(getActivity(), "Incorrect Fields", Toast.LENGTH_SHORT).show();
        }
        else if(photoSet==false){
            isOk=false;
            Toast.makeText(getActivity(), "You have to set a photo for this benefit", Toast.LENGTH_SHORT).show();
        }
        return isOk;
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==newBenefitPhoto.getId()){
            Intent galleryIntent=new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");

            startActivityForResult(galleryIntent,galleryPick);
        }
        if(v.getId()==clearFields.getId()){
            clearFields();
        }
        if(v.getId()==saveNewBenefit.getId()){
            storeImageToFirebaseStorage();
        }
    }
    void storeImageToFirebaseStorage() {
        if(photoSet==false) {
            Toast.makeText(getActivity(), "You have to set a photo for this benefit", Toast.LENGTH_SHORT).show();
        }else {
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
                                Toast.makeText(getContext(), "image uploaded successfully to Storage...", Toast.LENGTH_SHORT).show();
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
        name=newBenefitName.getText().toString();
        description=newBenefitDescription.getText().toString();
        costString=newBenefitCost.getText().toString();
        category=listOfCategories.getSelectedItem().toString();
        if(checkFields(name,description,costString)==true){
            DatabaseReference benefitsRef = FirebaseDatabase.getInstance().getReference();
            newRedeemableBenefit=new RedeemableBenefit(name,description,category,costString,imageUri.toString());
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

    void setListeners(){
        newBenefitPhoto.setOnClickListener(this);
        clearFields.setOnClickListener(this);
        saveNewBenefit.setOnClickListener(this);
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
                Glide.with(getContext()).load(imageInByte).centerCrop().into(newBenefitPhoto);
                photoSet=true;

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

