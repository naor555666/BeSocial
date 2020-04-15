package com.example.besocial.ui.mainactivity.socialcenter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.besocial.databinding.FragmentBenefitBinding;


public class BenefitFragment extends Fragment {
    private FragmentBenefitBinding binding;
    public BenefitFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBenefitBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }
}
