package com.chat.saumya.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chat.saumya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class UpdateProfile extends AppCompatActivity implements View.OnClickListener {


    EditText ud_name;
    EditText ud_email;
    EditText ud_city;
    EditText ud_cno;
    EditText ud_count;
    Button ud_submit;
    String name, email, contactno, city,countryname;
    DatabaseReference databaseReference;
    FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
fbAuth=FirebaseAuth.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference();

        ud_name = (EditText) findViewById(R.id.ud_name);
        ud_email = (EditText) findViewById(R.id.ud_email);
        ud_city = (EditText) findViewById(R.id.ud_city);
        ud_cno = (EditText) findViewById(R.id.ud_cno);
        ud_count = (EditText) findViewById(R.id.ud_count);
        ud_submit = (Button) findViewById(R.id.ud_submit);
        ud_submit.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot dataSnapshot1 = (DataSnapshot) iterator.next();
                    if (dataSnapshot1.getKey().equals("Name")) {
                        ud_name.setText(dataSnapshot1.getValue().toString());
                    } else if (dataSnapshot1.getKey().equals("Email")) {
                        ud_email.setText(dataSnapshot1.getValue().toString());
                    } else if (dataSnapshot1.getKey().equals("city")) {
                        ud_city.setText(dataSnapshot1.getValue().toString());
                    } else if (dataSnapshot1.getKey().equals("Contact no")) {
                        ud_cno.setText(dataSnapshot1.getValue().toString());
                    }
                    else if (dataSnapshot1.getKey().equals("Country Name")) {
                        ud_count.setText(dataSnapshot1.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        name = ud_name.getText().toString().trim();
        email = ud_email.getText().toString().trim();
        city = ud_city.getText().toString().trim();
        contactno = ud_cno.getText().toString().trim();
         countryname = ud_count.getText().toString().trim();
        switch (v.getId()) {
            case R.id.ud_submit:
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(fbAuth.getCurrentUser().getUid());
                if (!name.equals("")) {
                    databaseReference.child("Name").setValue(name);
                }
                if (!contactno.equals("")) {
                    databaseReference.child("Contact no").setValue(contactno);
                }
                if (!city.equals("")) {
                    databaseReference.child("City").setValue(city);
                }

                if (!countryname.equals("")) {
                    databaseReference.child("Country Name").setValue(countryname);
                }
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                finish();
                break;

        }
    }
}
