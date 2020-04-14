package com.example.besocial.ui.mainactivity.socialcenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.besocial.R;
import com.example.besocial.ui.mainactivity.MainActivity;


public class AddNewRedeemableBonusFragment extends Fragment {

    private ImageView newBenefitPhoto;
    private Button clearFields,saveNewBenefit;
    public AddNewRedeemableBonusFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_redeemable_bonus, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newBenefitPhoto=view.findViewById(R.id.new_benefit_photo);
        saveNewBenefit=view.findViewById(R.id.new_benefit_save_benefit);
        clearFields=view.findViewById(R.id.new_benefit_clear_fields);
    }
}
