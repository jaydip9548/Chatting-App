package com.example.chattingapp.userAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chattingapp.MessageActivity;
import com.example.chattingapp.Model.User;
import com.example.chattingapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mcontect;
    private List<User> list;

    public UserAdapter(Context mcontect, List<User> list) {
        this.mcontect = mcontect;
        this.list = list;
    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user = list.get(position);
        holder.username.setText(user.getUsername());
        if(user.getImageUrl().equals("default")){
            holder.circleImageView.setImageResource(R.mipmap.ic_launcher);;

        }else{
            Glide.with(mcontect).load(user.getImageUrl())
                    .into(holder.circleImageView);
        }
holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mcontect, MessageActivity.class);
        intent.putExtra("userid",user.getId());
        mcontect.startActivity(intent);
    }
});
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        CircleImageView circleImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_name);
            circleImageView = itemView.findViewById(R.id.user_circle_image);
        }

    }
}