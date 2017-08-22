package com.chat.saumya.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.chat.saumya.Activities.ChatActivity;
import com.chat.saumya.Models.Chat;
import com.chat.saumya.Models.User;
import com.chat.saumya.R;
import com.chat.saumya.Utiles.AppController;

import java.util.List;

public class ChatListRecyclerAdapter extends RecyclerView.Adapter<ChatListRecyclerAdapter.MyViewHolder> {

    ImageLoader imageLoader;
    Context context;
    List<Chat> chatList;
    public ChatListRecyclerAdapter(Context contex, List<Chat> chatList){
        this.context = contex;
        this.chatList=chatList;
        imageLoader = AppController.getInstance().getImageLoader();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{

        NetworkImageView network_chat_image;
        TextView txt_chat_name;
        TextView   txt_msg;



        public MyViewHolder(View itemView) {
            super(itemView);
            network_chat_image = (NetworkImageView)itemView.findViewById(R.id.network_chat_image);
            txt_chat_name = (TextView)itemView.findViewById(R.id.txt_chat_name);
            txt_msg = (TextView)itemView.findViewById(R.id.txt_msg);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {


            int position = getAdapterPosition();
            Chat chat = chatList.get(position);
            Toast.makeText(context, "Clicked on" + chat.name, Toast.LENGTH_SHORT).show();
            Intent i=new Intent(context, ChatActivity.class);
            i.putExtra("otheruid",chat.authorid);
            i.putExtra("Name",chat.name);
            context.startActivity(i);
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatlist, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
Chat chat = chatList.get(position);

        if(chatList.get(position).picurl.equals(null)) {
            holder.network_chat_image.setDefaultImageResId(R.drawable.chat);
        }
        else{
            holder.network_chat_image.setImageUrl(chatList.get(position).picurl, imageLoader);
        }
        holder.txt_chat_name.setText(chat.name);
        holder.txt_msg.setText(chat.msg);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

}
