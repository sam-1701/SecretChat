package com.chat.saumya.Activities;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.chat.saumya.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;





import java.util.Arrays;

import static android.R.attr.data;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 9001;
    private static final int FB_Sign_IN = 64206;


    EditText et_username;
    EditText et_password;
    Button bt_login;
    Button bt_signup;
    TextView tv_forgotpass;
    Button login_button;
    // SignInButton sign_in_button;
    SignInButton sign_in_button;
    GoogleSignInOptions gso;
    private CallbackManager callbackManager;
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth mAuth;
    FirebaseAuth fbAuth;
    ProgressDialog progressDialog;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseAuth.AuthStateListener fbAuthListener;
     static int  f=0;
    String login_user, login_pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        fbAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
               /* FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(LoginActivity.this, "onAuthStateChanged:signed_in:", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "onAuthStateChanged:signed_out", Toast.LENGTH_LONG).show();


                }
                */
            }
        };


        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);


        bt_login = (Button) findViewById(R.id.bt_login);
        bt_signup = (Button) findViewById(R.id.bt_signup);
        login_button = (Button) findViewById(R.id.login_button);
        tv_forgotpass = (TextView) findViewById(R.id.tv_forgotpass);
        sign_in_button = (SignInButton) findViewById(R.id.sign_in_button);
        bt_login.setOnClickListener(this);
        bt_signup.setOnClickListener(this);
        login_button.setOnClickListener(this);
        sign_in_button.setOnClickListener(this);
        tv_forgotpass.setOnClickListener(this);

       gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStop();
        fbAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {

            fbAuth.removeAuthStateListener(mAuthListener);
        }

    }


    @Override
    public void onClick(View v) {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Processing");
        login_user = et_username.getText().toString().trim();
        login_pass = et_password.getText().toString().trim();
        switch (v.getId()) {
            case R.id.bt_login:
                progressDialog.show();
                if (login_user.equals(""))
                    et_username.setError("Empty");
                else if (et_password.equals(""))
                    et_password.setError("Empty");
                else {
                    fbAuth.signInWithEmailAndPassword(login_user, login_pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("", "signInWithEmail..." + task.isSuccessful());


                            if (!task.isSuccessful()) {
                                Log.w("", "signInWithEmail:failed", task.getException());
                                Toast.makeText(LoginActivity.this, "Wrong Credential",
                                        Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            } else {
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
                //  Toast.makeText(this,"LoginActivity",Toast.LENGTH_LONG).show();
                break;
            case R.id.bt_signup:
                Toast.makeText(LoginActivity.this, "SIGNUP", Toast.LENGTH_LONG).show();
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);

                startActivity(i);

                break;
            case R.id.login_button:
                Toast.makeText(this, "FBLOGIN", Toast.LENGTH_LONG).show();
                facebook_login();
                break;

            case R.id.sign_in_button:
                signIn();
                Toast.makeText(this, "GOGGLE_LOGIN", Toast.LENGTH_LONG).show();
                break;
            case R.id.tv_forgotpass:
                Intent it = new Intent(LoginActivity.this, ForgotpasswordActivity.class);
                startActivity(it);
                Toast.makeText(this, "FORGOTPASSWORD", Toast.LENGTH_LONG).show();
                break;


        }

    }

    public void facebook_login() {

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(LoginActivity.this, "success", Toast.LENGTH_LONG);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "cancel....", Toast.LENGTH_LONG);
            }

            @Override
            public void onError(FacebookException error) {

                progressDialog.dismiss();

                Toast.makeText(LoginActivity.this, "something is wrong", Toast.LENGTH_LONG);
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {

        Log.d("fbbb", "handleFacebookAccessToken" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        fbAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("fbbb", "signInWithCredential" + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.w("fbbbb", "signWithCredential", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Fb login_successful", Toast.LENGTH_LONG).show();
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    final DatabaseReference databaseReference = firebaseDatabase.getReference().child("users").child(uid);
                    databaseReference.child("Name").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                databaseReference.child("Email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                Toast.makeText(LoginActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            } else
                                Toast.makeText(LoginActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }

        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else if (requestCode == FB_Sign_IN) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void signIn() {
        //f=1;
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.

            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
            //Intent i = new Intent(LoginActivity.this, HomeActivity.class);
          //  startActivity(i);
            Toast.makeText(LoginActivity.this, "Woooooooo!!!!", Toast.LENGTH_LONG).show();
            // mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            // updateUI(false);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Google", "firebaseAauthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);


        FirebaseAuth fbauth = FirebaseAuth.getInstance();
        fbauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d("Google", "signInWithCredential:onComplete:" + task.isSuccessful());
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        final DatabaseReference databaseReference = firebaseDatabase.getReference().child("users").child(uid);
                        databaseReference.child("Name").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    //databaseReference.child("Contact").setValue(phone);
                                   // databaseReference.child("picture_url").setValue(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
                                    databaseReference.child("Email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    //
                                    task.getResult();
                                    Toast.makeText(LoginActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("Google", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
//                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    // [END auth_with_google]


}
