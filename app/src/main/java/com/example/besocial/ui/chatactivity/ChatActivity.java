package com.example.besocial.ui.chatactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.besocial.R;
import com.example.besocial.databinding.ActivityChatBinding;
import com.example.besocial.databinding.ContentChatActivityBinding;
import com.example.besocial.utils.ConstantValues;


public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private static NavController navController;
    private String loggedUserId, chosenId;
    public static Intent callingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        callingIntent=getIntent();

        navController = Navigation.findNavController(this, R.id.chat_nav_host_fragment);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
