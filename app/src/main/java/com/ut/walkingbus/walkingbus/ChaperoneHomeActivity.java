package com.ut.walkingbus.walkingbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class ChaperoneHomeActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ChaperoneAdapter mToSchoolAdapter;
    private ChaperoneAdapter mFromSchoolAdapter;
    private Button mToSchoolButton;
    private Button mFromSchoolButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chaperone_home_activity);
        mRecyclerView = (RecyclerView) findViewById(R.id.childList);
        mFromSchoolAdapter = new ChaperoneAdapter(new ArrayList<Child>(), this);
        mToSchoolAdapter = new ChaperoneAdapter(new ArrayList<Child>(), this);
        mRecyclerView.setAdapter(mToSchoolAdapter);
        mToSchoolButton = (Button) findViewById(R.id.toSchool);
        mFromSchoolButton = (Button) findViewById(R.id.fromSchool);
        mFromSchoolButton.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     mToSchoolButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                     mFromSchoolButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                                                     mRecyclerView.setAdapter(mFromSchoolAdapter);
                                                 }
                                             }
        );

        mToSchoolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToSchoolButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mFromSchoolButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                mRecyclerView.setAdapter(mFromSchoolAdapter);
            }
        });
    }
}
