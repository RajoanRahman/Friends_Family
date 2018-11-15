package com.example.user.friendsandfamily;

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

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewPager myViewPager;
    TabLayout mTabLayout;

    //"FireBaseUser" class Represents a user's profile information in project's user database
    private FirebaseUser currentUser;
    FirebaseAuth mAuth;
    DatabaseReference rootRef;
    //Access the class
    TabsAccessAdapter tabsAccessAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        rootRef=FirebaseDatabase.getInstance().getReference();

        toolbar=findViewById(R.id.main_toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Friends and Family");
        myViewPager=findViewById(R.id.main_tabs_pager);

        tabsAccessAdapter=new TabsAccessAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(tabsAccessAdapter);

        mTabLayout=findViewById(R.id.main_tabs);

        //set the viewPager Fragments on TabLayout.
        mTabLayout.setupWithViewPager(myViewPager);
    }

    //TODO:If user is New, he'll go the LoginActivity
    //TODO:If User logged in he'll directly go to Settings Activity for update UserName and others.

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser==null){
            sendUserToLogin();
        }else {
            verifyUserExistence();
        }
    }

    private void sendUserToLogin() {
        Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    //TODO:Add Menu Items on MainActivity and Handler the menu Items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.option_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);

         if (item.getItemId()==R.id.log_Out){
             mAuth.signOut();
             sendUserToLogin();
         }else if (item.getItemId()==R.id.settings_acccount){
             sendUserToSettingsActivity();
         }else if (item.getItemId()==R.id.find_friends){
             sendUserToFindFriendsActivity();
         }else if (item.getItemId()==R.id.create_group){
             requestForNewGroup();
         }

         return true;
    }

    //TODO: Create a AlertDialogue with EdittText field for creating a New Group Chat option
    private void requestForNewGroup() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name");
        final EditText groupNameField=new EditText(MainActivity.this);
        groupNameField.setHint("e.g.Group Name");
        builder.setView(groupNameField);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName=groupNameField.getText().toString();
                if (TextUtils.isEmpty(groupName)){
                    Toast.makeText(MainActivity.this,"Enter the group name",Toast.LENGTH_SHORT).show();
                }else {
                    createNewGroup(groupName);
                }
            }

            
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               dialog.cancel();
            }
        });

        builder.show();
    }

    //TODO:Save the Group Name into the FireBase
    private void createNewGroup(final String groupName) {
        rootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this,groupName+" is Created Successfully..",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void sendUserToSettingsActivity(){
        Intent settingIntent=new Intent(MainActivity.this,SettingsActivity.class);
        settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingIntent);
        finish();
    }

    public void sendUserToFindFriendsActivity(){
        Intent findFriendsIntent=new Intent(MainActivity.this,FindFriendsActivity.class);
        startActivity(findFriendsIntent);

    }

    //TODO:We'll check if the user id is already in database or if the user already logged in the database
    public void verifyUserExistence() {
        String currentUserId=mAuth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            //A DataSnapshot instance contains data from a Firebase Database location.
            // Any time you read Database data, you receive the data as a DataSnapshot.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //If user has a name under "name" child class s/he already has set his/her userName.
                if ((dataSnapshot.child("name").exists())){
                    Toast.makeText(MainActivity.this,"Welcome",Toast.LENGTH_SHORT).show();
                }else {
                    sendUserToSettingsActivity();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
