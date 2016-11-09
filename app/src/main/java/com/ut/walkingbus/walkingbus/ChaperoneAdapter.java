package com.ut.walkingbus.walkingbus;


import android.content.Context;
import android.support.v4.content.ContextCompat;
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
        private Button alert, action;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            status = (TextView) view.findViewById(R.id.status);
            picture = (ImageView) view.findViewById(R.id.child_image);
            alert = (Button) view.findViewById(R.id.alert_button);
            action = (Button) view.findViewById(R.id.action_button);
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
        // TODO: what do we show to the chaperone in scenarios where they shouldn't interact

        final TextView statusView = holder.status;
        final Button action = holder.action;
        final Button alert = holder.alert;

        String childStatus = child.getStatus();
        if(childStatus.equals(mContext.getString(R.string.status_waiting))) {
            action.setVisibility(VISIBLE);
            alert.setVisibility(VISIBLE);

            alert.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
            action.setBackgroundColor(ContextCompat.getColor(mContext, R.color.blue));

            action.setText("Picked Up");
            alert.setText("Leaving");
        } else
        if(childStatus.equals(mContext.getString(R.string.status_lost))) {
            alert.setVisibility(VISIBLE);
            action.setVisibility(GONE);

            alert.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green));

            alert.setText("Found");
        } else
        if(childStatus.equals(mContext.getString(R.string.status_picked_up))) {
            action.setVisibility(VISIBLE);
            alert.setVisibility(VISIBLE);

            alert.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
            action.setBackgroundColor(ContextCompat.getColor(mContext, R.color.blue));

            action.setText("Picked Up");
            alert.setText("Leaving");
        } else
        if(childStatus.equals(mContext.getString(R.string.status_dropped_off))) {
            action.setVisibility(GONE);
            alert.setVisibility(GONE);
        } else
        if(childStatus.equals(mContext.getString(R.string.status_left))) {
            action.setVisibility(VISIBLE);
            alert.setVisibility(GONE);

            action.setBackgroundColor(ContextCompat.getColor(mContext, R.color.blue));

            action.setText("Picked Up");
        } else {
            Log.d(TAG, "Unknown status");
        }

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ServerHelper helper = LoginActivity.getServerHelper();
                Log.d(TAG,"Action pressed");
                Button action = (Button)arg0.findViewById(R.id.action_button);
                Context c = arg0.getContext();
                String status = child.getStatus();
                Log.d(TAG, "Status: " + status);

                // TODO: server transition from dropped off into waiting automatically

                // hit action while child is picked up -> child is dropped off
                if(status.equals(c.getString(R.string.status_picked_up))) {
                    helper.updateChildStatus(child.getId(), c.getString(R.string.status_dropped_off));
                    child.setStatus(c.getString(R.string.status_dropped_off));
                    // action.setVisibility(GONE);
                    action.setText("Picked Up");
                } else
                // hit action while child is waiting -> child is picked up
                if(status.equals(c.getString(R.string.status_waiting))) {
                    helper.updateChildStatus(child.getId(), c.getString(R.string.status_picked_up));
                    child.setStatus(c.getString(R.string.status_picked_up));
                    action.setText("Dropped Off");
                } else
                // hit action while child is left -> child is picked up
                if(status.equals(c.getString(R.string.status_left))) {
                    alert.setVisibility(VISIBLE);
                    alert.setText("Lost");
                    helper.updateChildStatus(child.getId(), c.getString(R.string.status_picked_up));
                    child.setStatus(c.getString(R.string.status_picked_up));
                    action.setText("Dropped Off");
                } else
                // hit action while child is dropped off -> re-pick up child (for accidental press)
                if(status.equals("Dropped Off")) {
                    // allow un-dropping off
                    helper.updateChildStatus(child.getId(), c.getString(R.string.status_picked_up));
                    alert.setVisibility(VISIBLE);
                    alert.setText("Lost");
                    child.setStatus(c.getString(R.string.status_picked_up));
                    action.setText("Dropped Off");
                } else
                {
                    // status doesn't fall under any known value
                    Log.d(TAG, "Unknown status: " + status);
                }
                // Update any changes to the status to the view
                statusView.setText(child.getStatus());
            }
        });
        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ServerHelper helper = LoginActivity.getServerHelper();
                Context c = arg0.getContext();
                Log.d(TAG,"Alert pressed");
                Button t = (Button)arg0.findViewById(R.id.alert_button);

                // status needs to be changed locally, impossible with final child
                String status = child.getStatus();
                Log.d(TAG, "Status: " + status);

                // hit alert while child is waiting -> child has been left
                if(status.equals(c.getString(R.string.status_waiting))) {
                    helper.updateChildStatus(child.getId(), c.getString(R.string.status_left));
                    child.setStatus(c.getString(R.string.status_waiting));
                    t.setVisibility(GONE);
                } else
                // hit alert while child is en route -> child has been lost
                if(status.equals(c.getString(R.string.status_picked_up))) {
                    helper.updateChildStatus(child.getId(), c.getString(R.string.status_lost));
                    child.setStatus(c.getString(R.string.status_lost));
                    action.setVisibility(GONE);
                    t.setBackgroundColor(ContextCompat.getColor(c, R.color.green));
                    t.setText("Found");
                } else
                // hit alert while child is lost -> child has been found
                if(status.equals(c.getString(R.string.status_lost))) {
                    helper.updateChildStatus(child.getId(), c.getString(R.string.status_picked_up));
                    child.setStatus(c.getString(R.string.status_picked_up));
                    action.setVisibility(VISIBLE);
                    action.setText("Dropped Off");
                    t.setBackgroundColor(ContextCompat.getColor(c, R.color.red));
                    t.setText("Lost");
                } else {
                    // status doesn't fall under any known value
                    Log.d(TAG, "Unknown status: " + status);
                }
                // Update any changes to the status to the view
                statusView.setText(child.getStatus());
            }
        });
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }
}