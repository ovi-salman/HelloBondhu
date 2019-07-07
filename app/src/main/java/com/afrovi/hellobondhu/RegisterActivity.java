package com.afrovi.hellobondhu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private Button  registerButton;
    private EditText registerEmailAddressEditText, registerPasswordEditText;
    private TextView alreadyHaveAnaccount;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference rootReference;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //toolbar using app name
        mainToolbar = (Toolbar) findViewById(R.id.mainPageToolBar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Hello Bondhu");

        //get instance of firebase authentication
        mAuth = FirebaseAuth.getInstance();

        //Database Controlling
        rootReference = FirebaseDatabase.getInstance().getReference();




        initializeFields();






        //adding all button Action
        //send to log in activity
        alreadyHaveAnaccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                sendUserToLoginActivity();
            }
        });
        //send to register Activity
        registerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                createNewAccout();
            }
        });






    }


    //creating new account
    private void createNewAccout()
    {
        String email = registerEmailAddressEditText.getText().toString();
        String password = registerPasswordEditText.getText().toString();


        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please Enter your Mail Address", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please Enter your Password", Toast.LENGTH_SHORT).show();
        }

        //Creating a new account
        else
        {
            //Progress Bar Operations
            loadingBar.setTitle("Create New Account");
            loadingBar.setMessage("We are Creating your Account");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            //creating user with firebase function
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                String currentUserID = mAuth.getCurrentUser().getUid();
                                rootReference.child("Users").child(currentUserID).setValue("");


                                sendUserToMainActivity();
                                Toast.makeText(RegisterActivity.this, "Account Create Successful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                String message =task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void sendUserToMainActivity()
    {
        Intent mainActivityIntent = new Intent(RegisterActivity.this,MainActivity.class);
        //Validation need for not going into back Button
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();
    }


    private void initializeFields()
    {
        registerButton = (Button)findViewById(R.id.registerButton);
        registerEmailAddressEditText = (EditText) findViewById(R.id.registerMailAddress);
        registerPasswordEditText = (EditText) findViewById(R.id.registerPassword);
        alreadyHaveAnaccount = (TextView) findViewById(R.id.textViewAlreadyHaveAccount);
        loadingBar = new ProgressDialog(this);

    }

    private void sendUserToLoginActivity()
    {
        Intent logInIntent = new Intent(RegisterActivity.this,LogInActivity.class);
        startActivity(logInIntent);

    }

}
