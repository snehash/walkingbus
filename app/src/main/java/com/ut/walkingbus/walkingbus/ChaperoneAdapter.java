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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ChaperoneAdapter extends RecyclerView.Adapter<ChaperoneAdapter.MyViewHolder> {
    private static final String TAG = "ChaperoneAdapter";

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
                Log.d(TAG,"Blue pressed");
                TextView t = (TextView)arg0.findViewById(R.id.blue);
                if(t.getText().equals("Picked Up")) {
                    helper.updateChildStatus(child.getId(), "Picked Up");
                    t.setText("Dropped off");
                }
                if(t.getText().equals("Dropped Off")) {
                    helper.updateChildStatus(child.getId(), "Dropped Off");
                    t.setText("Picked Up");
                }
            }
        });
        holder.red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ServerHelper helper = LoginActivity.getServerHelper();
                Log.d(TAG,"Red pressed");
                TextView t = (TextView)arg0.findViewById(R.id.blue);
                if(t.getText().equals("Leaving")) {
                    helper.updateChildStatus(child.getId(), "Left");
                    t.setText("Lost");
                }
                arg0.findViewById(R.id.red).setVisibility(GONE);
                arg0.findViewById(R.id.green).setVisibility(VISIBLE);
            }
        });
        holder.green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ServerHelper helper = LoginActivity.getServerHelper();
                Log.d(TAG,"Green pressed");
                arg0.findViewById(R.id.green).setVisibility(GONE);
                arg0.findViewById(R.id.red).setVisibility(VISIBLE);
                helper.updateChildStatus(child.getId(), "Found");
            }
        });
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }
}