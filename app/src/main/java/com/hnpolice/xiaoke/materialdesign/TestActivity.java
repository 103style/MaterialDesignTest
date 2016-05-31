package com.hnpolice.xiaoke.materialdesign;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class TestActivity extends AppCompatActivity {


    Toolbar toolBar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        toolBar = (Toolbar) findViewById(R.id.tool_bar);
        if (toolBar != null) {
            toolBar.setTitle(R.string.default_search_keyword);
        }
        setSupportActionBar(toolBar);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.drawer_open, R.string.drawer_close);
//        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();

        drawerLayout.addDrawerListener(mDrawerToggle);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        setupDrawerContent(navigationView);

        //profile Image
        setUpProfileImage();

        switchToBook();

    }

    private void switchToBook() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new BookFragment()).commit();
        toolBar.setTitle(R.string.navigation_about);
    }

    private void setUpProfileImage() {
        View header = navigationView.inflateHeaderView(R.layout.avigation_header);
        View avatar = header.findViewById(R.id.avatar);
        if (avatar != null) {
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.closeDrawers();
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
            });
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_item_1:
                        toolBar.setTitle(R.string.navigation_about);
                        break;
                    case R.id.navigation_item_2:
                        toolBar.setTitle(R.string.navigation_example);
                        break;
                    case R.id.navigation_item_3:
                        toolBar.setTitle(R.string.navigation_my_blog);
                        break;
                    case R.id.navigation_item_4:
                        toolBar.setTitle(R.string.navigation_book);
                        break;
                }
                item.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
