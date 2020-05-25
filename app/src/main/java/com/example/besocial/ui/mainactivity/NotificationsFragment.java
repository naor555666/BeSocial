package com.example.besocial.ui.mainactivity;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.besocial.R;
import com.example.besocial.data.Notification;
import com.example.besocial.data.Post;
import com.example.besocial.data.User;
import com.example.besocial.utils.ConstantValues;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment {
    String TAG= "NotificationsFragment";
    private static DatabaseReference notificationsRef;
    private static RecyclerView notificationsRecyclerView;
    private FirebaseRecyclerAdapter<Notification, NotificationsFragment.NotificationsViewHolder> firebaseRecyclerAdapter;


    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        notificationsRecyclerView = view.findViewById(R.id.notifications_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        notificationsRecyclerView.setLayoutManager(layoutManager);
        displayNotifications();

    }

    void displayNotifications(){
        notificationsRef = FirebaseDatabase.getInstance().getReference().child(ConstantValues.NOTIFICATIONS).child(MainActivity.getLoggedUser().getUserId());
        notificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    Toast.makeText(getContext(), "No notifications to display", Toast.LENGTH_SHORT).show();
                } else {
                    showNotifications();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void showNotifications(){
        FirebaseRecyclerOptions<Notification> options = new FirebaseRecyclerOptions
                .Builder<Notification>()
                .setQuery(notificationsRef, Notification.class)
                .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Notification, NotificationsFragment.NotificationsViewHolder>(options) {
            @NonNull
            @Override
            public NotificationsFragment.NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_in_recycler, parent, false);
                NotificationsFragment.NotificationsViewHolder viewHolder = new NotificationsFragment.NotificationsViewHolder(view);
                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull final NotificationsFragment.NotificationsViewHolder holder, int position, @NonNull final Notification model) {
                String modelType=model.getType();
                if(modelType.equals(ConstantValues.EVENT)) {
                    holder.notificationBody.setText(ConstantValues.EVENT_NOTIFICATION_BODY1+model.getRelatedName()
                            +ConstantValues.EVENT_NOTIFICATION_BODY2+model.getSocialPointsAmount());
                    Glide.with(getContext()).load(R.drawable.social_event0).into(holder.notificationPicture);
                }
                else if(modelType.equals(ConstantValues.NEW_CONVERSTION)) {
                    holder.notificationBody.setText(ConstantValues.NEW_CONVERSATION_NOTIFICATION_BODY1+model.getRelatedName()
                            +ConstantValues.NEW_CONVERSATION_NOTIFICATION_BODY2+model.getSocialPointsAmount());
                    Glide.with(getContext()).load(R.drawable.new_conversation_image).into(holder.notificationPicture);
                }
                else if(modelType.equals(ConstantValues.LIKES)){
                    holder.notificationBody.setText(ConstantValues.LIKE_NOTIFICATION_BODY);
                    Glide.with(getContext()).load(R.drawable.full_like_button).into(holder.notificationPicture);
                }
                else if(modelType.equals(ConstantValues.NEW_RANK)){
                    holder.notificationBody.setText(ConstantValues.NEW_RANK_BODY+"\""+model.getRelatedName()+"\"");
                    Glide.with(getContext()).load(R.drawable.good_job).into(holder.notificationPicture);
                }
                holder.notificationHeadline.setText(modelType);
            }
        };
        RecyclerView newNotificationsRecyclerView = NotificationsFragment.notificationsRecyclerView;
        newNotificationsRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }



    public static class NotificationsViewHolder extends RecyclerView.ViewHolder {
        Notification postNode;
        TextView notificationHeadline,notificationBody;
        CircleImageView notificationPicture;

        public NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationBody = itemView.findViewById(R.id.notification_in_recycler_body);
            notificationHeadline = itemView.findViewById(R.id.notification_in_recycler_headline);
            notificationPicture = itemView.findViewById(R.id.notification_in_recycler_image);
        }
    }

}
