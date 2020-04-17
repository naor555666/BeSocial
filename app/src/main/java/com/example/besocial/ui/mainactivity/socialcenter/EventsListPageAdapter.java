package com.example.besocial.ui.mainactivity.socialcenter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class EventsListPageAdapter extends FragmentStatePagerAdapter {
    private int  numOfTabs;
    public EventsListPageAdapter(@NonNull FragmentManager fm,int numOfTabs ) {
        super(fm);
        this.numOfTabs=numOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new AllEventsTabFragment();
            case 1:
                return new HostingEventsTabFragment();
            case 2:
                return new AttendingEventsTabFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }


}
