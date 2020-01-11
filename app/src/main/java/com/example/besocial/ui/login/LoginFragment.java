package com.example.besocial.ui.login;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.besocial.MainActivity;
import com.example.besocial.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private EditText email, password;
    private Button register, login;
    private FirebaseAuth firebaseAuth;
    private CheckBox checkBox;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;
    private static  LoginFragment loginFragment;
    public static LoginFragment getInstance(){return loginFragment;}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        loginFragment= this;
        editor = sharedPref.edit();

        email = (EditText) view.findViewById(R.id.username);
        password = (EditText) view.findViewById(R.id.password);
        register = view.findViewById(R.id.register);
        login = view.findViewById(R.id.login);
        login.setEnabled(true);

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading... Please wait");

        setListeners();
        checkBox = (CheckBox) view.findViewById(R.id.remember_emailPassword_checkBox);
        firebaseAuth= FirebaseAuth.getInstance();

        boolean isCheckBoxChecked = false;
        isCheckBoxChecked = sharedPref.getBoolean("remember_emailPassword_checkBox", false);
        checkBox.setChecked(isCheckBoxChecked);


        if (isCheckBoxChecked) {
            String emailS = sharedPref.getString("email", null);
            String passwordS = sharedPref.getString("password", null);

            email.setText(emailS);
            password.setText(passwordS);
        }
    }

    private void setListeners() {
        View.OnClickListener clickListener = new ClickListener();
        login.setOnClickListener(clickListener);
        register.setOnClickListener(clickListener);
    }






    @Override
    public void onStop() {
        super.onStop();
        saveLoginPreference();
    }

    /**
     * save user email and password, checkbox - using shared preferances
     */

    private void saveLoginPreference() {
        editor.putBoolean("remember_emailPassword_checkBox", checkBox.isChecked());
        editor.putString("email", email.getText().toString());
        editor.putString("password", password.getText().toString());
        editor.commit();
    }

    private class ClickListener implements View.OnClickListener {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.register:
                    register.setEnabled(false);
                    //Toast.makeText(this, "register", Toast.LENGTH_SHORT).show();
                    getFragmentManager().beginTransaction().
                            replace(R.id.loginContainer,  RegisterFragment.getInstance()).//add on top of the static fragment
                            addToBackStack("BBB").//cause the back button scrolling through the loaded fragments
                            commit();
                    getFragmentManager().executePendingTransactions();
                    break;
                case R.id.login:
                    login.setEnabled(false);
                    loginUser();
                    break;
            }
        }
    }

    private void loginUser() {
        String usernameString = email.getText().toString();
        String passwordString = password.getText().toString();
        if (usernameString.isEmpty())
            Toast.makeText(getActivity(), "Username field is empty", Toast.LENGTH_SHORT).show();
        else if (passwordString.isEmpty())
            Toast.makeText(getActivity(), "Password field is empty", Toast.LENGTH_SHORT).show();
        else {
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(usernameString, passwordString)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendUserToMainActivity();
                                Toast.makeText(getActivity(), "Logged in successfuly", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                login.setEnabled(true);
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(getActivity(), "Could not log in: " + errorMessage, Toast.LENGTH_LONG).show();

                            }
                        }

                    });
        }


           //getActivity().finish();
    }
    private void sendUserToMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();

    }

    public void setRegisterValue(boolean registerValue) {
        this.register.setEnabled(registerValue);
    }
}
