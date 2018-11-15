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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    EditText email_edit,pass_edit;
    Button login_button,phn_button;
    TextView forgotPasstxt,needNewAccounttxt;
    FirebaseAuth mAuth;
    ProgressDialog loadingDialogue;

    //FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        loadingDialogue=new ProgressDialog(this);



        email_edit=findViewById(R.id.login_mail_edit_txt);
        pass_edit=findViewById(R.id.login_pass_edit_txt);
        login_button=findViewById(R.id.user_login_button);
        phn_button=findViewById(R.id.user_phn_button);
        forgotPasstxt=findViewById(R.id.forgot_pass_txt);
        needNewAccounttxt=findViewById(R.id.need_new_accnt_txt);

        needNewAccounttxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUsertoRegisterActivity();
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowUserToLogIn();
            }
        });

        phn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phnLoginIntent=new Intent(LoginActivity.this,PhonLoginActivity.class);
                startActivity(phnLoginIntent);
            }
        });
    }



    //If user already logged In he'll directly go to MainActivity
    /*@Override
    protected void onStart() {
        super.onStart();

        if (currentUser!=null){
            sendUserToMain();
        }
    }*/

    private void sendUserToMain() {
        Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
        /*So after getting MainActivity,user cannot go gack to Login/RegisterActivity by using back option.
            s/he have to log out*/
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    //TODO:Send User to Register Activity for if he has nedd to create new Account
    private void sendUsertoRegisterActivity() {
        Intent regIntent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(regIntent);
    }

    //TODO: Allow user to login his/her new account that h/she created successfully.
    public void allowUserToLogIn(){
        String email=email_edit.getText().toString();
        String password=pass_edit.getText().toString();

        // Check the mail and password field if empty.
        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter mail... ",Toast.LENGTH_SHORT).show();
        }if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password... ",Toast.LENGTH_SHORT).show();
        }else {
            loadingDialogue.setTitle("Sign In");
            loadingDialogue.setMessage("Please wait...");
            loadingDialogue.setCanceledOnTouchOutside(true);
            loadingDialogue.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        sendUserToMain();
                        Toast.makeText(LoginActivity.this,"Logged account Successfully...",Toast.LENGTH_SHORT).show();
                        loadingDialogue.dismiss();
                    }else {
                        String message=task.getException().toString();
                        Toast.makeText(LoginActivity.this,"Error"+message,Toast.LENGTH_SHORT).show();
                        loadingDialogue.dismiss();
                    }
                }
            });
        }
    }
}
