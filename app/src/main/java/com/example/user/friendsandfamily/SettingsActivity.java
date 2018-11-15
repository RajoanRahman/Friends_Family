package com.example.user.friendsandfamily;

import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    CircleImageView circleImageView;
    EditText updateUserStatus,updateUserName;
    Button update_button;
    FirebaseAuth mAuth;
    DatabaseReference rootRef;
    String currentUserId;
    private static final int galleryPic=1;
    StorageReference userProfileImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAuth=FirebaseAuth.getInstance();
        rootRef=FirebaseDatabase.getInstance().getReference();
        currentUserId=mAuth.getCurrentUser().getUid();
        userProfileImageRef=FirebaseStorage.getInstance().getReference().child("ProfileImages");

        circleImageView=findViewById(R.id.set_profile_image);
        updateUserStatus=findViewById(R.id.set_user_status);
        updateUserName=findViewById(R.id.set_user_Name);
        update_button=findViewById(R.id.set_user_update);

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               updateSettings();
            }
        });

        retrieveUserData();

        //TODO: Click the Image circle on Settings,it will go to the mobile storage
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,galleryPic);
            }
        });
    }

    private void sendUserToMainActivity() {
        Intent mainIntent=new Intent(SettingsActivity.this,MainActivity.class);
        /*So after getting MainActivity,user cannot go gack to Login/RegisterActivity by using back option.
            s/he have to log out*/
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    //TODO: Set/Get the Crop Image from Storage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==galleryPic && resultCode==RESULT_OK && data!=null){
            Uri imageUri=data.getData();

            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK){

                Uri resultUri=result.getUri();// resultUri value contains the selected croping image.

                StorageReference filePath=userProfileImageRef.child(currentUserId+".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SettingsActivity.this,"Profile Uploaded Successfully",Toast.LENGTH_SHORT).show();
                        }else {
                            String message=task.getException().toString();
                            Toast.makeText(SettingsActivity.this,"Error: "+message,Toast.LENGTH_LONG).show();
                            Log.d("Error",message);
                            //ToastExpander.showFor(atost, 5000);


                        }
                    }
                });
            }
        }
    }

    //TODO: Update user Profile Settings
    public void updateSettings(){
        String setUserName=updateUserName.getText().toString();
        String setUserStatus=updateUserStatus.getText().toString();


        if (TextUtils.isEmpty(setUserName)){
            Toast.makeText(SettingsActivity.this,"Update your User Name first",Toast.LENGTH_SHORT).show();
        }if (TextUtils.isEmpty(setUserStatus)){
            Toast.makeText(SettingsActivity.this,"Update your Status",Toast.LENGTH_SHORT).show();
        }else {


            // HashMap contains values based on the key.That is to say it will have a value under a Key
            HashMap<String,String> profileMap=new HashMap<>();
            profileMap.put("uid",currentUserId);
            profileMap.put("name",setUserName);
            profileMap.put("status",setUserStatus);

            rootRef.child("Users").child(currentUserId).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                sendUserToMainActivity();
                                Toast.makeText(SettingsActivity.this,"Profile Updated Successfully",Toast.LENGTH_SHORT).show();
                            }else {
                                String message=task.getException().toString();
                                Toast.makeText(SettingsActivity.this,"Error"+message,Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        }
    }

    //TODO:Retrieve User Data to set on Setting Activity.
    public void retrieveUserData(){
        rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") &&(dataSnapshot.hasChild("image")) )){
                    String retrieveUserName=dataSnapshot.child("name").getValue().toString();
                    String retrieveUseStatus=dataSnapshot.child("status").getValue().toString();
                    String retrieveUseImage=dataSnapshot.child("image").getValue().toString();

                    updateUserName.setText(retrieveUserName);
                    updateUserStatus.setText(retrieveUseStatus);
                }else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                    String retrieveUserName=dataSnapshot.child("name").getValue().toString();
                    String retrieveUseStatus=dataSnapshot.child("status").getValue().toString();

                    updateUserName.setText(retrieveUserName);
                    updateUserStatus.setText(retrieveUseStatus);
                }else {
                    Toast.makeText(SettingsActivity.this,"Set your Profile First",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
