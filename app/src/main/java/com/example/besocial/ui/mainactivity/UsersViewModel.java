package com.example.besocial.ui.mainactivity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.besocial.data.User;

public class UsersViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private static MutableLiveData<User> user = new MutableLiveData<>();


    public MutableLiveData<User> getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user.setValue(user);
    }

}
