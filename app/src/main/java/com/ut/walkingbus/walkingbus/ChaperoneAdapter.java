package com.ut.walkingbus.walkingbus;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ChaperoneAdapter extends RecyclerView.Adapter<ChaperoneAdapter.MyViewHolder> {

    private List<Child> childList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, status;
        public ImageView picture;
        private Button red, green, blue;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            status = (TextView) view.findViewById(R.id.status);
            picture = (ImageView) view.findViewById(R.id.child_image);
            red = (Button) view.findViewById(R.id.red);
            green = (Button) view.findViewById(R.id.green);
            blue = (Button) view.findViewById(R.id.blue);
        }
    }


    public ChaperoneAdapter(List<Child> childList, Context context) {
        mContext = context;
        this.childList = childList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chaperone_child, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Child child = childList.get(position);
        holder.name.setText(child.getName());
        holder.status.setText(child.getStatus());

        holder.blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ServerHelper helper = LoginActivity.getServerHelper();
                Log.d("HEY","Blue pressed");
                helper.updateChildStatus(child.getId(), "Picked Up");
            }
        });
        holder.red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ServerHelper helper = LoginActivity.getServerHelper();
                Log.d("HEY","Red pressed");
                helper.updateChildStatus(child.getId(), "LOST");
            }
        });
        // holder.picture.setImageURI(child.getPicture());
        /*switch(child.getStatus()) {
            case "Not Yet Picked Up":
                break;

        } */
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }
}