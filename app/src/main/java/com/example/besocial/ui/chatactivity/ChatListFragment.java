package com.example.besocial.ui.chatactivity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.example.besocial.R;
import com.example.besocial.data.User;
import com.example.besocial.databinding.FragmentChatListBinding;
import com.example.besocial.ui.mainactivity.UsersViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {
    private static String TAG = "ChatListFragment";

    private FragmentChatListBinding binding;
    private static UsersViewModel mViewModel;
    private RecyclerView chatListRecycler;
    private Query query;
    private User loggedUser;
    private FirebaseDatabase firebaseDatabase;
    private static DatabaseReference chatRef;
    private StorageReference userPicturesRef;
    private NavController navController;
    private String loggedUserId;

    private Intent callingIntent;

    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        binding = FragmentChatListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
        //handle navigation components
        navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(binding.chatToolbar, navController, appBarConfiguration);

        callingIntent = ChatActivity.callingIntent;
        ChatActivity.callingIntent = null;
        if (callingIntent != null && callingIntent.getStringExtra("chosenUid") != null) {
            Bundle bundle = callingIntent.getExtras();
/*            loggedUserId = intent.getStringExtra(ConstantValues.LOGGED_USER_ID);
            chosenId = intent.getStringExtra("chosenUid");*/
            showChatConversation(bundle);
        }
        //setChatList();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        mViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);
    }

    private void showChatConversation(Bundle bundle) {
        Navigation.findNavController(getView()).navigate(R.id.action_chatListFragment_to_chatConversationFragment, bundle);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        callingIntent = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }


}
