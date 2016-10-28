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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

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

            am.setHasFixedSize(true);
            pm.setHasFixedSize(true);

            LoginActivity.getServerHelper().setContext(this.getContext());

            ArrayList<Child> childrenMonAm = new ArrayList<Child>();
            ArrayList<Child> childrenMonPm = new ArrayList<Child>();

            ArrayList<Child> childrenTuesAm = new ArrayList<Child>();
            ArrayList<Child> childrenTuesPm = new ArrayList<Child>();

            ArrayList<Child> childrenWedAm = new ArrayList<Child>();
            ArrayList<Child> childrenWedPm = new ArrayList<Child>();

            ArrayList<Child> childrenThursAm = new ArrayList<Child>();
            ArrayList<Child> childrenThursPm = new ArrayList<Child>();

            ArrayList<Child> childrenFriAm = new ArrayList<Child>();
            ArrayList<Child> childrenFriPm = new ArrayList<Child>();

            // Placeholder implementation
            ArrayList<Child> placeholderChildren = new ArrayList<Child>();
            childrenMonAm.add(new Child(null, "Samantha", null, null, null, null));
            childrenMonPm.add(new Child(null, "Timmy", null, null, null, null));
            childrenTuesAm.add(new Child(null, "Jerry", null, null, null, null));
            childrenTuesPm.add(new Child(null, "Sally", null, null, null, null));
            childrenWedAm.add(new Child(null, "Amanda", null, null, null, null));
            childrenWedPm.add(new Child(null, "George", null, null, null, null));
            childrenThursAm.add(new Child(null, "Beth", null, null, null, null));
            childrenThursPm.add(new Child(null, "Franklin", null, null, null, null));
            childrenFriAm.add(new Child(null, "Harry", null, null, null, null));
            placeholderChildren.add(new Child(null, "Gerald", null, null, null, null));
            childrenFriPm.add(new Child(null, "Marie", null, null, null, null));

            switch(getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    amChildAdapter = new GroupAdapter(childrenMonAm, this.getContext());
                    pmChildAdapter = new GroupAdapter(childrenMonPm, this.getContext());
                    break;
                case 2:
                    amChildAdapter = new GroupAdapter(childrenTuesAm, this.getContext());
                    pmChildAdapter = new GroupAdapter(childrenTuesPm, this.getContext());
                    break;
                case 3:
                    amChildAdapter = new GroupAdapter(childrenWedAm, this.getContext());
                    pmChildAdapter = new GroupAdapter(childrenWedPm, this.getContext());
                    break;
                case 4:
                    amChildAdapter = new GroupAdapter(childrenThursAm, this.getContext());
                    pmChildAdapter = new GroupAdapter(childrenThursPm, this.getContext());
                    break;
                case 5:
                    amChildAdapter = new GroupAdapter(childrenFriAm, this.getContext());
                    pmChildAdapter = new GroupAdapter(childrenFriPm, this.getContext());
                    break;
            }

            am.setAdapter(amChildAdapter);
            pm.setAdapter(pmChildAdapter);

            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            am.setLayoutManager(llm);
            pm.setLayoutManager(new LinearLayoutManager(getActivity()));
            return rootView;
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
