package com.example.besocial.ui.mainactivity.socialcenter;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.besocial.R;
import com.example.besocial.data.RedeemableBenefit;
import com.example.besocial.databinding.FragmentBenefitBinding;


public class BenefitFragment extends Fragment {
    private SocialCenterViewModel socialCenterViewModel;
    private FragmentBenefitBinding binding;
    private TextView benefitName,benefitDescription,benefitCost;
    private ImageView benefitPhoto;
    private MutableLiveData<RedeemableBenefit> redeemableBenefitMutableLiveData;
    private RedeemableBenefit selectedBenefit;
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

        benefitName=binding.benefitBenefitName;
        benefitPhoto=binding.benefitBenefitPhoto;
        benefitCost=binding.benefitBenefitCost;
        benefitDescription=binding.benefitBenefitDescription;
        redeemableBenefitMutableLiveData=socialCenterViewModel.getBenefit();
        selectedBenefit=redeemableBenefitMutableLiveData.getValue();
        benefitName.setText(selectedBenefit.getName());
        benefitDescription.setText(selectedBenefit.getDescription());
        benefitCost.setText("Cost: "+selectedBenefit.getCost()+" CREDITS");
        Glide.with(getContext()).load(selectedBenefit.getBenefitPhoto()).placeholder(R.drawable.social_event0).into(benefitPhoto);

        return view;
    }

}
