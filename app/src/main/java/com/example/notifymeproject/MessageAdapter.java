package com.example.notifymeproject;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageAdapterViewHolder> {
    Context cntx;
    List<Message> messages;
    DatabaseReference mesagedb;

    public MessageAdapter(Context cntx, List<Message> messages, DatabaseReference mesagedb) {
        this.cntx = cntx;
        this.messages = messages;
        this.mesagedb = mesagedb;
    }

    @NonNull
    @Override
    public MessageAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(cntx).inflate(R.layout.message_item,parent,false);
        return new MessageAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapterViewHolder holder, int position) {
        Message mesg=messages.get(position);

            if (mesg.getName().equals(AllMethods.name)){
                holder.txttime.setText(mesg.getDate());
                holder.txtvw.setText("You: "+mesg.getMessage());
                holder.deletebtn.setBackgroundColor(Color.parseColor("#FFF59D"));
                holder.llayout.setBackgroundColor(Color.parseColor("#FFF59D"));
            }
            else{
                holder.txttime.setText(mesg.getDate());
                holder.txtvw.setGravity(Gravity.START);
                holder.txtvw.setText(mesg.getName()+": "+mesg.getMessage());
                holder.deletebtn.setVisibility(View.GONE);
                holder.llayout.setBackgroundColor(Color.parseColor("#81D4FA"));
            }
        }




    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageAdapterViewHolder extends  RecyclerView.ViewHolder{
        TextView txtvw,txttime;
        ImageButton deletebtn;
        RelativeLayout llayout;
        public MessageAdapterViewHolder(@NonNull final View itemView) {
            super(itemView);
            txtvw=itemView.findViewById(R.id.txt_msgtitle);
            txttime=itemView.findViewById(R.id.txt_msgtime);
            deletebtn=itemView.findViewById(R.id.img_delete);
            llayout=itemView.findViewById(R.id.ll_msg);

            deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mesagedb.child(messages.get(getAdapterPosition()).getKey()).removeValue();

                }
            });
        }
    }
}
