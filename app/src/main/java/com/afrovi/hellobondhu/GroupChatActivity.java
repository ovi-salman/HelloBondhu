package com.afrovi.hellobondhu;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mtoolBar;
    private ImageButton sendMessageButton;
    private EditText userMessage;
    private ScrollView mScrollView;
    private TextView displayTextMessage;
    private FirebaseAuth mAuth;
    private DatabaseReference userReference,groupNameReference,groupMessageKeyRef;

    private String currentGroupName,currentUserId,currentUserName,currentData,currentTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);



        mAuth= FirebaseAuth.getInstance();
        currentUserId= mAuth.getCurrentUser().getUid();
        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(this, "Group Created Successfully", Toast.LENGTH_SHORT).show();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");


        groupNameReference =FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        initializeField();


        //geting all info about a user
        getUserInfo();




        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sendMessageInfoToDataBase();

                userMessage.setText("");
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }



    private void getUserInfo()
    {
        userReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
              if(dataSnapshot.exists())
              {
                  currentUserName =dataSnapshot.child("Name").getValue().toString();

              }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        groupNameReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    displayMessage(dataSnapshot);

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    displayMessage(dataSnapshot);

                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayMessage(DataSnapshot dataSnapshot)
    {
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext())
        {
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();


            displayTextMessage.append(chatName+ "  :\n"+ chatMessage +"  :\n"+chatTime + "       "+chatDate+ "\n\n\n" );

            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);


        }

    }

    private void initializeField()
    {
        mtoolBar = (Toolbar)findViewById(R.id.groupChatBarLayout);
        setSupportActionBar(mtoolBar);
        getSupportActionBar().setTitle(currentGroupName);


        sendMessageButton = (ImageButton)findViewById(R.id.messageButton);
        userMessage = (EditText)findViewById(R.id.messageEditText);
        displayTextMessage = (TextView)findViewById(R.id.groupChatTextDisplay);
        mScrollView = (ScrollView)findViewById(R.id.textScroll);

    }

    private void sendMessageInfoToDataBase()
    {
        String message = userMessage.getText().toString();
        String messageKey = groupNameReference.push().getKey();

        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Please Write Message First", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar callForDate = Calendar.getInstance();

            SimpleDateFormat  currentDateFormat = new SimpleDateFormat("MMM dd,yyyy");
            currentData = currentDateFormat.format(callForDate.getTime());


            Calendar callForTime = Calendar.getInstance();

            SimpleDateFormat  currentTimeFormat = new SimpleDateFormat("hh:mm:ss a");
            currentTime = currentTimeFormat.format(callForTime.getTime());


            HashMap<String, Object> groupMessageKey = new HashMap<>();
            groupNameReference.updateChildren(groupMessageKey);

            groupMessageKeyRef = groupNameReference.child(messageKey);
            HashMap<String,Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("Name",currentUserName);
            messageInfoMap.put("Message",message);
            messageInfoMap.put("Date",currentData);
            messageInfoMap.put("Time",currentTime);
            groupMessageKeyRef.updateChildren(messageInfoMap);



        }
    }


}
