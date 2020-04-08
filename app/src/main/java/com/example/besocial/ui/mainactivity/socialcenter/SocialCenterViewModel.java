package com.example.besocial.ui.mainactivity.socialcenter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.besocial.data.Event;

public class SocialCenterViewModel extends ViewModel {
    private MutableLiveData<Event> event = new MutableLiveData<>();

    public LiveData<Event> getEvent() {
        return this.event;
    }

    public void setEvent(Event event) {
        this.event.setValue(event);
    }
}
