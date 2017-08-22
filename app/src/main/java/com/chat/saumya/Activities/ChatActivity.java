package com.chat.saumya.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.chat.saumya.Adapter.ChatMsgAdapter;
import com.chat.saumya.Models.Chat;
import com.chat.saumya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    String otheruid = "";
    String name="";
String currentuserid = "";
     List<Chat>chatList;
    ListView listView;
    ImageButton send_btn;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    ChatMsgAdapter chatListAdapter;
    String chatroomid="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        currentuserid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        Intent i=getIntent();
        if(i!=null)
        {

            otheruid=getIntent().getStringExtra("otheruid");
            name=getIntent().getStringExtra("Name");
        }
        if(!name.equals(""))
        {
            setTitle(name);
        }
        listView=(ListView)findViewById(R.id.list);
        send_btn=(ImageButton)findViewById(R.id.sendMessageButton);
        chatList=new ArrayList<Chat>();
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage();


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


        ValueEventListener eventlistener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                chatList.clear();
                if (dataSnapshot.getValue() != null) {
                    DataSnapshot child;
                    Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        child = (DataSnapshot) iterator.next();
                        if (child.getKey().equals("chat")) {
                            Iterator chatRoomIterator = child.getChildren().iterator();
                            while (chatRoomIterator.hasNext()) {
                            DataSnapshot chatRoomSnapshot=(DataSnapshot) chatRoomIterator.next();
                                if(chatRoomSnapshot.getKey().equals(currentuserid+":"+otheruid)) {
                                    chatroomid = currentuserid + ":" + otheruid + "";
                                    Iterator iterator1 = chatRoomSnapshot.getChildren().iterator();
                                    while (iterator1.hasNext()) {
                                        DataSnapshot snapshot = (DataSnapshot) iterator1.next();
                                        Chat chat = snapshot.getValue(Chat.class);
                                        chatList.add(chat);

                                    }
                                }
                                else if (chatRoomSnapshot.getKey().equals(otheruid+":"+currentuserid)){
                                    chatroomid=otheruid+":"+currentuserid;
                                    Iterator iterator1=chatRoomSnapshot.getChildren().iterator();
                                    while (iterator1.hasNext()){
                                        DataSnapshot snapshot=(DataSnapshot) iterator1.next();
                                        Chat chat=snapshot.getValue(Chat.class);
                                        chatList.add(chat);
                                    }





                                }




                                }
                            }
                        }
                    }
                chatListAdapter=new ChatMsgAdapter(ChatActivity.this,chatList);
                listView.setAdapter(chatListAdapter);
                listView.setSelection(chatList.size()-1);
                }




            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(eventlistener);
        valueEventListener =eventlistener;

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(valueEventListener!=null)
        {
            databaseReference.child("chat").removeEventListener(valueEventListener);
        }
    }

    private void sendMessage(){
        String timestamp="";
        EditText inputtext=(EditText)findViewById(R.id.messageEditText);
        String input=inputtext.getText().toString();
        if(chatroomid.equals("")){
            chatroomid =currentuserid+":"+otheruid;
        }
        if(!input.equals("") &&!currentuserid.equals("")&&!otheruid.equals(""))
        {
            Long tslong= System.currentTimeMillis()/1000;
            timestamp=tslong.toString();
            Chat chat=new Chat(timestamp,currentuserid,input,true);

            Map<String,Object> postvalue=chat.tomap();
            Map<String,Object> childupdate=new HashMap<>();
            childupdate.put("/chat/"+chatroomid+"/"+timestamp,postvalue);
            databaseReference.updateChildren(childupdate);
            inputtext.setText("");
        }
    }



}
