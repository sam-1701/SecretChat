package com.chat.saumya.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chat.saumya.Adapter.ChatListRecyclerAdapter;
import com.chat.saumya.Models.Chat;
import com.chat.saumya.Models.User;
import com.chat.saumya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatTabFragment extends Fragment {

    RecyclerView recycler_view;
    List<User> userList;
    List<Chat> chatRoomList;


    public ChatTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        userList = new ArrayList<User>();
        chatRoomList = new ArrayList<Chat>();

        recycler_view = (RecyclerView ) inflater.inflate(R.layout.fragment_chat_tab, container, false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());



        return recycler_view;
    }

    @Override
    public void onStart() {
        super.onStart();

        userList.clear();
        chatRoomList.clear();

        Query query = FirebaseDatabase.getInstance().getReference();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    DataSnapshot chatSnapshot = (DataSnapshot) iterator.next();
                    if (chatSnapshot.getKey().equals("chat")){
                        Iterator chatRoomIterator = chatSnapshot.getChildren().iterator();
                        while (chatRoomIterator.hasNext()){
                            DataSnapshot chatRoomSnapshot = (DataSnapshot) chatRoomIterator.next();

                            Chat chat = new Chat();

                            String str = chatRoomSnapshot.getKey().toString();
                            String[] separated = str.split(":");
                            String user_id_1 = separated[0];
                            String user_id_2 = separated[1];
                            if (user_id_1.equals(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())){

                                chat.authorid = user_id_2;

                                Iterator chatIterator = chatRoomSnapshot.getChildren().iterator();
                                while (chatIterator.hasNext()){ // last iteration
                                    DataSnapshot snapshot = (DataSnapshot) chatIterator.next();

                                    if (user_id_2.equals(snapshot.child("authorid").getValue().toString())){
                                        chat.msg = snapshot.child("msg").getValue().toString();
                                        //break;
                                    }else {
                                        chat.msg = snapshot.child("msg").getValue().toString();
                                        //break;
                                    }
                                }

                                chatRoomList.add(chat);

                            }else if (user_id_2.equals(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())){

                                chat.authorid= user_id_1;

                                Iterator chatIterator = chatRoomSnapshot.getChildren().iterator();
                                while (chatIterator.hasNext()){ // last iteration
                                    DataSnapshot snapshot = (DataSnapshot) chatIterator.next();

                                    if (user_id_1.equals(snapshot.child("authorid").getValue().toString())){
                                        chat.msg = snapshot.child("msg").getValue().toString();
                                    }else {
                                        chat.msg = snapshot.child("msg").getValue().toString();
                                    }
                                }

                                chatRoomList.add(chat);

                            }
                        }
                    }else if (chatSnapshot.getKey().equals("users")){
                        Iterator userIterator = chatSnapshot.getChildren().iterator();
                        while (userIterator.hasNext()){
                            DataSnapshot snapshot = (DataSnapshot) userIterator.next();

                            String key = "";
                            String name = "";
                            String email = "";
                            String artistType = "";
                            String cno = "";
                            String city = "";

                            String country = "";
                            String picture_url = "";
                            String fcmId = "";


                            key = snapshot.getKey();

                            Iterator iterator1 = snapshot.getChildren().iterator();
                            while (iterator1.hasNext()){
                                DataSnapshot snapshot1 = (DataSnapshot) iterator1.next();
                                if (snapshot1.getKey().equals("Name")){
                                    name = snapshot1.getValue().toString().trim();
                                }else if (snapshot1.getKey().equals("Email")) {
                                    email = snapshot1.getValue().toString().trim();
                                }
                                else if (snapshot1.getKey().equals("Contact no")){
                                    cno = snapshot1.getValue().toString().trim();
                                }else if (snapshot1.getKey().equals("city")){
                                    city = snapshot1.getValue().toString().trim();

                                }else if (snapshot1.getKey().equals("country")){
                                    country = snapshot1.getValue().toString().trim();
                                }else if (snapshot1.getKey().equals("picture_url")){
                                    picture_url = snapshot1.getValue().toString().trim();
                                }
                            }

                            User user = new User(name,email,cno,key,picture_url);

                            //User user = new User(name, email, providerId, photoUrl, artistType, mobile, country,state, city, key, fcmId);

                            userList.add(user);

                        }
                    }
                }

                for (User user: userList){

                    for (Chat chat : chatRoomList){
                        if (chat.authorid.equals(user.uid)){

                            chat.name = user.name;
                            chat.picurl = user.picture_url;
                        }
                    }
                }

                //ChatListRecyclerViewAdapter chatListRecyclerViewAdapter = new ChatListRecyclerViewAdapter(getActivity(), userList);
                recycler_view.setAdapter(new ChatListRecyclerAdapter(getActivity(), chatRoomList));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
