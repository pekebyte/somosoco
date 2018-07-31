package com.pekebyte.somosoco.ui.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;

import de.cketti.mailto.EmailIntentBuilder;

import com.pekebyte.somosoco.R;
import com.pekebyte.somosoco.ui.helpers.CustomTypefaceSpan;
import com.pekebyte.somosoco.ui.fragments.HomeFragment;
import com.pekebyte.somosoco.ui.fragments.SavedFragment;
import com.pekebyte.somosoco.ui.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    NavigationView nav_view;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLeftMenu();
        nav_view.getMenu().performIdentifierAction(R.id.left_item_home,0);

        Fragment frag = HomeFragment.newInstance();
        setFragment(frag);
        setActionBarTitle(getString(R.string.app_name));
    }

    private void initLeftMenu() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nav_view = (NavigationView) findViewById(R.id.nav_view);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem item) {
                Fragment frag = null;
                switch (item.getItemId()) {
                    case R.id.left_item_home:
                        frag = HomeFragment.newInstance();
                        setFragment(frag);
                        setActionBarTitle(getString(R.string.app_name));
                        drawer.closeDrawers();
                        return true;
                    case R.id.left_item_favorites:
                        frag = SavedFragment.newInstance();
                        setFragment(frag);
                        setActionBarTitle(getString(R.string.menu_favorites));
                        drawer.closeDrawers();
                        return true;
                    case R.id.left_item_telegram:
                        Intent telegram = null;
                        try {
                            getPackageManager().getPackageInfo("org.telegram.messenger", 0);
                            telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/somosoco"));
                        } catch (Exception e) {
                            telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/somosoco"));
                        }
                        startActivity(telegram);
                        return false;
                    case R.id.left_item_facebook:
                        Intent facebook = null;
                        try {
                            getPackageManager().getPackageInfo("com.facebook.katana", 0);
                            facebook = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://group/<1478892489074114>"));
                        } catch (Exception e) {
                            facebook = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/groups/somosOCO"));
                        }
                        startActivity(facebook);
                        return false;
                    case R.id.left_item_youtube:
                        Intent youtube = null;
                        youtube = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCwgJ0FER4wBvlehiQmdSWEA"));
                        startActivity(youtube);
                        return false;
                    case R.id.left_item_mail:
                        Intent emailIntent = EmailIntentBuilder.from(getApplicationContext())
                                .to("elmaildeoco@gmail.com")
                                .subject("Mail desde la app de OCO")
                                .build();
                        startActivity(emailIntent);
                        return false;
                    case R.id.left_item_settings:
                        frag = SettingsFragment.newInstance();
                        setFragment(frag);
                        setActionBarTitle(getString(R.string.settings));
                        drawer.closeDrawers();
                        return true;
                }
                drawer.closeDrawers();
                return true;
            }
        });

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);



    }

    protected void setFragment(Fragment fragment) {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.content_frame, fragment, fragment.getTag());
        t.commit();
    }

    private void setActionBarTitle(String title){
        Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/GillSansUltraBold.ttf");
        SpannableStringBuilder ssb = new SpannableStringBuilder(title);
        ssb.setSpan(new CustomTypefaceSpan("", font), 0, title.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(ssb);
    }

}
