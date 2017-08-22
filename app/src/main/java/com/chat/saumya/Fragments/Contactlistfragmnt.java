package com.chat.saumya.Fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chat.saumya.Adapter.ContactListRecyclerAdapter;
import com.chat.saumya.Models.User;
import com.chat.saumya.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Contactlistfragmnt extends Fragment {
    List<User> ContactList;
    List<User>userList;
    List<User>filterlist;
    RecyclerView recyclerView;


    public Contactlistfragmnt() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ContactList = new ArrayList<User>();
        userList=new ArrayList<User>();
        filterlist=new ArrayList<User>();
        // Inflate the layout for this fragment
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_chat_tab, container, false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);


        // return inflater.inflate(R.layout.fragment_contactlistfragmnt, container, false);
        return recyclerView;
    }


    @Override
    public void onStart() {
        super.onStart();
        ContactList.clear();
        userList.clear();
        filterlist.clear();
        Cursor cur = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC");
        if (cur.moveToFirst()) {
            do {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));


                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor cur1 = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
                    while (cur1.moveToNext()) {
                        String contactno = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String contactname = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));


                        User user = new User();

                        user.name = contactname + "->" + contactno;
                        user.cno=contactno;
                        ContactList.add(user);
                        break;

                    }
                    cur1.close();
                }
            }
                while (cur.moveToNext()) ;
               // ContactListRecyclerAdapter chatListRecyclerAdapter = new ContactListRecyclerAdapter(getActivity(), ContactList);
               // recyclerView.setAdapter(chatListRecyclerAdapter);


            }




        Query query = FirebaseDatabase.getInstance().getReference().child("users");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext())
                {
                    DataSnapshot dataSnapshot1 = (DataSnapshot) iterator.next();
                    Iterator iterator1 = dataSnapshot1.getChildren().iterator();
                    User user = new User();
                    user.uid = dataSnapshot1.getKey();
                    while (iterator1.hasNext())
                    {

                        DataSnapshot dataSnapshot2 = (DataSnapshot) iterator1.next();
                        if(dataSnapshot2.getKey().equals("Name")){
                            user.name = dataSnapshot2.getValue().toString();
                        }
                        if(dataSnapshot2.getKey().equals("Email")){
                            user.email = dataSnapshot2.getValue().toString();
                        }
                        if(dataSnapshot2.getKey().equals("Contact no")){
                            user.cno = dataSnapshot2.getValue().toString();
                        }
                    }
                    userList.add(user);

                }
                Log.d("Userlist",userList.toString()+"========"+ ContactList.toString());
                for(User firebaseuser:userList)
                {
                    for (User contactuser:ContactList)
                    {
                        String phonecontact=contactuser.cno;
                        phonecontact=phonecontact.replaceAll(" ","");

                        if(phonecontact.length()>=10)
                        {
                            phonecontact=phonecontact.substring(phonecontact.length()-10);
                        }
                        if(firebaseuser.cno.trim().equals(phonecontact) && !firebaseuser.equals(""))
                        {
                          filterlist.add(firebaseuser) ;
                            break;
                        }
                    }
                }


                 ContactListRecyclerAdapter chatListRecyclerAdapter = new ContactListRecyclerAdapter(getActivity(), filterlist);
                recyclerView.setAdapter(chatListRecyclerAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        }


    }
