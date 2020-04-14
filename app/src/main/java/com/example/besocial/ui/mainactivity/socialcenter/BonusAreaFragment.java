package com.example.besocial.ui.mainactivity.socialcenter;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.example.besocial.R;
import com.example.besocial.data.User;
import com.example.besocial.ui.mainactivity.MainActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class BonusAreaFragment extends Fragment {
    private Spinner listOfCategories;
    private User loggedUser;
    private TextView socialLevel,socialPoints,socialCredits;
    private ImageButton addNewRedeemableBonus;
    private NavController navController;
    public BonusAreaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bonus_area, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        socialCredits=view.findViewById(R.id.bonus_area_social_credits);
        socialLevel=view.findViewById(R.id.bonus_area_social_level);
        socialPoints=view.findViewById(R.id.bonus_area_social_points);
        listOfCategories=view.findViewById(R.id.bonus_area_categories_list);
        ArrayAdapter<CharSequence> arrayAdapter= ArrayAdapter.createFromResource(getContext(),R.array.list_of_bonus_area_categories,R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        listOfCategories.setAdapter(arrayAdapter);
        loggedUser= MainActivity.getLoggedUser();
        socialPoints.setText(loggedUser.getSocialPoints());
        socialLevel.setText(loggedUser.getSocialLevel());
        socialCredits.setText(loggedUser.getSocialStoreCredits());
        addNewRedeemableBonus=view.findViewById(R.id.new_redeemable_bonus_button);
        navController= MainActivity.getNavController();

        setListeners();
    }


    void setListeners(){
        listOfCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addNewRedeemableBonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_nav_bonus_area_to_addNewRedeemableBonusFragment);
            }
        });
    }
}
