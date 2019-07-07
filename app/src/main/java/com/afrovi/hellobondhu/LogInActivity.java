package com.afrovi.hellobondhu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    private Toolbar mainToolbar;

    private Button  logInButton, phoneButton;
    private EditText emailAddressEditText, passwordEditText;
    private TextView forgetPassword, newAccount;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //Toolbar with name Hello Bondhu
        mainToolbar = (Toolbar) findViewById(R.id.mainPageToolBar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Hello Bondhu");


        initializeField();

        //Geting firebase instance
        mAuth = FirebaseAuth.getInstance();


        //Go to Registration
        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendUserToRegisterActivity();
            }
        });

        //Checking user name and pass for Login

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userVerification();
            }
        });


    }

    private void userVerification()
    {
        String email = emailAddressEditText.getText().toString();
        String password = passwordEditText.getText().toString();


        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please Enter your Mail Address", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please Enter your Password", Toast.LENGTH_SHORT).show();
        }


        else
        {
            //Progress Bar Operations
            loadingBar.setTitle("Singing In");
            loadingBar.setMessage("Please wait ");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();


            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {


                                sendUserToMainActivity();
                                Toast.makeText(LogInActivity.this, "Log In Successful", Toast.LENGTH_SHORT).show();


                            }
                            else
                            {
                                String message =task.getException().toString();
                                Toast.makeText(LogInActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
        }
    }

    private void initializeField()
    {
        logInButton = (Button) findViewById(R.id.logInButton);
        phoneButton = (Button) findViewById(R.id.buttonPhone);
        emailAddressEditText = (EditText) findViewById(R.id.mailAddress);
        passwordEditText = (EditText)findViewById(R.id.password);
        forgetPassword = (TextView) findViewById(R.id.textViewTForgetPassword);
        newAccount = (TextView)findViewById(R.id.textViewneed_an_account);
        loadingBar = new ProgressDialog(this);


    }


    private void sendUserToMainActivity()
    {
        Intent mainActivityIntent = new Intent(LogInActivity.this,MainActivity.class);
        //Validation need for not going into back Button
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();
    }

    private void sendUserToRegisterActivity()
    {
        Intent registerActivity = new Intent(LogInActivity.this,RegisterActivity.class);
        startActivity(registerActivity);
    }

}
