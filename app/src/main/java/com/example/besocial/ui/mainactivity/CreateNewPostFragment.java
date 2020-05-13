package com.example.besocial.ui.mainactivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.besocial.R;
import com.example.besocial.data.Post;
import com.example.besocial.data.User;
import com.example.besocial.utils.BitmapUtils;
import com.example.besocial.utils.ConstantValues;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateNewPostFragment extends Fragment implements View.OnClickListener {
    private User loggedUser=MainActivity.getLoggedUser();
    private TextView newPostUsername,newPostDate;
    private final static int galleryPick = 1;
    private ImageView postPhoto;
    private static CreateNewPostFragment createNewPostFragment=new CreateNewPostFragment();
    private Spinner categories;
    private StorageReference userPicturesRef;
    private Button postButton,uploadPhotoButton;
    private Calendar calendar;
    private CircleImageView newPostUserProfilePicture;
    private Post newPost;
    private ProgressDialog progressDialog;
    private EditText postDescription;
    private String currentDate,currentTime,uploadedImageUri;
    public static CreateNewPostFragment getInstance() {
        return createNewPostFragment;
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_new_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userPicturesRef = FirebaseStorage.getInstance().getReference().child(MainActivity.getCurrentUser().getUid());
        newPostDate=view.findViewById(R.id.new_post_date);
        newPostUsername=view.findViewById(R.id.new_post_username);
        newPostUserProfilePicture=view.findViewById(R.id.new_post_user_image);
        categories=view.findViewById(R.id.new_post_category_list);
        uploadPhotoButton=view.findViewById(R.id.new_post_upload_photo_button);
        postPhoto=view.findViewById(R.id.new_post_photo);
        postDescription=view.findViewById(R.id.new_post_description);
        ArrayAdapter<CharSequence> arrayAdapter= ArrayAdapter.createFromResource(getContext(),R.array.list_of_categories,R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categories.setAdapter(arrayAdapter);
        categories.setEnabled(false);
        postButton=view.findViewById(R.id.post_new_post_button);
        calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat simpleTimeFormat=new SimpleDateFormat("hh:mm");
        uploadedImageUri=null;
        Glide.with(getContext()).load(loggedUser.getProfileImage()).placeholder(R.drawable.social_event0).into(newPostUserProfilePicture);
        currentDate= simpleDateFormat.format(calendar.getTime());
        currentTime=simpleTimeFormat.format(calendar.getTime());
        newPostUsername.setText(loggedUser.getUserFirstName()+" "+loggedUser.getUserLastName());
        newPostDate.setText(currentTime+"  "+currentDate);
        /**
         * on click method for posting new post
         */

        setListeners();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==postButton.getId()){
            String postCategory;
            postCategory=categories.getSelectedItem().toString();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setTitle("Loading... Please wait");
            progressDialog.show();
            newPost=new Post(loggedUser.getUserId(),loggedUser.getProfileImage(),loggedUser.getUserFirstName()+" "+loggedUser.getUserLastName(),currentTime+"  "+currentDate,postDescription.getText().toString(),postCategory,null);
            //HomeFragment.addPost(newPost);
            post();
            //Toast.makeText(getActivity(), "post created successfuly", Toast.LENGTH_LONG).show();
        }
        else if(v.getId()==uploadPhotoButton.getId()){
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, galleryPick);
        }
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
        final StorageReference postImagesRef = userPicturesRef.child("postImages" + imageUri.getLastPathSegment());
        BitmapUtils rotateNcompress = new BitmapUtils();
        byte[] compressedPhoto = new byte[0];
        try {
            compressedPhoto = rotateNcompress.compressAndRotateBitmap(getContext(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        postImagesRef.putBytes(compressedPhoto).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                postImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                       // DatabaseReference imageStore = FirebaseDatabase.getInstance().getReference().child("users")
                               // .child(MainActivity.getLoggedUser().getUserId()).child("postImages").push();
                        //imageStore.setValue(uri.toString());
                        //Toast.makeText(getContext(), "Image was uploaded successfully.", Toast.LENGTH_SHORT).show();
                        uploadedImageUri=uri.toString();
                        Glide.with(getContext()).load(uri).placeholder(R.drawable.empty_profile_image).into(postPhoto);
                    }
                });
            }
        });
    }

    void setListeners(){
        postButton.setOnClickListener(this);
        uploadPhotoButton.setOnClickListener(this);
    }

    void post(){
        DatabaseReference postsRef;
        if(uploadedImageUri!=null){
            HashMap<String,String> userImage= new HashMap<>();
            userImage.put("userId",loggedUser.getUserId());
            userImage.put("uploadedImage",uploadedImageUri);
            newPost.setPostImage(uploadedImageUri);
            postsRef = FirebaseDatabase.getInstance().getReference().child(ConstantValues.USER_PHOTOS).push();
            postsRef.setValue(userImage);
        }
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts").push();
        postsRef.setValue(newPost).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
            }
        });
        MainActivity.getNavController().popBackStack();
    }
}
