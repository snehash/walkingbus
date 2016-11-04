package com.ut.walkingbus.walkingbus;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.MyViewHolder> {

    private List<Child> childList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, status, chaperone_name;
        public ImageView picture;
        public View call, message;


        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            status = (TextView) view.findViewById(R.id.status);
            chaperone_name = (TextView) view.findViewById(R.id.chaperone_status);
            call = view.findViewById(R.id.call);
            message = view.findViewById(R.id.text);
            picture = (ImageView) view.findViewById(R.id.child_image);
        }
    }


    public ParentAdapter(List<Child> childList, Context context) {
        mContext = context;
        this.childList = childList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.parent_child, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Child child = childList.get(position);
        holder.name.setText(child.getName());
        holder.status.setText(child.getStatus());
        holder.chaperone_name.setText(child.getChaperoneName());
        holder.picture.setImageURI(child.getPicture());

        if(holder.status.getText() != null) {

            holder.message.setVisibility(VISIBLE);
            holder.call.setVisibility(VISIBLE);

            holder.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("address", child.getChaperoneNumber());
                    //smsIntent.putExtra("sms_body","Body of Message");
                    mContext.startActivity(smsIntent);
                }
            });

            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + child.getChaperoneNumber()));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mContext.startActivity(callIntent);
                }
            });
        } else {
            holder.message.setVisibility(GONE);
            holder.call.setVisibility(GONE);
        }
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }
}