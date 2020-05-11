package com.example.besocial.ui.chatactivity;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.besocial.R;
import com.example.besocial.data.ChatConversation;
import com.example.besocial.data.ChatMessage;
import com.example.besocial.databinding.FragmentChatConversationBinding;
import com.example.besocial.ui.mainactivity.MainActivity;
import com.example.besocial.utils.ConstantValues;
import com.example.besocial.utils.DateUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatConversationFragment extends Fragment {


    private static final String TAG = "ChatConversationFragment";
    private FragmentChatConversationBinding binding;
    private NavController navController;
    private Query chatConversationRef, chatMessagesRef;
    private String chosenUid;
    private DatabaseReference dbRef;
    private String userName;
    private String photoUrl;

    public ChatConversationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatConversationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Bundle bundle = getArguments();
        assert bundle != null;
        chosenUid = bundle.getString("chosenUid");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //handle navigation components
        navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(binding.chatToolbar, navController, appBarConfiguration);
        dbRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userPhotoRef = dbRef
                .child(ConstantValues.USERS)
                .child(chosenUid);
        userPhotoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                photoUrl = (String) dataSnapshot.child(ConstantValues.PROFILE_PHOTO).getValue();
                userName = String.format("%s %s",
                        dataSnapshot.child(ConstantValues.USER_FIRST_NAME).getValue().toString(), dataSnapshot.child(ConstantValues.USER_LAST_NAME).getValue().toString());
                initToolBar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        setListeners();
    }

    private void setListeners() {

        binding.chatUserInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.trim().equals("")) {
                    Log.d(TAG, "onTextChanged: empty");
                    binding.chatConversationSendBtn.setVisibility(View.INVISIBLE);
                } else {
                    Log.d(TAG, "onTextChanged: something");
                    binding.chatConversationSendBtn.setVisibility(View.VISIBLE);
                }
            }
        });
        binding.chatConversationSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        final String textMessage = binding.chatUserInput.getText().toString();
        binding.chatUserInput.setText("");
        final Map<String, Object> childUpdates = new HashMap<>();
        final DatabaseReference userConversationRef = FirebaseDatabase.getInstance().getReference()
                .child(ConstantValues.CHAT_CONVERSATIONS).child(MainActivity.getLoggedUser().getUserId());
        userConversationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(chosenUid)) {
                    createNewConversationInDB(childUpdates, textMessage);
                } else {
                    addMessage(textMessage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addMessage(String toString) {
    }

    private void createNewConversationInDB(final Map<String, Object> childUpdates, String textMessage) {
        dbRef = FirebaseDatabase.getInstance().getReference();
        final String conversationId = dbRef.child(ConstantValues.CHAT_MESSAGES).push().getKey();
        String newMessageId = dbRef.child(ConstantValues.CHAT_MESSAGES).child(conversationId).push().getKey();
        ChatMessage chatMessage = new ChatMessage(textMessage,
                MainActivity.getLoggedUser().getUserId(),
                chosenUid,
                DateUtils.getCurrentTimeString());

        final ChatConversation chatConversation = new ChatConversation(conversationId,
                MainActivity.getLoggedUser().getUserId(),
                chosenUid,
                MainActivity.getLoggedUser().getProfileImage(), false);

        childUpdates.put(String.format("/%s/%s/%s", ConstantValues.CHAT_MESSAGES, conversationId, newMessageId), chatMessage);
        childUpdates.put(String.format("/%s/%s/%s", ConstantValues.CHAT_CONVERSATIONS, MainActivity.getCurrentUser().getUid(), chosenUid), chatConversation);
        DatabaseReference userPhotoRef = dbRef
                .child(ConstantValues.USERS)
                .child(chosenUid)
                .child(ConstantValues.PROFILE_PHOTO);
        userPhotoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String photoUrl = (String) dataSnapshot.getValue();
                ChatConversation chatConversation = new ChatConversation(conversationId,
                        MainActivity.getLoggedUser().getUserId(),
                        chosenUid,
                        photoUrl, false);
                childUpdates.put(String.format("/%s/%s/%s",
                        ConstantValues.CHAT_CONVERSATIONS,
                        chosenUid,
                        MainActivity.getCurrentUser().getUid()), chatConversation);
                dbRef.updateChildren(childUpdates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initToolBar() {
        binding.chatConversationReceiverName.setText(userName);
        Glide.with(getActivity()).load(photoUrl).placeholder(R.drawable.empty_profile_image).into(binding.chatConversationReceiverPhoto);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
