package com.ut.walkingbus.walkingbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class ParentHome extends AppCompatActivity {
    private static final String TAG = "ParentHome";
    public static final String ID= "ID";
    public static final String SERVER_HELPER ="ServerHelper";
    RecyclerView mRecyclerView;
    Button mToSchoolButton;
    Button mFromSchoolButton;
    ParentAdapter mFromSchoolAdapter;
    ParentAdapter mToSchoolAdapter;
    ServerHelper mServerHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_home_activity);
        Intent intent = getIntent();

        //do UI stuff
        mRecyclerView = (RecyclerView) findViewById(R.id.childList);
        mFromSchoolAdapter = new ParentAdapter(new ArrayList<Child>(), this);
        mToSchoolAdapter = new ParentAdapter(new ArrayList<Child>(), this);
        mRecyclerView.setAdapter(mToSchoolAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                mToSchoolButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                mFromSchoolButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mRecyclerView.setAdapter(mFromSchoolAdapter);
            }
        });

        //server stuff
        mServerHelper = LoginActivity.getServerHelper();
        mServerHelper.setContext(this); //call this line every time to change activities

        //get the id
        if(mServerHelper.needToRegister) {
            mServerHelper.register();
        }

        mServerHelper.getParentData();
    }


}
