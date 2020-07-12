package com.example.besocial.ui.mainactivity.socialcenter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.besocial.data.Event;
import com.example.besocial.data.RedeemableBenefit;

public class SocialCenterViewModel extends ViewModel {
    private MutableLiveData<Event> event = new MutableLiveData<>();
    private MutableLiveData<RedeemableBenefit> benefit= new MutableLiveData<>();
    
    public LiveData<Event> getEvent() {
        return this.event;
    }

    public void setEvent(Event event) {
        this.event.setValue(event);
    }

    public MutableLiveData<RedeemableBenefit> getBenefit() {
        return benefit;
    }

    public void setBenefit(RedeemableBenefit redeemableBenefit) {
        this.benefit.setValue(redeemableBenefit);
    }

}
