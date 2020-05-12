package com.example.besocial.ui.chatactivity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.besocial.data.ChatConversation;

public class ChatViewModel extends ViewModel {
    private MutableLiveData<ChatConversation> chosenChatConversation = new MutableLiveData<>();


    public LiveData<ChatConversation> getChosenChatConversation() {
        return this.chosenChatConversation;
    }

    public void setChosenChatConversation(ChatConversation chosenChatConversation) {
        this.chosenChatConversation.setValue(chosenChatConversation);
    }
}
