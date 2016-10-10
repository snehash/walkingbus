package com.ut.walkingbus.walkingbus;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class ParentHome extends AppCompatActivity {
    RecyclerView mRecyclerView;
    Button mToSchoolButton;
    Button mFromSchoolButton;
    ParentAdapter mFromSchoolAdapter;
    ParentAdapter mToSchoolAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_home_activity);
        mRecyclerView = (RecyclerView) findViewById(R.id.childList);
        mFromSchoolAdapter = new ParentAdapter(new ArrayList<Child>(), this);
        mToSchoolAdapter = new ParentAdapter(new ArrayList<Child>(), this);
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
