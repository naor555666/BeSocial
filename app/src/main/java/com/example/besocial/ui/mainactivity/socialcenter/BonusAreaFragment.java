package com.example.besocial.ui.mainactivity.socialcenter;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.besocial.R;
import com.example.besocial.data.RedeemableBenefit;
import com.example.besocial.data.User;
import com.example.besocial.databinding.FragmentBonusAreaBinding;
import com.example.besocial.ui.mainactivity.MainActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;


/**
 * A simple {@link Fragment} subclass.
 */
public class BonusAreaFragment extends Fragment {
    private FragmentBonusAreaBinding binding;
    private Spinner listOfCategories;
    private User loggedUser;
    private TextView socialLevel,socialPoints,socialCredits;
    private ImageButton addNewRedeemableBonus;
    private NavController navController;
    private DatabaseReference benefitsRef;
    private SocialCenterViewModel socialCenterViewModel;

    public BonusAreaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBonusAreaBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
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


    private void displayEventsList() {
        FirebaseRecyclerOptions<RedeemableBenefit> options = new FirebaseRecyclerOptions
                .Builder<RedeemableBenefit>()
                .setQuery(benefitsRef, RedeemableBenefit.class)
                .build();
        FirebaseRecyclerAdapter<RedeemableBenefit, BenefitsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<RedeemableBenefit, BenefitsViewHolder>(options) {
            @NonNull
            @Override
            public BenefitsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_node, parent, false);
                BenefitsViewHolder viewHolder = new BenefitsViewHolder(view);
                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull BenefitsViewHolder holder, int position, @NonNull final RedeemableBenefit model) {
                holder.benefitNode = model;
                //Glide.with(getContext()).load(model).placeholder(R.drawable.social_event0).into(holder.eventPhoto);
                holder.benefitName.setText(model.getName());
                holder.benefitDescription.setText(model.getDescription());
                holder.benefitCategory.setText(model.getCategory());
                holder.benefitCost.setText(model.getCost().toString());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        socialCenterViewModel.setBenefit(model);
                        MainActivity.getNavController().navigate(R.id.action_nav_bonus_area_to_benefitFragment);
                    }
                });
            }
        };
        binding.bonusAreaBenefitsRecycler.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static class BenefitsViewHolder extends RecyclerView.ViewHolder {
        RedeemableBenefit benefitNode;
        //ImageView benefitPhoto;
        TextView benefitCost, benefitName, benefitDescription,benefitCategory;
        public BenefitsViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        socialCenterViewModel = ViewModelProviders.of(getActivity()).get(SocialCenterViewModel.class);

    }

}
