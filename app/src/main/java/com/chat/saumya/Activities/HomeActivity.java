package com.chat.saumya.Activities;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.chat.saumya.Manifest;
import com.chat.saumya.R;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.model.AppInviteContent;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.chat.saumya.Utiles.AppController;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener,View.OnClickListener {
    ImageLoader imageLoader;
    NetworkImageView top_network_imageview;
    NetworkImageView img_logged_user_dp;
    TextView edt_loged_user_name;
    TextView edt_logged_user_email;
    String providerId;
    FirebaseUser firebaseUser;
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth fbAuth;
    DatabaseReference databaseReference;
    TabLayout tabLayout;
    ViewPager viewPager;
    SearchView searchView;
    private int REQUEST_CAMERA=0;
    private int SELECT_FILE=1;
    private Bitmap thumbnail;
    String SELECTED_IMAGE_PATH;
    BitmapFactory.Options  options;
    StorageReference storageRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        databaseReference = FirebaseDatabase.getInstance().getReference();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        imageLoader = AppController.getInstance().getImageLoader();
          storageRef= FirebaseStorage.getInstance().getReferenceFromUrl("gs://secretchat-b2ddb.appspot.com/");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navHeaderView = navigationView.inflateHeaderView(R.layout.nav_header_home);
        img_logged_user_dp = (NetworkImageView)navHeaderView.findViewById(R.id.img_logged_user_dp);
        top_network_imageview = (NetworkImageView) findViewById(R.id.top_network_imageview);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        PagerAdapter pagerAdapter = new com.chat.saumya.Adapter.PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
top_network_imageview.setOnClickListener(this);


        edt_loged_user_name = (TextView) navHeaderView.findViewById(R.id.edt_loged_user_name);
        edt_logged_user_email = (TextView) navHeaderView.findViewById(R.id.edt_loged_user_email);


        fbAuth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        if (firebaseUser != null) {
            for (UserInfo userInfo : firebaseUser.getProviderData()) {
                if (userInfo.getProviderId() != null) {
                    providerId = userInfo.getProviderId();
                }
                if (userInfo.getPhotoUrl() != null)
                {
                    img_logged_user_dp.setImageUrl(userInfo.getPhotoUrl().toString(), imageLoader);
                    //img_logged_user_dp.setImageUrl(userInfo.getPhotoUrl().toString(), imageLoader);
                    top_network_imageview.setImageUrl(userInfo.getPhotoUrl().toString(), imageLoader);
                    databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("picture_url").setValue(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
                }
                else
                {
                    img_logged_user_dp.setDefaultImageResId(R.drawable.bird);
                    top_network_imageview.setDefaultImageResId(R.drawable.bird);
                }
            }
        }




        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });*/





    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_menu, menu);
        searchView=(SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager=(SearchManager)getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(onQueryTextListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_update) {
            Toast.makeText(HomeActivity.this, "going to update", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this,UpdateProfile.class));

        } else if (id == R.id.nav_signout) {
            signout();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void signout() {
        final Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        if (providerId.equals("google.com")) {
            fbAuth.signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    startActivity(i);
                    finish();
                }
            });

        } else if (providerId.equals("facebook.com") && AccessToken.getCurrentAccessToken() != null) {
            fbAuth.signOut();
            LoginManager.getInstance().logOut();
            startActivity(i);
            finish();
        } else {
            fbAuth.signOut();
            startActivity(i);
            finish();
        }

    }

    SearchView.OnQueryTextListener onQueryTextListener=new SearchView.OnQueryTextListener(){


        @Override
        public boolean onQueryTextSubmit(String query) {
            Intent intent=new Intent(HomeActivity.this,SearchResultActivity.class);
            intent.putExtra("query",query);
            startActivity(intent);
            HomeActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            return false;

        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        Query query=databaseReference.child("users").child(fbAuth.getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator=dataSnapshot.getChildren().iterator();
                while(iterator.hasNext())
                {
                    DataSnapshot dataSnapshot1=(DataSnapshot)iterator.next();
                    if(dataSnapshot1.getKey().equalsIgnoreCase("name")){
                     edt_loged_user_name.setText(dataSnapshot1.getValue().toString());
                    }
                    else if(dataSnapshot1.getKey().equalsIgnoreCase("email")){
                        edt_logged_user_email.setText(dataSnapshot1.getValue().toString());
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Query query1=databaseReference.child("users").child(fbAuth.getCurrentUser().getUid()).child("picture_url");
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getKey().equals("picture_url"))
                {
                    img_logged_user_dp.setImageUrl(dataSnapshot.getValue().toString(), imageLoader);
                    //img_logged_user_dp.setImageUrl(userInfo.getPhotoUrl().toString(), imageLoader);
                    top_network_imageview.setImageUrl(dataSnapshot.getValue().toString(), imageLoader);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.top_network_imageview:
                SelectImage();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            if(requestCode==REQUEST_CAMERA)
            {
                thumbnail=(Bitmap)data.getExtras().get("data");
                ByteArrayOutputStream bytes=new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.PNG,90,bytes);
                File destination=new File(Environment.getExternalStorageDirectory(),System.currentTimeMillis()+".PNG");
                FileOutputStream fo;

                try {
                    destination.createNewFile();
                    fo=new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(firebaseUser.getUid()!=null&&thumbnail!=null)
                {
                    Bitmap bitmap=thumbnail;
                    String path= MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"Title",null);
                    uploadProfileImage(Uri.parse(path));
                }
                else{

                }



            }
            else if (requestCode==SELECT_FILE)
            {
                Uri selectImageUri=data.getData();
                String[] projection ={ MediaStore.MediaColumns.DATA};
                Cursor cursor=managedQuery(selectImageUri,projection,null,null,null);
                int columnIndex=cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                SELECTED_IMAGE_PATH=cursor.getString(columnIndex);
                options=new BitmapFactory.Options();
                options.inJustDecodeBounds=true;
                BitmapFactory.decodeFile(SELECTED_IMAGE_PATH,options);
                final int REQUIRED_SIZE=200;
                int scale=1;
                while (options.outWidth/scale/2>=REQUIRED_SIZE&&options.outHeight/scale/2>=REQUIRED_SIZE)
                    scale*=2;
                options.inSampleSize=scale;
                options.inJustDecodeBounds=false;
                thumbnail=BitmapFactory.decodeFile(SELECTED_IMAGE_PATH,options);
                if(firebaseUser!=null && thumbnail!=null) {
                    Bitmap bitmap=thumbnail;
                    String path=MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"Title",null);
                    uploadProfileImage(Uri.parse(path));
                }
                else
                {
                }
            }
        }
    }
    private void SelectImage()
    {
        final CharSequence[] items={"Take Photo","Choose from Gallery","Cancel"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("ADD Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(items[which].equals("Take Photo")){
                     if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                    {
                        if(checkSelfPermission(android.Manifest.permission.CAMERA) != getPackageManager().PERMISSION_GRANTED)
                        {
                            requestPermissions(new String[]{android.Manifest.permission.CAMERA},REQUEST_CAMERA);
                        }
                    }
                    else {
                        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent,REQUEST_CAMERA);
                    }
                }
                else if(items[which].equals("Choose from Gallery")){
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                        if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=getPackageManager().PERMISSION_GRANTED){

                            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},SELECT_FILE);
                        }
                    }
                    else {
                        Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent,"SELECT_FILE"),SELECT_FILE);

                    }
                }
                else if(items[which].equals("Cancel")){
                    dialog.dismiss();

                }

            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CAMERA)
        {
           if(grantResults[0]== PackageManager.PERMISSION_GRANTED){


               Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               startActivityForResult(intent,REQUEST_CAMERA);
           }
            else{

           }
        }
        else if(requestCode==SELECT_FILE)
        {

              if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                  Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                  intent.setType("image/*");
                  startActivityForResult(Intent.createChooser(intent,"SELECT_FILE"),SELECT_FILE);
              }

            else
          {

          }
        }
    }
    public void uploadProfileImage(Uri file){

        StorageMetadata metadata=new StorageMetadata.Builder()
                .setContentType("image/jpg").build();
        StorageReference storageReference=storageRef.child(firebaseUser.getUid()).child("ProfileImages");
        UploadTask uploadTask=storageReference.child("image_dp.jpg").putFile(file,metadata);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {


            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                databaseReference.child("users").child(firebaseUser.getUid()).child("picture_url").setValue(taskSnapshot.getMetadata().getDownloadUrl().toString());
            }
        });



    }
}