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
import com.chat.saumya.Models.User;
import com.chat.saumya.R;
import com.chat.saumya.Utiles.AppController;

import java.util.List;

public class ContactListRecyclerAdapter extends RecyclerView.Adapter<ContactListRecyclerAdapter.MyViewHolder> {

    ImageLoader imageLoader;
    Context context;
    List<User> userList;
    public ContactListRecyclerAdapter(Context contex, List<User> userList){
        this.context = contex;
        this.userList=userList;
        imageLoader = AppController.getInstance().getImageLoader();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{

        NetworkImageView network_chat_image;
        TextView txt_chat_name;

        public MyViewHolder(View itemView) {
            super(itemView);
            network_chat_image = (NetworkImageView)itemView.findViewById(R.id.network_chat_image);
            txt_chat_name = (TextView)itemView.findViewById(R.id.txt_chat_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {


            int position=getAdapterPosition();
            User user=userList.get(position);
            Intent i=new Intent(context, ChatActivity.class);
            i.putExtra("otheruid",user.uid);
            i.putExtra("Name",user.name);
            context.startActivity(i);
            Toast.makeText(context, "Clicked on"+user.name+user.email, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatlist, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        User user = userList.get(position);

        if(userList.get(position).picture_url.equals(null)) {
            holder.network_chat_image.setDefaultImageResId(R.drawable.chat);
        }
        else{
            holder.network_chat_image.setImageUrl(userList.get(position).picture_url, imageLoader);
        }
        holder.txt_chat_name.setText(user.name);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}
