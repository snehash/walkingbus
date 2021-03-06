package com.ut.walkingbus.walkingbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String ID= "ID";
    private static final String TAG = "ParentActivity";

    RecyclerView mRecyclerView;
    ParentAdapter mChildAdapter;
    ServerHelper mServerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Parent");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParentActivity.this, AddChildActivity.class));
            }
        });

        mServerHelper = LoginActivity.getServerHelper();
        mServerHelper.setContext(this);

        //get the id
        if(mServerHelper.getNeedToRegister()) {
            mServerHelper.register();
        }

        JSONObject data = mServerHelper.getParentData();
        String username = "";
        String email = "";

        ArrayList<Child> children = new ArrayList<Child>();
        try {
            username = data.getString("name");
            email = data.getString("email");
            JSONArray jsonChildren = data.getJSONArray("children");
            for(int i = 0; i < jsonChildren.length(); i++) {
                JSONObject jsonChild = jsonChildren.getJSONObject(i);
                String id = jsonChild.getString("id");
                String name = jsonChild.getString("name");
                String status = jsonChild.getString("status");
                children.add(new Child(id, name, null, status, null, null));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "children should be added");

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView = (RecyclerView) findViewById(R.id.childList);
        mRecyclerView.setLayoutManager(llm);
        mChildAdapter = new ParentAdapter(children, this);

        mRecyclerView.setAdapter(mChildAdapter);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        ((TextView)header.findViewById(R.id.nav_username)).setText(username);
        ((TextView)header.findViewById(R.id.nav_email)).setText(email);

        //server stuff
        // mServerHelper = LoginActivity.getServerHelper();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Resuming");
        mServerHelper = LoginActivity.getServerHelper();
        mServerHelper.setContext(this);

        //get the id
        if(mServerHelper.getNeedToRegister()) {
            mServerHelper.register();
        }

        JSONObject data = mServerHelper.getParentData();

        ArrayList<Child> children = new ArrayList<Child>();
        try {
            JSONArray jsonChildren = data.getJSONArray("children");
            for(int i = 0; i < jsonChildren.length(); i++) {
                JSONObject jsonChild = jsonChildren.getJSONObject(i);
                String id = jsonChild.getString("id");
                String name = jsonChild.getString("name");
                String status = jsonChild.getString("status");
                children.add(new Child(id, name, null, status, null, null));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "children should be added");

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView = (RecyclerView) findViewById(R.id.childList);
        mRecyclerView.setLayoutManager(llm);
        mChildAdapter = new ParentAdapter(children, this);

        mRecyclerView.setAdapter(mChildAdapter);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_parent) {
            this.recreate();
        } else if (id == R.id.nav_chaperone) {
            Intent intent = new Intent(this, ChaperoneActivity.class);
            startActivity(intent);
        } else if(id == R.id.nav_sign_out) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_group) {
            Intent intent = new Intent(this, GroupActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_create_group) {
            startActivity(new Intent(ParentActivity.this, AddGroupActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
