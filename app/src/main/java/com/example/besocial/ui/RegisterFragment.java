package com.example.besocial.ui;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.besocial.MainActivity;
import com.example.besocial.R;
import com.example.besocial.ui.login.LoginActivity;
import com.example.besocial.ui.login.LoginFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import static android.graphics.Color.red;

public class RegisterFragment extends Fragment {
    private static final RegisterFragment registerFragment = new RegisterFragment();

    public static RegisterFragment getInstance() {
        return registerFragment;
    }

    private RegisterFragment() {

    }

    private EditText firstName, lastName, email, confirmEmail, password, confirmPassword;
    private Button clearFields, createAccount;
    private FirebaseDatabase database=FirebaseDatabase.getInstance();
    private RegisterTextWatcher registerTextWatcher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        email = view.findViewById(R.id.username);
        confirmEmail = view.findViewById(R.id.confirmUsername);
        password = view.findViewById(R.id.password);
        confirmPassword = view.findViewById(R.id.confirmPassword);
        clearFields = view.findViewById(R.id.clearFieldsRegister);
        createAccount = view.findViewById(R.id.createAccount);
        setListeners();

    }


    private class ClickListener implements View.OnClickListener {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.clearFieldsRegister:
                    clearFields();
                    break;
                case R.id.createAccount:
                    if (checkFields()) {
                        Toast.makeText(getActivity(), "Registered successfully", Toast.LENGTH_SHORT).show();
                    } else

                    break;
            }
        }
    }

    private boolean checkFields() {
        Toast.makeText(getActivity(), "chekc fielda", Toast.LENGTH_SHORT);

        Boolean isFieldsValid = false;
        String firstNameString = firstName.getText().toString();
        String lastNameString = lastName.getText().toString();
        String emailString = email.getText().toString();
        String confirmEmailString = confirmEmail.getText().toString();
        String passwordString = password.getText().toString();
        String confirmPasswordString = confirmPassword.getText().toString();
/*
        if (firstNameString.equals("")) {
            Toast.makeText(getActivity(), "First name is empty", Toast.LENGTH_SHORT).show();
            firstName.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
        } else if (lastNameString.equals("")) {
            Toast.makeText(getActivity(), "Last name is empty", Toast.LENGTH_SHORT).show();
            lastName.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
        } else if (emailString.equals("")) {
            Toast.makeText(getActivity(), "Email is empty", Toast.LENGTH_SHORT).show();
            email.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
        } else if (confirmEmailString.equals("")) {
            Toast.makeText(getActivity(), "Email confirmation is empty", Toast.LENGTH_SHORT).show();
            confirmEmail.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
        } else if (passwordString.equals("")) {
            Toast.makeText(getActivity(), "Password is empty", Toast.LENGTH_SHORT).show();
            password.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
        } else if (confirmPasswordString.equals("")) {
            Toast.makeText(getActivity(), "Password confirmation is empty", Toast.LENGTH_SHORT).show();
            confirmPassword.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
        } else if (!emailString.equals(confirmEmailString)) {
            Toast.makeText(getActivity(), "Email confirmation is incompatible", Toast.LENGTH_SHORT).show();
            confirmEmail.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
        } else if (!passwordString.equals(confirmPasswordString)) {
            Toast.makeText(getActivity(), "Password confirmation is incompatible", Toast.LENGTH_SHORT).show();
            confirmPassword.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
        } else
            isFieldsValid = true;


 */
        isFieldsValid=true;
        if (firstNameString.equals("")) {
            firstName.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
            isFieldsValid=false;
        }
        if (lastNameString.equals("")) {
            lastName.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
            isFieldsValid=false;
        }
        if (emailString.equals("")) {
            email.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
            isFieldsValid=false;
        }
        if (confirmEmailString.equals("")) {
            confirmEmail.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
            isFieldsValid=false;
        }
        if (passwordString.length()<8) {
            password.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
            isFieldsValid=false;
        }
        if (confirmPasswordString.length()<8) {
            confirmPassword.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
            isFieldsValid=false;
        }
        if (!emailString.equals(confirmEmailString)) {
            confirmEmail.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
            isFieldsValid=false;
        }
        if (!passwordString.equals(confirmPasswordString)) {
            confirmPassword.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
            isFieldsValid=false;
        }
        if(isFieldsValid==false)
            Toast.makeText(getActivity(), "There are incorrect fields", Toast.LENGTH_SHORT).show();


        return isFieldsValid;
    }

    private void clearFields() {
        firstName.setText("");
        lastName.setText("");
        email.setText("");
        confirmEmail.setText("");
        password.setText("");
        confirmPassword.setText("");
    }
    public class RegisterTextWatcher implements android.text.TextWatcher{
        private int chosenEditText;
        public RegisterTextWatcher(int chosenEditText){
            super();
            this.chosenEditText=chosenEditText;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (chosenEditText == firstName.getId()) {
                if(firstName.getText().length()>0){
                    firstName.setBackground(getResources().getDrawable(R.drawable.input_field));
                }

            } else if (chosenEditText == lastName.getId()) {
                if(lastName.getText().length()>0){
                    lastName.setBackground(getResources().getDrawable(R.drawable.input_field));
                }

            } else if (chosenEditText == email.getId()) {
                if(email.getText().length()>0){
                    email.setBackground(getResources().getDrawable(R.drawable.input_field));
                }

            } else if (chosenEditText == confirmEmail.getId()) {
                if((confirmEmail.getText().toString().equals(email.getText().toString()))&&(confirmEmail.getText().length()>0) ) {
                    confirmEmail.setBackground(getResources().getDrawable(R.drawable.input_field));
                }
            } else if (chosenEditText == password.getId()) {

                //if (password.getBackground().equals(getResources().getDrawable(R.drawable.incorrect_input_register))) {
                if(password.getText().length()>7){
                    password.setBackground(getResources().getDrawable(R.drawable.input_field));
                }
            } else if (chosenEditText == confirmPassword.getId()) {
                if((confirmPassword.getText().length()>7 )&&(confirmPassword.getText().toString().equals(password.getText().toString())) ){
                    confirmPassword.setBackground(getResources().getDrawable(R.drawable.input_field));
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private void setListeners() {
        View.OnClickListener clickListener = new ClickListener();
        clearFields.setOnClickListener(clickListener);
        createAccount.setOnClickListener(clickListener);
        firstName.addTextChangedListener(new RegisterTextWatcher(firstName.getId()));
        lastName.addTextChangedListener(new RegisterTextWatcher(lastName.getId()));
        email.addTextChangedListener(new RegisterTextWatcher(email.getId()));
        confirmEmail.addTextChangedListener(new RegisterTextWatcher(confirmEmail.getId()));
        password.addTextChangedListener(new RegisterTextWatcher(password.getId()));
        confirmPassword.addTextChangedListener(new RegisterTextWatcher(confirmPassword.getId()));


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LoginFragment loginFragment = LoginFragment.getInstance();
        loginFragment.setRegisterValue(true);
    }
}