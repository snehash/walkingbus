package com.ut.walkingbus.walkingbus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AddGroupActivity extends AppCompatActivity {
    private static final String TAG = "AddGroupActivity";

    private String school;
    ServerHelper mServerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button submit = (Button) findViewById(R.id.add_submit);
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String name = ((TextView) findViewById(R.id.add_name)).getText().toString();

                Log.d(TAG, "adding group");
                LoginActivity.getServerHelper().addGroup();
                AddGroupActivity.super.onBackPressed();
            }
        });

    }

}
