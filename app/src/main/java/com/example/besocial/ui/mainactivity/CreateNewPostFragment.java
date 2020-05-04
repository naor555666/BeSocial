package com.example.besocial.ui.mainactivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.besocial.R;
import com.example.besocial.data.Post;
import com.example.besocial.data.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateNewPostFragment extends Fragment {
    private User loggedUser=MainActivity.getLoggedUser();
    private TextView newPostUsername,newPostDate;
    private static CreateNewPostFragment createNewPostFragment=new CreateNewPostFragment();
    private Spinner categories;
    private Button postButton;
    private Calendar calendar;
    private Post newPost;
    private String currentDate,currentTime;
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
        newPostDate=view.findViewById(R.id.new_post_date);
        newPostUsername=view.findViewById(R.id.new_post_username);
        categories=view.findViewById(R.id.new_post_category_list);
        ArrayAdapter<CharSequence> arrayAdapter= ArrayAdapter.createFromResource(getContext(),R.array.list_of_categories,R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categories.setAdapter(arrayAdapter);
        postButton=view.findViewById(R.id.post_new_post_button);
        calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat simpleTimeFormat=new SimpleDateFormat("hh:mm");

        currentDate= simpleDateFormat.format(calendar.getTime());
        currentTime=simpleTimeFormat.format(calendar.getTime());
        newPostUsername.setText(loggedUser.getUserFirstName()+" "+loggedUser.getUserLastName());
        newPostDate.setText(currentTime+"  "+currentDate);
        /**
         * on click method for posting new post
         */
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPost=new Post(loggedUser.getUserId(),getResources().getDrawable(R.drawable.empty_profile_image),loggedUser.getUserFirstName()+" "+loggedUser.getUserLastName(),currentTime+"  "+currentDate,"MY first post creation","General",null);
                HomeFragment.addPost(newPost);
                Toast.makeText(getActivity(), "post created successfuly", Toast.LENGTH_LONG).show();

            }
        });

    }
}
