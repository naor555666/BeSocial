package com.example.besocial.ui.chatactivity;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.besocial.R;
import com.example.besocial.data.ChatConversation;
import com.example.besocial.data.ChatMessage;
import com.example.besocial.databinding.FragmentChatConversationBinding;
import com.example.besocial.ui.mainactivity.MainActivity;
import com.example.besocial.utils.ConstantValues;
import com.example.besocial.utils.DateUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private String chosenUid;
    private DatabaseReference dbRef;
    private String userName;
    private String photoUrl;
    private ChatConversation chosenChatConversation;
    private ChatViewModel chatViewModel;
    private FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder> firebaseRecyclerAdapter;
    private boolean isConversationExists;
    String conversationId;
    public ChatConversationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatConversationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        binding.chatConversationRecyclerview.setLayoutManager(linearLayoutManager);
        setListeners();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chatViewModel = ViewModelProviders.of(getActivity()).get(ChatViewModel.class);
        getConversationDetails();
    }

    private void getConversationDetails() {
        Bundle bundle;
        bundle = getArguments();

        //conversation was chosen from main activity
        if (bundle != null) {
            chosenUid = bundle.getString("chosenUid", null);

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
                    checkIfConversationExists();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //conversation was chosen from conversations list, the view model is initialized
        } else {
            chosenChatConversation = chatViewModel.getChosenChatConversation().getValue();
            conversationId= chosenChatConversation.getChatId();
            userName = chosenChatConversation.getReceiver();
            photoUrl = chosenChatConversation.getReceiverProfilePicture();
            initToolBar();
            prepareDatabaseQuery();
        }
    }

    private void checkIfConversationExists() {
        final DatabaseReference userConversationRef = FirebaseDatabase.getInstance().getReference()
                .child(ConstantValues.CHAT_CONVERSATIONS).child(MainActivity.getLoggedUser().getUserId());
        userConversationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(chosenUid)) {
                    isConversationExists=false;
                } else {
                    isConversationExists=true;
                    conversationId = (String) dataSnapshot.child(chosenUid).child(ConstantValues.CHAT_ID).getValue();
                    prepareDatabaseQuery();
                }
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
        if (chosenChatConversation == null) {
/*            final DatabaseReference userConversationRef = FirebaseDatabase.getInstance().getReference()
                    .child(ConstantValues.CHAT_CONVERSATIONS).child(MainActivity.getLoggedUser().getUserId());
            userConversationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(chosenUid)) {
                        createNewConversationInDB(childUpdates, textMessage);
                    } else {
                        String conversationId = (String) dataSnapshot.child(chosenUid).child(ConstantValues.CHAT_ID).getValue();
                        addMessage(textMessage, conversationId);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/

            if(!isConversationExists){
                createNewConversationInDB(childUpdates, textMessage);
            }else {
                addMessage(textMessage, conversationId);
            }
        } else {
            addMessage(textMessage, chosenChatConversation.getChatId());
        }
    }

    private void addMessage(String textMessage, String conversationId) {
        ChatMessage chatMessage = new ChatMessage(textMessage,
                MainActivity.getLoggedUser().getUserId(),
                chosenUid,
                DateUtils.getCurrentTimeString());
        DatabaseReference messageRef = dbRef.child(ConstantValues.CHAT_MESSAGES).child(conversationId).push();

        messageRef.setValue(chatMessage);
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
        chatViewModel.setChosenChatConversation(chatConversation);

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
                dbRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        prepareDatabaseQuery();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void prepareDatabaseQuery() {
        final Query chatMessagesRef = FirebaseDatabase.getInstance().getReference()
                .child(ConstantValues.CHAT_MESSAGES).child(conversationId);

        chatMessagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    Toast.makeText(getContext(), "No messages to display.", Toast.LENGTH_LONG).show();
                    if (firebaseRecyclerAdapter != null) {
                        firebaseRecyclerAdapter.stopListening();
                    }
                } else {
                    displayMessagesList(binding.chatConversationRecyclerview, chatMessagesRef);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void displayMessagesList(final RecyclerView chatMessagesRecyclerView, Query chatMessagesRef) {
        FirebaseRecyclerOptions<ChatMessage> options = new FirebaseRecyclerOptions
                .Builder<ChatMessage>()
                .setQuery(chatMessagesRef, ChatMessage.class)
                .build();
        firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder>(options) {
            @NonNull
            @Override
            public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_node, parent, false);
                ChatMessageViewHolder viewHolder = new ChatMessageViewHolder(view);
                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position, @NonNull final ChatMessage model) {

                holder.messageDate.setText(model.getTimeStamp());
                holder.textMessage.setText(model.getTextMessage());
                Drawable background;
                if (model.getSenderId().equals(MainActivity.getLoggedUser().getUserId())) {
                    background=getResources().getDrawable(R.drawable.message_form_blue) ;
                    holder.textMessage.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                } else {
                    background=getResources().getDrawable(R.drawable.message_form_grey) ;
                    holder.textMessage.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                }
                holder.textMessage.setBackground(background);
            }

        };
        chatMessagesRecyclerView.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();
    }

    public static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageDate, textMessage;

        public ChatMessageViewHolder(@NonNull final View itemView) {
            super(itemView);
            messageDate = itemView.findViewById(R.id.message_date);
            textMessage = itemView.findViewById(R.id.textMessage);
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(firebaseRecyclerAdapter!=null)
            firebaseRecyclerAdapter.stopListening();
        binding = null;
    }
}
