package com.example.besocial.ui.chatactivity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.example.besocial.data.User;
import com.example.besocial.databinding.FragmentChatListBinding;
import com.example.besocial.ui.mainactivity.MainActivity;
import com.example.besocial.ui.mainactivity.UsersViewModel;
import com.example.besocial.utils.ConstantValues;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
    private ChatViewModel chatViewModel;
    private FirebaseRecyclerAdapter<ChatConversation, ChatConversationViewHolder> firebaseRecyclerAdapter;

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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.chatListRecycler.setLayoutManager(linearLayoutManager);

        callingIntent = ChatActivity.callingIntent;
        ChatActivity.callingIntent = null;
        if (callingIntent != null && callingIntent.getStringExtra("chosenUid") != null) {
            Bundle bundle = callingIntent.getExtras();
/*            loggedUserId = intent.getStringExtra(ConstantValues.LOGGED_USER_ID);
            chosenId = intent.getStringExtra("chosenUid");*/
            showChatConversation(bundle);
        }else prepareDatabaseQuery();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chatViewModel = ViewModelProviders.of(getActivity()).get(ChatViewModel.class);
    }

    private void showChatConversation(Bundle bundle) {
        Navigation.findNavController(getView()).navigate(R.id.action_chatListFragment_to_chatConversationFragment, bundle);
    }
    private void prepareDatabaseQuery() {
        final Query conversationsRef = FirebaseDatabase.getInstance().getReference()
                .child(ConstantValues.CHAT_CONVERSATIONS).child(MainActivity.getFireBaseAuth().getUid());
        conversationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    Toast.makeText(getContext(), "No chats to display yet...send some greetings to others!", Toast.LENGTH_LONG).show();
                    if (firebaseRecyclerAdapter != null) {
                        firebaseRecyclerAdapter.stopListening();
                    }
                } else {
                    displayConversationsList(binding.chatListRecycler, conversationsRef);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void displayConversationsList(final RecyclerView conversationsRecyclerView, Query conversationsRef) {
        FirebaseRecyclerOptions<ChatConversation> options = new FirebaseRecyclerOptions
                .Builder<ChatConversation>()
                .setQuery(conversationsRef, ChatConversation.class)
                .build();
        firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<ChatConversation, ChatConversationViewHolder>(options) {
            @NonNull
            @Override
            public ChatConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_in_recycler, parent, false);
                ChatConversationViewHolder viewHolder = new ChatConversationViewHolder(view);
                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull ChatConversationViewHolder holder, int position, @NonNull final ChatConversation model) {

                holder.chatConversation = model;
                Glide.with(getActivity()).load(model.getReceiverProfilePicture()).placeholder(R.drawable.empty_profile_image).into(holder.userPhoto);
                holder.userName.setText(String.format("%s", model.getUserName()));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chatViewModel.setChosenChatConversation(model);
                        Navigation.findNavController(getView()).navigate(R.id.action_chatListFragment_to_chatConversationFragment);
                    }
                });
            }

        };
        conversationsRecyclerView.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();
    }

    public static class ChatConversationViewHolder extends RecyclerView.ViewHolder {
        ChatConversation chatConversation;
        ImageView userPhoto;
        TextView userName;

        public ChatConversationViewHolder(@NonNull final View itemView) {
            super(itemView);
            userPhoto = itemView.findViewById(R.id.chat_in_recycler_user_image);
            userName = itemView.findViewById(R.id.chat_in_recycler_user_name);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }

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
}
