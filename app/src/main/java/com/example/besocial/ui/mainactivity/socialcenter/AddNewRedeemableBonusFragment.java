package com.example.besocial.ui.mainactivity.socialcenter;

import android.app.Activity;
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

import com.example.besocial.R;
import com.example.besocial.ui.mainactivity.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class AddNewRedeemableBonusFragment extends Fragment implements View.OnClickListener {

    private ImageView newBenefitPhoto;
    private Button clearFields,saveNewBenefit;
    private String name,description,costString,category;
    private int cost;
    private Spinner listOfCategories;
    private final static int galleryPick=1;
    private EditText newBenefitName,newBenefitDescription,newBenefitCost;
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
        ArrayAdapter<CharSequence> arrayAdapter= ArrayAdapter.createFromResource(getContext(),R.array.list_of_bonus_area_categories,R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        listOfCategories.setAdapter(arrayAdapter);
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
        int cost=0,categoryPosition=0;
        categoryPosition=listOfCategories.getSelectedItemPosition();
        if(costString.equals(""))
            name="";
        else cost=Integer.parseInt(costString);
        if(categoryPosition==0||name.equals("")||description.equals("")||cost<1 ){
            isOk = false;
            Toast.makeText(getActivity(), "Incorrect Fields", Toast.LENGTH_SHORT).show();
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
            name=newBenefitName.getText().toString();
            description=newBenefitDescription.getText().toString();
            costString=newBenefitCost.getText().toString();
            category=listOfCategories.getSelectedItem().toString();
            if(checkFields(name,description,costString)==true){

            }
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
            Uri imageUri = data.getData();
//            final StorageReference imageName=userPicturesRef.child("image "+imageUri.getLastPathSegment() );
//            imageName.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Toast.makeText(getActivity(), "Profile picture changed", Toast.LENGTH_LONG).show();
//                    imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            DatabaseReference imageStore= FirebaseDatabase.getInstance().getReference().child("users").child(MainActivity.getCurrentUser().getUid()).child("profileImage");
//                            imageStore.setValue((String)uri.toString());
//                        }
//                    });
//                }
//            });
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
            newBenefitPhoto.setImageURI(imageUri);
        }
    }

}
