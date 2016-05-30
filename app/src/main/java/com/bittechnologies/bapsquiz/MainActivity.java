package com.bittechnologies.bapsquiz;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bittechnologies.bapsquiz.fragments.HomeFragment;
import com.bittechnologies.bapsquiz.fragments.LoginFragment;
import com.bittechnologies.bapsquiz.fragments.MainHomeFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainContainer, new MainHomeFragment()).addToBackStack(null)
                .commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View v = navigationView.getHeaderView(0);
        TextView imageView = (TextView) v.findViewById(R.id.txtUserNameHeader);
        imageView.setText(mSharedPreferences.getString("userName", "Jay Swaminarayan"));

    }

    /* @Override
     public void onBackPressed() {


       *//*  DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*//*
    }*/
    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(R.id.mainContainer);

        if (fm.getBackStackEntryCount() > 1) {
            fm.popBackStack();
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SharedPreferences mSharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        int flag = mSharedPreferences.getInt("Logged", 0);
        if (flag == 1) {
            menu.findItem(R.id.action_settings).setTitle("Logout");
        } else {
            menu.findItem(R.id.action_settings).setTitle("Login");
        }
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
            if (item.getTitle().equals("Logout")) {
                item.setTitle("Logout");
                SharedPreferences mSharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                mEditor.putInt("Logged", 0);
                mEditor.clear();
                mEditor.commit();
              /* FragmentManager fragmentManager = getSupportFragmentManager();
               fragmentManager.beginTransaction()
                       .replace(R.id.mainContainer, new MainHomeFragment()).addToBackStack("Login")
                       .commit();
               return true;*/
                Intent intent = getIntent();
                finish();
                startActivity(intent);

            } else if (item.getTitle().equals("Login")) {
                SharedPreferences mSharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                int flag = mSharedPreferences.getInt("Logged", 0);
                if (flag == 1) {
                    item.setTitle("Logout");
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContainer, new LoginFragment()).addToBackStack("Login")
                        .commit();


            }
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Home) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContainer, new MainHomeFragment()).addToBackStack("Main")
                    .commit();
            // Handle the camera action
        } else if (id == R.id.nav_exam) {
            SharedPreferences mSharedPreferences =getSharedPreferences("Login", Context.MODE_PRIVATE);
            int flag = mSharedPreferences.getInt("Logged", 0);
            if (flag == 1) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContainer, new HomeFragment()).addToBackStack("Home")
                        .commit();
            }
            else {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContainer, new LoginFragment()).addToBackStack("Login")
                        .commit();
            }

           /* FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContainer, new HomeFragment()).addToBackStack("Home")
                    .commit();*/

        } else if (id == R.id.nav_slideshow) {
            SharedPreferences mSharedPreferences =getSharedPreferences("Login", Context.MODE_PRIVATE);
            int flag = mSharedPreferences.getInt("Logged", 0);
            if (flag == 1) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContainer, new HomeFragment()).addToBackStack("Home")
                        .commit();
            } else {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContainer, new LoginFragment()).addToBackStack("Login")
                        .commit();
            }

          /*  FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContainer, new LoginFragment()).addToBackStack(null)
                    .commit();*/


        } else if (id == R.id.nav_share) {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                String sAux = "\nLet me recommend you this application\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id="
                    + getPackageName() + " \n\n";
                sAux = sAux + "\n Give Us rating on play store" + " \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "Choose one"));
            } catch (Exception e) {
                Log.e("Exception", "" + e.getMessage());
            }


        } else if (id == R.id.nav_send) {

            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(myAppLinkToMarket);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Unable to find market app", Toast.LENGTH_LONG).show();
            }


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
