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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class ChaperoneActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "ChaperoneActivity";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private ChaperoneActivity.SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    ServerHelper mServerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chaperone);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mServerHelper = LoginActivity.getServerHelper();
        mServerHelper.setContext(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new ChaperoneActivity.SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.chaperone_tabs);
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
        private ChaperoneAdapter mChildAdapter;
        private static final String ARG_TO_SCHOOL = "to_school";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ChaperoneActivity.PlaceholderFragment newInstance(boolean toSchool) {
            ChaperoneActivity.PlaceholderFragment fragment = new ChaperoneActivity.PlaceholderFragment();
            Bundle args = new Bundle();
            args.putBoolean(ARG_TO_SCHOOL, toSchool);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
            rv.setHasFixedSize(true);
            LoginActivity.getServerHelper().setContext(this.getContext());
            JSONObject data = LoginActivity.getServerHelper().getParentData();

            ArrayList<Child> childrenToSchool = new ArrayList<Child>();
            ArrayList<Child> childrenFromSchool = new ArrayList<Child>();

            // int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

            int currentDay = Calendar.MONDAY;

            try {
                JSONArray jsonGroups = data.getJSONArray("groups");
                for(int i = 0; i < jsonGroups.length(); i++) {
                    JSONObject jsonGroup = jsonGroups.getJSONObject(i);
                    JSONArray jsonChildren = jsonGroup.getJSONArray("children");
                    String time = jsonGroup.getString("time");
                    int day = getDay(time.substring(0, time.indexOf("_")));
                    Log.d(TAG, "Current Day: " + currentDay + ", Day: " + day);

                    if(day == currentDay) {
                        String am_or_pm = time.substring(time.indexOf("_") + 1).toUpperCase();
                        Log.d(TAG, "AM/PM: " +am_or_pm);
                        switch(am_or_pm) {
                            case "AM":
                                for(int j = 0; j < jsonChildren.length(); j++) {
                                    JSONObject jsonChild = jsonChildren.getJSONObject(j);
                                    String id = jsonChild.getString("id");
                                    String name = jsonChild.getString("name");
                                    String status = jsonChild.getString("status");
                                    Log.d(TAG, id + " " + name + " " + status);
                                    childrenToSchool.add(new Child(id, name, null, status, null, null));
                                }
                                break;
                            case "PM":
                                for(int j = 0; j < jsonChildren.length(); j++) {
                                    JSONObject jsonChild = jsonChildren.getJSONObject(j);
                                    String id = jsonChild.getString("id");
                                    String name = jsonChild.getString("name");
                                    String status = jsonChild.getString("status");
                                    Log.d(TAG, id + " " + name + " " + status);
                                    childrenFromSchool.add(new Child(id, name, null, status, null, null));
                                }
                                break;
                        }
                    }
                }
            } catch(Exception e) {}


            if(getArguments().getBoolean(ARG_TO_SCHOOL)) {
                mChildAdapter = new ChaperoneAdapter(childrenToSchool, this.getContext());
            } else {
                mChildAdapter = new ChaperoneAdapter(childrenFromSchool, this.getContext());
            }

            rv.setAdapter(mChildAdapter);

            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            rv.setLayoutManager(llm);
            return rootView;
        }
    }

    private static int getDay(String day) {
        day = day.toUpperCase();
        switch(day) {
            case "MONDAY":
                return Calendar.MONDAY;
            case "TUESDAY":
                return Calendar.TUESDAY;
            case "WEDNESDAY":
                return Calendar.WEDNESDAY;
            case "THURSDAY":
                return Calendar.THURSDAY;
            case "FRIDAY":
                return Calendar.FRIDAY;
            default:
                return -1;
        }
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
            this.recreate();
        } else if (id == R.id.nav_group) {
            Intent intent = new Intent(this, GroupActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
            switch(position) {
                case 0:
                    return ChaperoneActivity.PlaceholderFragment.newInstance(true);
                case 1:
                    return ChaperoneActivity.PlaceholderFragment.newInstance(false);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "To School";
                case 1:
                    return "To Home";
            }
            return null;
        }
    }
}
