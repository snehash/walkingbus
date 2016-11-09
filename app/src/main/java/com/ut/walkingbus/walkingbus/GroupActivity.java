package com.ut.walkingbus.walkingbus;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "GroupActivity";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.group_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_parent) {
            Intent intent = new Intent(this, ParentActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_chaperone) {
            Intent intent = new Intent(this, ChaperoneActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_group) {
            this.recreate();
        } else if (id == R.id.nav_create_group) {
            startActivity(new Intent(GroupActivity.this, AddGroupActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private GroupAdapter amChildAdapter;
        private GroupAdapter pmChildAdapter;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            Log.d(TAG, "Creating new instance: " + sectionNumber);
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_group, container, false);
            RecyclerView am = (RecyclerView) rootView.findViewById(R.id.am_group);
            RecyclerView pm = (RecyclerView) rootView.findViewById(R.id.pm_group);

            View amChaperone =  rootView.findViewById(R.id.am_chaperone);
            View pmChaperone =  rootView.findViewById(R.id.pm_chaperone);
            TextView amChapText = (TextView) amChaperone.findViewById(R.id.name);
            TextView pmChapText = (TextView) pmChaperone.findViewById(R.id.name);

            am.setHasFixedSize(true);
            pm.setHasFixedSize(true);

            LoginActivity.getServerHelper().setContext(this.getContext());
            JSONObject data = LoginActivity.getServerHelper().getParentData();

            // goes from Mon AM, Mon PM, Tues AM, etc in order
            ArrayList<ArrayList<Child>> groupChildren = new ArrayList<ArrayList<Child>>();
            for(int i = 0; i < 10; i++) {
                groupChildren.add(new ArrayList<Child>());
            }

            // initialize chaperone name for each timeslot
            ArrayList<String> chaperoneNames = new ArrayList<String>();
            for(int i = 0; i < 10; i++) {
                chaperoneNames.add("");
            }

            String groupId = "-1";

            try {
                JSONArray jsonGroups = data.getJSONArray("groups");
                // just get first group for now, will use dropdown selector later
                JSONObject jsonGroup = jsonGroups.getJSONObject(0);
                groupId = jsonGroup.getString("id");
                JSONArray jsonTimeslots = jsonGroup.getJSONArray("timeslots");
                for(int i = 0; i < jsonTimeslots.length(); i++) {
                    JSONObject jsonTimeslot = jsonTimeslots.getJSONObject(i);
                    JSONObject jsonChaperone = jsonTimeslot.getJSONObject("chaperone");
                    String chaperoneName = jsonChaperone.getString("name");
                    String time = jsonTimeslot.getString("time");
                    int day = getDay(time.substring(0, time.indexOf("_")));
                    int groupIndex = 0;

                    boolean isPm = time.contains("PM");
                    if(isPm) {
                        groupIndex++;
                    }
                    groupIndex += day*2;
                    Log.d(TAG, "Children Array Index: " + groupIndex);
                    chaperoneNames.set(groupIndex, chaperoneName);

                    JSONArray children = jsonTimeslot.getJSONArray("children");
                    for(int j = 0; j < children.length(); j++) {
                        JSONObject jsonChild = children.getJSONObject(j);
                        String name = jsonChild.getString("name");
                        String id = jsonChild.getString("id");
                        groupChildren.get(groupIndex).add(new Child(id, name, null, null, null, null));

                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }

            ArrayList<Child> amChildren = new ArrayList<Child>();
            ArrayList<Child> pmChildren = new ArrayList<Child>();

            String timeslot = "";

            Button amAddChild = (Button) rootView.findViewById(R.id.am_add_child);
            Button pmAddChild = (Button) rootView.findViewById(R.id.pm_add_child);

            ArrayList<Child> myChildren = new ArrayList<Child>();
            try {
                JSONArray jsonMyChildren = data.getJSONArray("children");
                for(int i = 0; i < jsonMyChildren.length(); i++) {
                    JSONObject child = jsonMyChildren.getJSONObject(i);
                    String id = child.getString("id");
                    String name = child.getString("name");
                    myChildren.add(new Child(id, name, null, null, null, null));
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }

            switch(getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    timeslot = "MONDAY";
                    if(!chaperoneNames.get(0).equals("")) {
                        amChapText.setText(chaperoneNames.get(0));
                    }
                    if(!chaperoneNames.get(1).equals("")) {
                        pmChapText.setText(chaperoneNames.get(1));
                    }
                    amChildren = groupChildren.get(0);
                    pmChildren = groupChildren.get(1);
                    break;
                case 2:
                    timeslot = "TUESDAY";
                    if(!chaperoneNames.get(2).equals("")) {
                        amChapText.setText(chaperoneNames.get(2));
                    }
                    if(!chaperoneNames.get(3).equals("")) {
                        pmChapText.setText(chaperoneNames.get(3));
                    }
                    amChildren = groupChildren.get(2);
                    pmChildren = groupChildren.get(3);
                    break;
                case 3:
                    timeslot = "WEDNESDAY";
                    if(!chaperoneNames.get(4).equals("")) {
                        amChapText.setText(chaperoneNames.get(4));
                    }
                    if(!chaperoneNames.get(5).equals("")) {
                        pmChapText.setText(chaperoneNames.get(5));
                    }
                    amChildren = groupChildren.get(4);
                    pmChildren = groupChildren.get(5);
                    break;
                case 4:
                    timeslot = "THURSDAY";
                    if(!chaperoneNames.get(6).equals("")) {
                        amChapText.setText(chaperoneNames.get(6));
                    }
                    if(!chaperoneNames.get(7).equals("")) {
                        pmChapText.setText(chaperoneNames.get(7));
                    }
                    amChildren = groupChildren.get(6);
                    pmChildren = groupChildren.get(7);
                    break;
                case 5:
                    timeslot = "FRIDAY";
                    if(!chaperoneNames.get(8).equals("")) {
                        amChapText.setText(chaperoneNames.get(8));
                    }
                    if(!chaperoneNames.get(9).equals("")) {
                        pmChapText.setText(chaperoneNames.get(9));
                    }
                    amChildren = groupChildren.get(8);
                    pmChildren = groupChildren.get(9);
                    break;
            }

            Log.d(TAG, "Group ID: " + groupId);
            Log.d(TAG, "Timeslot: " + timeslot);

            amAddChild.setOnClickListener(new View.OnClickListener() {
                String groupId;
                String timeslot;
                ArrayList<Child> children;
                @Override
                public void onClick(View view) {
                    // TODO: Retrieve which child to add
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    List<String> childNames = new ArrayList<String>();
                    ArrayList<String> childIds = new ArrayList<String>();
                    for(Child c: children) {
                        childNames.add(c.getName());
                        childIds.add(c.getId());
                    }
                    Log.d(TAG, "AM add child clicked for: " + timeslot);
                    CharSequence[] cs = childNames.toArray(new CharSequence[childNames.size()]);
                    builder.setTitle("Select Child")
                            .setItems(cs, new DialogInterface.OnClickListener() {
                                ArrayList<String> childIds;
                                String timeslot;
                                String groupId;
                                public void onClick(DialogInterface dialog, int which) {
                                    // The 'which' argument contains the index position
                                    // of the selected item
                                    String childId = childIds.get(which);
                                    Log.d(TAG, "Add Child ID: " + childId);
                                    LoginActivity.getServerHelper().addChildToGroup(childId, groupId, timeslot);
                                }
                                private DialogInterface.OnClickListener init(String groupId, String timeslot, ArrayList<String> childIds) {
                                    this.groupId = groupId;
                                    this.timeslot = timeslot;
                                    this.childIds = new ArrayList<String>();
                                    this.childIds.addAll(childIds);
                                    return this;
                                }

                            }.init(groupId, timeslot, childIds));
                    AlertDialog addChildDialog = builder.create();
                    addChildDialog.show();
                }

                private View.OnClickListener init(String groupId, String timeslot, ArrayList<Child> children) {
                    this.groupId = groupId;
                    this.timeslot = timeslot;
                    this.children = new ArrayList<Child>();
                    this.children.addAll(children);
                    return this;
                }
            }.init(groupId, timeslot + "_AM", myChildren));

            pmAddChild.setOnClickListener(new View.OnClickListener() {
                String groupId;
                String timeslot;
                ArrayList<Child> children;
                @Override
                public void onClick(View view) {
                    // TODO: Retrieve which child to add
                    Log.d(TAG, "PM add child clicked for: " + timeslot);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    List<String> childNames = new ArrayList<String>();
                    ArrayList<String> childIds = new ArrayList<String>();
                    for(Child c: children) {
                        childNames.add(c.getName());
                        childIds.add(c.getId());
                    }
                    CharSequence[] cs = childNames.toArray(new CharSequence[childNames.size()]);
                    builder.setTitle("Select Child")
                            .setItems(cs, new DialogInterface.OnClickListener() {
                                ArrayList<String> childIds;
                                String timeslot;
                                String groupId;
                                public void onClick(DialogInterface dialog, int which) {
                                    // The 'which' argument contains the index position
                                    // of the selected item
                                    Log.d(TAG, "Builder onclick PM");
                                    String childId = childIds.get(which);
                                    Log.d(TAG, "Add Child ID: " + childId);
                                    LoginActivity.getServerHelper().addChildToGroup(childId, groupId, timeslot);
                                }
                                private DialogInterface.OnClickListener init(String groupId, String timeslot, ArrayList<String> childIds) {
                                    this.groupId = groupId;
                                    this.timeslot = timeslot;
                                    this.childIds = new ArrayList<String>();
                                    this.childIds.addAll(childIds);
                                    return this;
                                }

                            }.init(groupId, timeslot, childIds));
                    AlertDialog addChildDialog = builder.create();
                    addChildDialog.show();

                }

                private View.OnClickListener init(String groupId, String timeslot, ArrayList<Child> children) {
                    this.groupId = groupId;
                    this.timeslot = timeslot;
                    this.children = new ArrayList<Child>();
                    this.children.addAll(children);
                    return this;
                }
            }.init(groupId, timeslot + "_PM", myChildren));

            Log.d(TAG, "amChaptext: " + amChapText.getText());
            Log.d(TAG, "pmChaptext: " + pmChapText.getText());

            if(!amChapText.getText().equals("Tap to claim")) {
                Log.d(TAG, "Adding amChaperone listener");
                // no AM chaperone
                amChapText.setOnClickListener(new View.OnClickListener() {
                    String groupId;
                    String timeslot;
                    @Override
                    public void onClick(View view) {
                        LoginActivity.getServerHelper().addChaperone(groupId, timeslot);
                    }

                    private View.OnClickListener init(String groupId, String timeslot) {
                        this.groupId = groupId;
                        this.timeslot = timeslot;
                        return this;
                    }
                }.init(groupId, timeslot + "_AM"));

            }
            if(!pmChapText.getText().equals("Tap to claim")) {
                Log.d(TAG, "Adding pmChaperone listener");
                // no PM chaperone
                pmChapText.setOnClickListener(new View.OnClickListener() {
                    String groupId;
                    String timeslot;
                    @Override
                    public void onClick(View view) {
                        LoginActivity.getServerHelper().addChaperone(groupId, timeslot);
                    }

                    private View.OnClickListener init(String groupId, String timeslot) {
                        this.groupId = groupId;
                        this.timeslot = timeslot;
                        return this;
                    }
                }.init(groupId, timeslot + "_PM"));
            }

            amChildAdapter = new GroupAdapter(amChildren, this.getContext());
            pmChildAdapter = new GroupAdapter(pmChildren, this.getContext());

            am.setAdapter(amChildAdapter);
            pm.setAdapter(pmChildAdapter);

            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            am.setLayoutManager(llm);
            pm.setLayoutManager(new LinearLayoutManager(getActivity()));
            return rootView;
        }
    }

    private static int getDay(String day) {
        day = day.toUpperCase();
        switch(day) {
            case "MONDAY":
                return 0;
            case "TUESDAY":
                return 1;
            case "WEDNESDAY":
                return 2;
            case "THURSDAY":
                return 3;
            case "FRIDAY":
                return 4;
            default:
                return -1;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Mon";
                case 1:
                    return "Tues";
                case 2:
                    return "Wed";
                case 3:
                    return "Thurs";
                case 4:
                    return "Fri";
            }
            return null;
        }
    }
}
