package com.example.besocial.ui.mainactivity.socialcenter;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.besocial.ConstantValues;
import com.example.besocial.R;
import com.example.besocial.data.RedeemableBenefit;
import com.example.besocial.data.User;
import com.example.besocial.databinding.FragmentBenefitBinding;
import com.example.besocial.ui.mainactivity.MainActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class BenefitFragment extends Fragment implements View.OnClickListener {
    private SocialCenterViewModel socialCenterViewModel;
    private FragmentBenefitBinding binding;
    private TextView benefitName, benefitDescription, benefitCost;
    private ImageView benefitPhoto;
    private MutableLiveData<RedeemableBenefit> redeemableBenefitMutableLiveData;
    private RedeemableBenefit selectedBenefit;
    private ImageButton redeemBenefit;
    private User user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;

    public BenefitFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        socialCenterViewModel = ViewModelProviders.of(getActivity()).get(SocialCenterViewModel.class);
        binding = FragmentBenefitBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        user = MainActivity.getLoggedUser();
        redeemBenefit = binding.redeemBenefitButton;
        benefitName = binding.benefitBenefitName;
        benefitPhoto = binding.benefitBenefitPhoto;
        benefitCost = binding.benefitBenefitCost;
        benefitDescription = binding.benefitBenefitDescription;
        redeemableBenefitMutableLiveData = socialCenterViewModel.getBenefit();
        firebaseDatabase = FirebaseDatabase.getInstance();
        selectedBenefit = redeemableBenefitMutableLiveData.getValue();
        benefitName.setText(selectedBenefit.getName());
        benefitDescription.setText(selectedBenefit.getDescription());
        benefitCost.setText("Cost: " + selectedBenefit.getCost() + " CREDITS");
        Glide.with(getContext()).load(selectedBenefit.getBenefitPhoto()).placeholder(R.drawable.social_event0).into(benefitPhoto);

        setListeners();
        return view;
    }

    void setListeners() {
        redeemBenefit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == redeemBenefit.getId()) {
            Long benefitCost, userCredits, creditsLeft;
            benefitCost = selectedBenefit.getCost();
            userCredits= user.getSocialStoreCredits();
            if (userCredits.longValue() < benefitCost.longValue()) {
                Toast.makeText(getContext(), "You don't have enough CREDITS", Toast.LENGTH_SHORT).show();
            } else {
                userRef = FirebaseDatabase.getInstance().getReference()
                        .child(ConstantValues.USERS)
                        .child(user.getUserId())
                        .child("socialStoreCredits");

                creditsLeft = userCredits - benefitCost;
                userRef.setValue(creditsLeft);
                Toast.makeText(getContext(), "You have purchased this benefit", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
