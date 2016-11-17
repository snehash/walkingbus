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
import android.support.v4.view.MenuItemCompat;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
    private static SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    // TODO: clean up use of static dropdown selector
    private static String groupId = "-1";

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
        getMenuInflater().inflate(R.menu.menu_group, menu);
        MenuItem spinnerHolder = menu.findItem(R.id.spinner);
        Spinner groupSpinner = (Spinner) MenuItemCompat.getActionView(spinnerHolder);
        String contents[] = {};
        ArrayList<String> groupIds = new ArrayList<>();

        try {
            JSONObject data = LoginActivity.getServerHelper().getParentData();
            JSONArray jsonGroups = data.getJSONArray("groups");
            for(int i = 0; i < jsonGroups.length(); i++) {
                String id = jsonGroups.getJSONObject(i).getString("id");
                groupIds.add(id);
                if(i == 0)
                    groupId = id;
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, groupIds);

        Log.d(TAG, "GroupAdapter count: " + groupAdapter.getCount());

        groupSpinner.setAdapter(groupAdapter);

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                groupId = (String)adapterView.getItemAtPosition(i);
                mSectionsPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
            final RecyclerView am = (RecyclerView) rootView.findViewById(R.id.am_group);
            final RecyclerView pm = (RecyclerView) rootView.findViewById(R.id.pm_group);

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
            String myName = "";
            ArrayList<ArrayList<String>> groupTimeslotIds = new ArrayList<ArrayList<String>>();
            int jsonGroupArrayIndex = -1;

            try {
                myName = data.getString("name");
                JSONArray jsonGroups = data.getJSONArray("groups");

                // just get first group for now, will use dropdown selector later
                JSONObject jsonGroup = new JSONObject();
                // initialize 10 timeslots id spaces for each group

                Log.d(TAG, "GroupActivity groupId: " + GroupActivity.groupId);

                for(int i = 0; i < jsonGroups.length(); i++) {
                    JSONObject g = jsonGroups.getJSONObject(i);
                    if(g.getString("id").equals(GroupActivity.groupId)) {
                        Log.d(TAG, "ID matches");
                        jsonGroup = g;
                        jsonGroupArrayIndex = i;
                    }
                    groupTimeslotIds.add(new ArrayList<String>());
                    for(int j = 0; j < 10; j++) {
                        groupTimeslotIds.get(i).add("");
                    }
                }

                if(jsonGroupArrayIndex == -1) {
                    jsonGroupArrayIndex = 0;
                    jsonGroup = jsonGroups.getJSONObject(0);
                }

                groupId = jsonGroup.getString("id");
                JSONArray jsonTimeslots = jsonGroup.getJSONArray("timeslots");
                for(int i = 0; i < jsonTimeslots.length(); i++) {
                    JSONObject jsonTimeslot = jsonTimeslots.getJSONObject(i);
                    JSONObject jsonChaperone = jsonTimeslot.getJSONObject("chaperone");
                    String chaperoneName = jsonChaperone.getString("name");
                    String timeslotId = jsonTimeslot.getString("id");
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
                    groupTimeslotIds.get(jsonGroupArrayIndex).set(groupIndex, timeslotId);

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

            final ArrayList<Child> myChildren = new ArrayList<Child>();
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

            int timeslotIdBaseIndex = -1;

            switch(getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    timeslot = "MONDAY";
                    timeslotIdBaseIndex = 0;
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
                    timeslotIdBaseIndex = 2;
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
                    timeslotIdBaseIndex = 4;
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
                    timeslotIdBaseIndex = 6;
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
                    timeslotIdBaseIndex = 8;
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

            // TODO: Populate with correct ID

            // index by Mon AM, Mon PM, Tues AM, etc...
            String amTimeslotId = groupTimeslotIds.get(jsonGroupArrayIndex).get(timeslotIdBaseIndex);
            String pmTimeslotId = groupTimeslotIds.get(jsonGroupArrayIndex).get(timeslotIdBaseIndex + 1);

            Log.d(TAG, "AM Timeslot ID: " + amTimeslotId);
            Log.d(TAG, "PM Timeslot ID: " + pmTimeslotId);

            amAddChild.setOnClickListener(new View.OnClickListener() {
                String groupId;
                String timeslotId;
                ArrayList<Child> children;
                ArrayList<Child> myChildren;
                @Override
                public void onClick(View view) {
                    // TODO: Retrieve which child to add

                    // Check if group exists (chaperone must have claimed it)

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    List<String> childNames = new ArrayList<String>();
                    ArrayList<String> childIds = new ArrayList<String>();
                    for(Child c: myChildren) {
                        childNames.add(c.getName());
                        childIds.add(c.getId());
                    }
                    Log.d(TAG, "AM add child clicked for: " + timeslotId);
                    CharSequence[] cs = childNames.toArray(new CharSequence[childNames.size()]);
                    if(!timeslotId.equals("")) {
                        builder.setTitle("Select Child")
                                .setItems(cs, new DialogInterface.OnClickListener() {
                                    ArrayList<Child> myChildren;
                                    ArrayList<Child> children;
                                    String timeslotId;
                                    String groupId;
                                    View view;

                                    public void onClick(DialogInterface dialog, int which) {
                                        // The 'which' argument contains the index position
                                        // of the selected item
                                        Child child = myChildren.get(which);
                                        Log.d(TAG, "Add Child ID: " + child.getId());
                                        LoginActivity.getServerHelper().addChildToGroup(child.getId(), groupId, timeslotId);
                                        children.add(new Child(child));
                                        Log.d(TAG, children.get(0).getName());
                                        amChildAdapter = new GroupAdapter(children, view.getContext());
                                        amChildAdapter.notifyDataSetChanged();
                                        am.setAdapter(amChildAdapter);
                                        mSectionsPagerAdapter.notifyDataSetChanged();
                                    }

                                    private DialogInterface.OnClickListener init(String groupId, String timeslotId, ArrayList<Child> myChildren, ArrayList<Child> children, View view) {
                                        this.groupId = groupId;
                                        this.timeslotId = timeslotId;
                                        this.myChildren = new ArrayList<Child>();
                                        this.myChildren.addAll(myChildren);
                                        this.children = new ArrayList<Child>();
                                        this.children.addAll(children);
                                        this.view = view;
                                        return this;
                                    }

                                }.init(groupId, timeslotId, myChildren, children, view));
                    } else {
                        // timeslot is unclaimed
                        builder.setTitle("Unable to Add Child");
                        builder.setMessage("A chaperone must claim this timeslot before a child can be added.");
                    }
                    AlertDialog addChildDialog = builder.create();
                    addChildDialog.show();
                }

                private View.OnClickListener init(String groupId, String timeslot, ArrayList<Child> myChildren, ArrayList<Child> children) {
                    this.groupId = groupId;
                    this.timeslotId = timeslot;
                    this.myChildren = new ArrayList<Child>();
                    this.myChildren.addAll(myChildren);
                    this.children = new ArrayList<Child>();
                    this.children.addAll(children);
                    return this;
                }
            }.init(groupId, amTimeslotId, myChildren, amChildren));

            pmAddChild.setOnClickListener(new View.OnClickListener() {
                String groupId;
                String timeslotId;
                ArrayList<Child> myChildren;
                ArrayList<Child> children;
                @Override
                public void onClick(View view) {
                    // TODO: Retrieve which child to add

                    // Check if group exists (chaperone must have claimed it)

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    List<String> childNames = new ArrayList<String>();
                    ArrayList<String> childIds = new ArrayList<String>();
                    for(Child c: myChildren) {
                        childNames.add(c.getName());
                        childIds.add(c.getId());
                    }
                    Log.d(TAG, "AM add child clicked for: " + timeslotId);
                    CharSequence[] cs = childNames.toArray(new CharSequence[childNames.size()]);
                    if(!timeslotId.equals("")) {
                        builder.setTitle("Select Child")
                                .setItems(cs, new DialogInterface.OnClickListener() {
                                    ArrayList<Child> myChildren;
                                    ArrayList<Child> children;
                                    String timeslotId;
                                    String groupId;
                                    View view;

                                    public void onClick(DialogInterface dialog, int which) {
                                        // The 'which' argument contains the index position
                                        // of the selected item
                                        Child child = myChildren.get(which);
                                        Log.d(TAG, "Add Child ID: " + child.getId());
                                        LoginActivity.getServerHelper().addChildToGroup(child.getId(), groupId, timeslotId);
                                        children.add(new Child(child));
                                        pmChildAdapter = new GroupAdapter(children, view.getContext());
                                        pmChildAdapter.notifyDataSetChanged();
                                        pm.setAdapter(pmChildAdapter);
                                        mSectionsPagerAdapter.notifyDataSetChanged();
                                    }

                                    private DialogInterface.OnClickListener init(String groupId, String timeslotId, ArrayList<Child> myChildren, ArrayList<Child> children, View view) {
                                        this.groupId = groupId;
                                        this.timeslotId = timeslotId;
                                        this.myChildren = new ArrayList<Child>();
                                        this.myChildren.addAll(myChildren);
                                        this.children = new ArrayList<Child>();
                                        this.children.addAll(children);
                                        this.view = view;
                                        return this;
                                    }

                                }.init(groupId, timeslotId, myChildren, children, view));
                    } else {
                        // timeslot is unclaimed
                        builder.setTitle("Unable to Add Child");
                        builder.setMessage("A chaperone must claim this timeslot before a child can be added.");
                    }
                    AlertDialog addChildDialog = builder.create();
                    addChildDialog.show();
                }

                private View.OnClickListener init(String groupId, String timeslot, ArrayList<Child> myChildren, ArrayList<Child> children) {
                    this.groupId = groupId;
                    this.timeslotId = timeslot;
                    this.myChildren = new ArrayList<Child>();
                    this.myChildren.addAll(myChildren);
                    this.children = new ArrayList<Child>();
                    this.children.addAll(children);
                    return this;
                }
            }.init(groupId, pmTimeslotId, myChildren, pmChildren));

            Log.d(TAG, "amChaptext: " + amChapText.getText());
            Log.d(TAG, "pmChaptext: " + pmChapText.getText());

            if(amChapText.getText().equals("Tap to claim")) {
                Log.d(TAG, "Adding amChaperone listener");
                // no AM chaperone
                amChapText.setOnClickListener(new View.OnClickListener() {
                    String groupId;
                    String timeslot;
                    String name;
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "Chaperone is claiming AM");
                        LoginActivity.getServerHelper().addChaperone(groupId, timeslot);
                        ((TextView) view).setText(name);
                        mSectionsPagerAdapter.notifyDataSetChanged();
                        view.setOnClickListener(null);
                    }

                    private View.OnClickListener init(String groupId, String timeslot, String name) {
                        this.groupId = groupId;
                        this.timeslot = timeslot;
                        this.name = name;
                        return this;
                    }
                }.init(groupId, timeslot + "_AM", myName));

            }
            if(pmChapText.getText().equals("Tap to claim")) {
                Log.d(TAG, "Adding pmChaperone listener");
                // no PM chaperone
                pmChapText.setOnClickListener(new View.OnClickListener() {
                    String groupId;
                    String timeslot;
                    String name;
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "Chaperone is claiming PM");
                        LoginActivity.getServerHelper().addChaperone(groupId, timeslot);
                        ((TextView) view).setText(name);
                        mSectionsPagerAdapter.notifyDataSetChanged();
                        view.setOnClickListener(null);
                    }

                    private View.OnClickListener init(String groupId, String timeslot, String name) {
                        this.groupId = groupId;
                        this.timeslot = timeslot;
                        this.name = name;
                        return this;
                    }
                }.init(groupId, timeslot + "_PM", myName));
            }

            amChildAdapter = new GroupAdapter(amChildren, this.getContext());
            pmChildAdapter = new GroupAdapter(pmChildren, this.getContext());

            am.setAdapter(amChildAdapter);
            pm.setAdapter(pmChildAdapter);

            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            am.setLayoutManager(llm);
            pm.setLayoutManager(new LinearLayoutManager(getActivity()));

            amChildAdapter.notifyDataSetChanged();
            pmChildAdapter.notifyDataSetChanged();
            mSectionsPagerAdapter.notifyDataSetChanged();

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
