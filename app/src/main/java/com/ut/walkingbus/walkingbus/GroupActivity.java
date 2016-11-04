package com.ut.walkingbus.walkingbus;

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
import org.json.JSONObject;

import java.util.ArrayList;

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
            Intent intent = new Intent(this, AddGroupActivity.class);
            startActivity(intent);
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
            View pmChaperone =  rootView.findViewById(R.id.am_chaperone);
            TextView pmChapText = (TextView) amChaperone.findViewById(R.id.name);
            TextView amChapText = (TextView) pmChaperone.findViewById(R.id.name);

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

            amAddChild.setOnClickListener(new View.OnClickListener() {
                String groupId;
                String timeslot;
                @Override
                public void onClick(View view) {
                    LoginActivity.getServerHelper().addChildToGroup("1", "1", "2");
                }

                private View.OnClickListener init(String groupId, String timeslot) {
                    this.groupId = groupId;
                    this.timeslot = timeslot;
                    return this;
                }
            }.init(groupId, timeslot + "_PM"));

            Log.d(TAG, "Section Number: " + getArguments().getInt(ARG_SECTION_NUMBER));
            Log.d(TAG, "Child Group 0: " + groupChildren.get(0).get(0).getName());

            groupChildren.get(8).add(new Child("Placeholder", null, null, null, null, null));

            switch(getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    timeslot = "MONDAY";
                    amChildren = groupChildren.get(0);
                    pmChildren = groupChildren.get(1);
                    break;
                case 2:
                    timeslot = "TUESDAY";
                    amChildren = groupChildren.get(2);
                    pmChildren = groupChildren.get(3);
                    break;
                case 3:
                    timeslot = "WEDNESDAY";
                    amChildren = groupChildren.get(4);
                    pmChildren = groupChildren.get(5);
                    break;
                case 4:
                    timeslot = "THURSDAY";
                    amChildren = groupChildren.get(6);
                    pmChildren = groupChildren.get(7);
                    break;
                case 5:
                    timeslot = "FRIDAY";
                    amChildren = groupChildren.get(8);
                    pmChildren = groupChildren.get(9);
                    break;
            }

            Log.d(TAG, "Group ID: " + groupId);
            Log.d(TAG, "Timeslot: " + timeslot);

            if(amChildren.isEmpty()) {
                Log.d(TAG, "Adding amChildren listener");
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
            if(pmChildren.isEmpty()) {
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
