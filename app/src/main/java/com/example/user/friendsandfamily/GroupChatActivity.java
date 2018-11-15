package com.example.user.friendsandfamily;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
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

    ImageButton sendMessageButton;
    EditText userMessageInput;
    TextView displayTextMessage;
    ScrollView mScrollVIew;
    Toolbar mToolbar;
    String currentUserId,currentUserName,currentDate,currentTime;
    FirebaseAuth mAuth;
    DatabaseReference userRef,groupNameRef,groupMessageKeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        ////Pass the Group Name grom GroupFragment to GroupChat
        String currentGroupName=getIntent().getExtras().get("groupName").toString();
        Toast.makeText(GroupChatActivity.this,currentGroupName,Toast.LENGTH_SHORT).show();

        mToolbar=findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);

        mAuth=FirebaseAuth.getInstance();
        //Getting the CurrentUser
        currentUserId=mAuth.getCurrentUser().getUid();
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameRef=FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        sendMessageButton=findViewById(R.id.send_message_button);
        userMessageInput=findViewById(R.id.input_grp_msg);
        displayTextMessage=findViewById(R.id.grp_chat_txt_display);
        mScrollVIew=findViewById(R.id.my_scroll_view);


        getUserInfo();
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveMessageToDatabase();
                userMessageInput.setText(" ");
                mScrollVIew.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    //TODO: Get the User Name through currentUserId value
    public void getUserInfo(){
        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    currentUserName=dataSnapshot.child("name").getValue().toString();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //TODO:Save Message to FIrebase DataBase
    public void saveMessageToDatabase(){
        String userMessage=userMessageInput.getText().toString();
        String messageKey=groupNameRef.push().getKey().toString();
        if (TextUtils.isEmpty(userMessage)){
            Toast.makeText(this,"Write your message first...",Toast.LENGTH_SHORT).show();
        }else {
            //Set the Date
            Calendar calForDate=Calendar.getInstance();
            SimpleDateFormat currentDatFormat=new SimpleDateFormat("MMM dd,yyyy");
            currentDate=currentDatFormat.format(calForDate.getTime());

            //Set the Time
            Calendar calForTime=Calendar.getInstance();
            SimpleDateFormat currentTimeFormat=new SimpleDateFormat("hh:mm a");
            currentTime=currentTimeFormat.format(calForTime.getTime());

            HashMap<String,Object> grpMessageKey=new HashMap<>();
            groupNameRef.updateChildren(grpMessageKey);

            groupMessageKeyRef=groupNameRef.child(messageKey);

            HashMap<String,Object> groupKeyMap=new HashMap<>();
            groupKeyMap.put("name",currentUserName);
            groupKeyMap.put("message",userMessage);
            groupKeyMap.put("date",currentDate);
            groupKeyMap.put("time",currentTime);

            groupMessageKeyRef.updateChildren(groupKeyMap);
        }
    }

    //TODO: Display Messages for Particular group
    @Override
    protected void onStart() {
        super.onStart();

        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()){
                    displayMessage(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()){
                    displayMessage(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void displayMessage(DataSnapshot dataSnapshot){
        Iterator iterator=dataSnapshot.getChildren().iterator();

        String chatDate=(String) ((DataSnapshot)iterator.next()).getValue();
        String chatMessage=(String) ((DataSnapshot)iterator.next()).getValue();
        String chatName=(String) ((DataSnapshot)iterator.next()).getValue();
        String chatTime=(String) ((DataSnapshot)iterator.next()).getValue();

        displayTextMessage.append(chatName + ":\n" + chatMessage + "\n" + chatTime + "  " + chatDate + "\n\n\n");

        mScrollVIew.fullScroll(ScrollView.FOCUS_DOWN);
    }
}
