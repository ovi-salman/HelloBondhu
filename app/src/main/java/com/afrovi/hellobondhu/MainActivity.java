package com.afrovi.hellobondhu;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity

{

    private Toolbar mainToolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabAccesser tabAccesser;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootReference;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mainToolbar = (Toolbar) findViewById(R.id.mainPageToolBar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Hello Bondhu");

        //adding all tabs to the viewPager
        viewPager = (ViewPager)findViewById(R.id.mainTabsPager);
        tabAccesser = new TabAccesser(getSupportFragmentManager());
        viewPager.setAdapter(tabAccesser);
        //adding tablayouts in viewpager
        tabLayout = (TabLayout) findViewById(R.id.mainTabs);
        tabLayout.setupWithViewPager(viewPager);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        rootReference = FirebaseDatabase.getInstance().getReference();

    }


    @Override
    protected void onStart()
    {
        super.onStart();

        if(currentUser == null)
        {
            sendUserToLogInActivity();
        }
        else
        {
            verifyExistence();
        }


    }


    private void sendUserToLogInActivity()
    {
        Intent logInIntent = new Intent(MainActivity.this,LogInActivity.class);
        logInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(logInIntent);
        finish();
    }
    private void sendUserToSettingsActivity()
    {
        Intent settingsActivity = new Intent(MainActivity.this,SettingsActivity.class);
        settingsActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsActivity);
        finish();
    }




    //adding menu option from res folder
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
         super.onCreateOptionsMenu(menu);


        getMenuInflater().inflate(R.menu.options_menu,menu);
        return  true;
    }
    //work description of menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.logout)
        {
            mAuth.signOut();
            sendUserToLogInActivity();
        }
        if(item.getItemId() == R.id.findFriend)
        {


        }
        if(item.getItemId() == R.id.settings)
        {
            sendUserToSettingsActivity();
        }
        if(item.getItemId() == R.id.createGroup)
        {
            requestNewGroup();

        }

        return true;

    }

    private void requestNewGroup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name");

        final EditText groupFieldName = new EditText(MainActivity.this);
        builder.setView (groupFieldName);

        //positive button
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupFieldName.getText().toString();

                if(TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(MainActivity.this, " Please Provide Group Name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    createNewGroup(groupName);
                }
            }
        });

        //negative Button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();





    }
    //Creating Group Name
    private void createNewGroup(final String groupName)
    {
        rootReference.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, groupName+" group is Created Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void verifyExistence()
    {
        String currentUserId = mAuth.getCurrentUser().getUid();

        rootReference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //User Already Set User Name (Mandatory)
                if(dataSnapshot.child("Name").exists())
                {
                    Toast.makeText(MainActivity.this, "WellCome", Toast.LENGTH_SHORT).show();
                }

                //if the user Just created Account but not update anything
                else
                {
                    sendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




}
