package com.example.besocial.ui;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
public class RegisterFragment extends Fragment {
    private static final RegisterFragment registerFragment = new RegisterFragment();

    public static RegisterFragment getInstance() {
        return registerFragment;
    }

    private RegisterFragment() {

    }

    private EditText firstName, lastName, email, confirmEmail, password, confirmPassword;
    private Button clearFields, createAccount;


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

        if (firstNameString.equals("")) {
            Toast.makeText(getActivity(), "First name is empty", Toast.LENGTH_SHORT).show();
        } else if (lastNameString.equals("")) {
            Toast.makeText(getActivity(), "Last name is empty", Toast.LENGTH_SHORT).show();
        } else if (emailString.equals("")) {
            Toast.makeText(getActivity(), "Email is empty", Toast.LENGTH_SHORT).show();
        } else if (confirmEmailString.equals("")) {
            Toast.makeText(getActivity(), "Email confirmation is empty", Toast.LENGTH_SHORT).show();
        } else if (passwordString.equals("")) {
            Toast.makeText(getActivity(), "Password is empty", Toast.LENGTH_SHORT).show();
        } else if (confirmPasswordString.equals("")) {
            Toast.makeText(getActivity(), "Password confirmation is empty", Toast.LENGTH_SHORT).show();
        } else if (!emailString.equals(confirmEmailString)) {
            Toast.makeText(getActivity(), "Email confirmation is incompatible", Toast.LENGTH_SHORT).show();
        } else if (!passwordString.equals(confirmPasswordString)) {
            Toast.makeText(getActivity(), "Password confirmation is incompatible", Toast.LENGTH_SHORT).show();
        } else
            isFieldsValid = true;

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

    private void setListeners() {
        View.OnClickListener clickListener = new ClickListener();
        clearFields.setOnClickListener(clickListener);
        createAccount.setOnClickListener(clickListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LoginFragment loginFragment = LoginFragment.getInstance();
        loginFragment.setRegisterValue(true);
    }
}
