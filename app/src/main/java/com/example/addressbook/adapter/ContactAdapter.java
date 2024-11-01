package com.example.addressbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.addressbook.R;
import com.example.addressbook.entities.ContactInfo;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    private Context context;
    private List<ContactInfo> contactInfoList;

    public ContactAdapter(Context context, List<ContactInfo> contactInfoList) {
        this.context = context;
        this.contactInfoList = contactInfoList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_name.setText(contactInfoList.get(position).getContactName());
        holder.tv_phone.setText(contactInfoList.get(position).getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return contactInfoList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_phone;
        ImageView iv_photo;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            iv_photo = itemView.findViewById(R.id.iv_photo);
        }
    }
}
