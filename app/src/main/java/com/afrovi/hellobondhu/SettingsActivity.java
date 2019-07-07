package com.afrovi.hellobondhu;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private Button updateUserInfo;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference roofReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mainToolbar = (Toolbar) findViewById(R.id.mainPageToolBar);
        setSupportActionBar(mainToolbar);
        //problem
        //getSupportActionBar().setTitle("Hello Bondhu");

        mAuth = FirebaseAuth.getInstance();
        currentUserId = FirebaseAuth.getInstance().getUid();
        roofReference = FirebaseDatabase.getInstance().getReference();

        initializingFields();
        //disable username Field for exits user
        userName.setVisibility(View.INVISIBLE);


        updateUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettigs();
            }
        });

        retriveUserInfo();


    }




    private void sendUserToMainActivity()
    {
        Intent mainActivityIntent = new Intent(SettingsActivity.this,MainActivity.class);
        //Validation need for not going into back Button
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();
    }



    private void initializingFields()
    {
        updateUserInfo = (Button) findViewById(R.id.updateButton);
        userName = (EditText) findViewById(R.id.setUserNameEditText);
        userStatus = (EditText)findViewById(R.id.setUserStatusEditText);
        userProfileImage = (CircleImageView)findViewById(R.id.setProfile_image);

    }

    //Retreve data from database for showing store data of userName status and Image
    private void retriveUserInfo()
    {
        roofReference.child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("Name") && (dataSnapshot.hasChild("Image"))))
                {
                    String retreveUSername = dataSnapshot.child("Name").getValue().toString();
                    String retreveStatus = dataSnapshot.child("Status").getValue().toString();
                    String retreveImage = dataSnapshot.child("Image").getValue().toString();

                    Toast.makeText(SettingsActivity.this, " in if", Toast.LENGTH_SHORT).show();

                    userName.setText(retreveUSername);
                    userStatus.setText(retreveStatus);

                }
                else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("Name")))
                {
                    String retreveUSername = dataSnapshot.child("Name").getValue().toString();
                    String retreveStatus = dataSnapshot.child("Status").getValue().toString();

                    userName.setText(retreveUSername);
                    userStatus.setText(retreveStatus);
                    Toast.makeText(SettingsActivity.this, " in else if", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    userName.setVisibility(View.VISIBLE);
                    Toast.makeText(SettingsActivity.this, " Please Set & Update Profile Picture", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Providing all settings info and making Child of the database
    private void updateSettigs()
    {

        String setUserName = userName.getText().toString();
        String updateStatus = userStatus.getText().toString();


        if(TextUtils.isEmpty(setUserName))
        {
            Toast.makeText(this, "Please Enter User Name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(updateStatus))
        {
            Toast.makeText(this, "Please Update your Status", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String ,String> profileMap = new HashMap<>();
            profileMap.put("uid",currentUserId);
            profileMap.put("Name",setUserName);
            profileMap.put("Status", updateStatus);

            roofReference.child("Users").child(currentUserId).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(SettingsActivity.this, " Profile Update Successfully", Toast.LENGTH_SHORT).show();
                                sendUserToMainActivity();
                            }
                            else
                            {
                                String message =task.getException().toString();
                                Toast.makeText(SettingsActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }
}
