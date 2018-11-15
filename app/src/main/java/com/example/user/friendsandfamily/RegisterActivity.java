package com.example.user.friendsandfamily;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText email_reg_edit,pass_reg_edit;
    Button create_New_Account;
    TextView alreadyHaveAccount;

    //FireBase Authentication supports user authentication using passwords, phone numbers, mail.
    FirebaseAuth mAuth;
    ProgressDialog loadingDialogue;
    DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initialize FireBaseAuth
        mAuth=FirebaseAuth.getInstance();
        loadingDialogue=new ProgressDialog(this);
        //"friendsandfamily-ee874" is the root reference of realTime Database in FireBaseDatabase.
        rootRef=FirebaseDatabase.getInstance().getReference();

        email_reg_edit=findViewById(R.id.reg_mail_edit_txt);
        pass_reg_edit=findViewById(R.id.reg_pass_edit_txt);
        create_New_Account=findViewById(R.id.register_createAccount_button);
        alreadyHaveAccount=findViewById(R.id.reg_alreday_accnt_txt);


        //send user to LoginActivity
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToLoginActivity();
            }
        });

        //creating new account for user.
        create_New_Account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUserNewAccount();
            }
        });
    }


    //TODO: Method of creates a new account of a user by Using FireBaseAuth class.
    private void createUserNewAccount() {
        String email=email_reg_edit.getText().toString();
        String password=pass_reg_edit.getText().toString();

        // Check the mail and password field if empty.
        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter mail... ",Toast.LENGTH_SHORT).show();
        }if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password... ",Toast.LENGTH_SHORT).show();
        }else {

            loadingDialogue.setTitle("Creating New Account");
            loadingDialogue.setMessage("Please wait, we're creating new account for you..");
            loadingDialogue.setCanceledOnTouchOutside(true);
            loadingDialogue.show();
            //Creating the account on FireBase
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        /*In the rootDatabase which is "friendsandfamily-ee874". there will be a child class
                        called "Users" where a unique user Id will bw stored. */
                        String currentUserId=mAuth.getCurrentUser().getUid();
                        rootRef.child("Users").child(currentUserId).setValue("");
                        sendUserToMainActivity();
                        //sendUserToLoginActivity();//if account created successfully we've send user to LoginActivity.
                        Toast.makeText(RegisterActivity.this,"Account Created Succesfully..",Toast.LENGTH_SHORT).show();
                        loadingDialogue.dismiss();
                    }else {
                        // Check if any exception is occur.
                        String message=task.getException().toString();
                        Toast.makeText(RegisterActivity.this,"Error"+message,Toast.LENGTH_SHORT).show();
                        loadingDialogue.dismiss();
                    }
                }
            });

        }
    }

    //TODO: Method Send User to Login Activity if already have an account.
    private void sendUserToLoginActivity() {
        Intent loginIntent=new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }

    public void sendUserToMainActivity(){
        Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
        /*So after getting MainActivity,user cannot go gack to Login/RegisterActivity by using back option.
            s/he have to log out*/
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
