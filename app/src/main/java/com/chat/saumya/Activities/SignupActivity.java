package com.chat.saumya.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.chat.saumya.R;
import com.chat.saumya.Models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignupActivity extends AppCompatActivity  implements View.OnClickListener {

    EditText et_name;
    EditText et_email;
    EditText et_pass;
    EditText et_cpass;
    EditText et_cno;
    EditText et_count;
    Button bt_submit;
    String name;
    String email;
    String pass;
    String cpass;
    String cno;
    String count;


     FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);



        et_name = (EditText) findViewById(R.id.et_name);
        et_email = (EditText) findViewById(R.id.et_email);
        et_pass = (EditText) findViewById(R.id.et_pass);
        et_cpass = (EditText) findViewById(R.id.et_cpass);
        et_cno = (EditText) findViewById(R.id.et_cno);
        et_count = (EditText) findViewById(R.id.et_count);
        bt_submit = (Button) findViewById(R.id.bt_submit);

        bt_submit.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
               /* if(user !=null)
                {

                    Toast.makeText(SignupActivity.this,"onAuthStateChanged:signed_in:",Toast.LENGTH_LONG).show();
                }

                else
                {
                    Toast.makeText(SignupActivity.this,"onAuthStateChanged:signed_out",Toast.LENGTH_LONG).show();

                }*/
            }
        };
    }
    @Override
    public void onStart()
    {
    super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
@Override
public void onStop()
{
    super.onStop();
    if(mAuthListener!=null)
    {
        mAuth.removeAuthStateListener(mAuthListener);
    }
}




    @Override
    public void onClick(View v) {


        name = et_name.getText().toString().trim();
        email = et_email.getText().toString().trim();
        pass = et_pass.getText().toString().trim();
        cpass = et_cpass.getText().toString().trim();
        cno = et_cno.getText().toString().trim();
        count = et_count.getText().toString().trim();

        switch (v.getId()) {

            case R.id.bt_submit:


                if (name.equals("")) {
                    et_name.setError("Name is not given");
                } else if (email.equals("")) {
                    et_email.setError("Email is not given");
                } else if (pass.equals("")) {
                    et_pass.setError("Password is not given");
                } else if (cpass.equals("")) {
                    et_cpass.setError("confrm pass field  is empty");
                }
                    else if (count.equals("")) {
                        et_cpass.setError("cant proceed without fiiling this field");
                } else if (!pass.equals(cpass)) {
                    Toast.makeText(this, "password is not  matched", Toast.LENGTH_LONG).show();

                } else {
                    createUser(email,pass);
                    Toast.makeText(this, "password is matched", Toast.LENGTH_LONG).show();
                    //Toast.makeText(this, "sucessfully Registered", Toast.LENGTH_LONG).show();

                }
        }
    }

    private void createUser(final String email, String pass) {

            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        Toast.makeText(SignupActivity.this,"task completed",Toast.LENGTH_LONG).show();
                        saveUserToDatabase(task.getResult().getUser(),name,email,cno,count);
                        //Toast.makeText(this, "sucessfully Registered", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(SignupActivity.this,"task not completed",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    public void saveUserToDatabase(FirebaseUser firebaseUser, final String name, String email, final String cno, final String count){

      String u_id=firebaseUser.getUid();
        Toast.makeText(SignupActivity.this,u_id,Toast.LENGTH_LONG).show();
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference=firebaseDatabase.getReference().child("users").child(u_id);
        databaseReference.child("Email").setValue(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {

                    databaseReference.child("Contact no").setValue(cno);
                    databaseReference.child("Country Name").setValue(count);
                    databaseReference.child("Name").setValue(name);



                    Toast.makeText(SignupActivity.this,"data_inserted",Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(SignupActivity.this,"not inserted",Toast.LENGTH_LONG).show();
            }
        });
    }
}