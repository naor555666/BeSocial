package com.example.besocial.ui.mainactivity;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.besocial.utils.ConstantValues;
import com.example.besocial.R;
import com.example.besocial.data.ChatConversation;
import com.example.besocial.data.ChatMessage;
import com.example.besocial.data.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;



/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {

    private static UsersViewModel mViewModel;
    private RecyclerView chatListRecycler;
    static FirebaseRecyclerAdapter<ChatConversation, ChatListFragment.ChatConversationViewHolder> firebaseRecyclerAdapter;
    private Query query;
    private User loggedUser;
    private FirebaseDatabase firebaseDatabase;
    private static DatabaseReference chatRef;
    private StorageReference userPicturesRef;
    private NavController navController;
    private String loggedUserId;
    private static String TAG;

    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TAG="ChatListFragment";
        loggedUser=MainActivity.getLoggedUser();
        loggedUserId=loggedUser.getUserId();
        chatListRecycler=view.findViewById(R.id.chat_list_recycler);
        firebaseDatabase = FirebaseDatabase.getInstance();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        chatListRecycler.setLayoutManager(linearLayoutManager);
        query = FirebaseDatabase.getInstance().getReference().child(ConstantValues.CHATS)
                .startAt(loggedUserId).endAt(loggedUserId + "\uf8ff");
        setChatList();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);
    }


    public void setChatList() {
        FirebaseRecyclerOptions<ChatConversation> options = new FirebaseRecyclerOptions
                .Builder<ChatConversation>()
                .setQuery(query, ChatConversation.class)
                .build();

        firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<ChatConversation, ChatConversationViewHolder>(options) {
            @NonNull
            @Override
            public ChatConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Log.d("SearchUsersFragment","On Create view holder");
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_in_recycler, parent, false);
                ChatConversationViewHolder viewHolder = new ChatConversationViewHolder(view);
                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull ChatConversationViewHolder holder, int position, @NonNull final ChatConversation model) {
                //holder.userNode = model;
                Log.d("SearchUsersFragment","On bind");
                Toast.makeText(getActivity(), model.getChatId(), Toast.LENGTH_SHORT).show();

                if(model==null){
                    //Toast.makeText(getActivity(),"nothing to show", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Toast.makeText(getActivity(), model.toString(), Toast.LENGTH_SHORT).show();
                }
                Glide.with(getActivity()).load(model.getSentToProfilePicture()).placeholder(R.drawable.empty_profile_image).into(holder.userPhoto);
                holder.userName.setText(model.getSentTo());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //mViewModel.setUser(model);
                        //MainActivity.getNavController().navigate(R.id.action_searchUsersFragment_to_nav_my_profile);
                    }
                });
            }
        };
        chatListRecycler.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();

    }

    public static class ChatConversationViewHolder extends RecyclerView.ViewHolder {
        ChatConversation chatConversation;
        ImageView userPhoto;
        TextView userName;

        public ChatConversationViewHolder(@NonNull final View itemView) {
            super(itemView);
            Log.d(TAG, "ChatConversationViewHolder: ");
            userPhoto = itemView.findViewById(R.id.chat_in_recycler_user_image);
            userName = itemView.findViewById(R.id.chat_in_recycler_user_name);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        firebaseRecyclerAdapter.stopListening();
    }


}
