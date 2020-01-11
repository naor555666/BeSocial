package com.example.besocial.ui.login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.besocial.R;

public class LoginActivity extends AppCompatActivity {
/*    private EditText email, password;
    private Button register, login;
    private FirebaseAuth firebaseAuth;
    private CheckBox checkBox;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;*/

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
        /*sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        editor = sharedPref.edit();
        email = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        checkBox=(CheckBox)findViewById(R.id.remember_emailPassword_checkBox);

        boolean isCheckBoxChecked=false;
        isCheckBoxChecked=sharedPref.getBoolean("remember_emailPassword_checkBox",false);
        System.out.println("checkboxvalue: "+isCheckBoxChecked);
        checkBox.setChecked(isCheckBoxChecked);
        if(isCheckBoxChecked){
            String emailS=sharedPref.getString("email",null);
            String passwordS=sharedPref.getString("password",null);
            email.setText(emailS);
            password.setText(passwordS);
        }
        firebaseAuth= FirebaseAuth.getInstance();

    }

    private void setCheckBoxListener() {
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                //Toast.makeText(this, "register", Toast.LENGTH_SHORT).show();
                getFragmentManager().beginTransaction().
                        add(R.id.loginContainer, new RegisterFragment()).//add on top of the static fragment
                        addToBackStack("BBB").//cause the back button scrolling through the loaded fragments
                        commit();
                getFragmentManager().executePendingTransactions();
                break;
            case R.id.login:
                loginUser();
                break;
        }
    }

    private void loginUser() {
        String usernameString=email.getText().toString();
        String passwordString=password.getText().toString();
        if(usernameString.isEmpty())
            Toast.makeText(this, "Username field is empty", Toast.LENGTH_SHORT).show();
        else if(passwordString.isEmpty())
            Toast.makeText(this, "Password field is empty", Toast.LENGTH_SHORT).show();
        else{
            firebaseAuth.signInWithEmailAndPassword(usernameString,passwordString)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this, com.example.besocial.MainActivity.class);
                                startActivity(intent);
                                sendUserToMainActivity();
                                Toast.makeText(LoginActivity.this, "Logged in successfuly", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                String errorMessage=task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Could not log in: "+errorMessage, Toast.LENGTH_LONG ).show();

                            }
                        }

                    });
        }


     //   finish();
    }

    private void sendUserToMainActivity() {
        Intent intent=new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveLoginPreference();

    }
    
    */
/**
     * save user email and password, checkbox - using shared preferances
     *//*

    private void saveLoginPreference() {
        editor.putBoolean("remember_emailPassword_checkBox",checkBox.isChecked());
        editor.putString("email",email.getText().toString());
        editor.putString("password",password.getText().toString());
        editor.commit();
    }
*/
/*public void onClick(View v) {
    switch (v.getId()) {
        case R.id.register:
            //Toast.makeText(this, "register", Toast.LENGTH_SHORT).show();
            getFragmentManager().beginTransaction().
                    replace(R.id.loginActivityContainer, new RegisterFragment()).//add on top of the static fragment
                    addToBackStack("BBB").//cause the back button scrolling through the loaded fragments
                    commit();
            getFragmentManager().executePendingTransactions();
            break;
        case R.id.login:
            loginUser();
            break;
    }
}*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
