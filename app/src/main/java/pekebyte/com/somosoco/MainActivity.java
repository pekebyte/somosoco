package pekebyte.com.somosoco;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    NavigationView nav_view;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLeftMenu();
        nav_view.getMenu().performIdentifierAction(R.id.left_item_home,0);
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
                        return true;
                    case R.id.left_item_telegram:
                        Intent telegram = null;
                        try {
                            getPackageManager().getPackageInfo("org.telegram.messenger", 0);
                            telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/somosoco"));
                        } catch (Exception e) {
                            telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/somosoco"));
                        }
//                        Intent telegram = new Intent(Intent.ACTION_VIEW);
//                        telegram.setData(Uri.parse("https://t.me/somosoco"));
//                        telegram.setPackage("org.telegram.messenger");
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
                }
                drawer.closeDrawers();
                return true;
            }
        });

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);



    }

//    private void initActionBar(){
//        Drawable d=getResources().getDrawable(R.drawable.actionbarbg2);
//        getSupportActionBar().setBackgroundDrawable(d);
//    }

    protected void setFragment(Fragment fragment) {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.content_frame, fragment, fragment.getTag());
        t.commit();
    }

}
