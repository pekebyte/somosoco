package pekebyte.com.somosoco;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;

import com.google.gson.Gson;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import pekebyte.com.somosoco.Helpers.CustomTypefaceSpan;
import pekebyte.com.somosoco.Model.Item;

public class PostDetail extends AppCompatActivity {
    Item item;
    WebView wv;

    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        initActionBar();

        Bundle b = getIntent().getExtras();
        String json = b.getString("item");
        Gson gson = new Gson();
        item = gson.fromJson(json, Item.class);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setActionBarTitle(item.getTitle());

        wv = (WebView) findViewById(R.id.wv);
        wv.setVerticalScrollBarEnabled(true);
        wv.setHorizontalScrollBarEnabled(true);
        wv.getSettings().setJavaScriptEnabled(true);
//        wv.getSettings().setLoadWithOverviewMode(true);
//        wv.getSettings().setUseWideViewPort(true);

        String html = "<!DOCTYPE html><html><head>";
        html += "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />";
        html += "<style>img{max-width:100%;width: auto;height: auto;}iframe{max-width:100%;width: auto; height: auto;}</style>";
        html += "</head><body>"+item.getContent()+"</body></html>";



        wv.loadData(html,"text/html; charset=utf-8", "utf-8");


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ActivityManager mngr = (ActivityManager) getSystemService( ACTIVITY_SERVICE );

                List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

                if(taskList.get(0).numActivities == 1 &&
                        taskList.get(0).topActivity.getClassName().equals(this.getClass().getName())) {
                    Intent i = new Intent(this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }else{
                    onBackPressed();
                }
                break;
        }
        return true;
    }

    private void initActionBar(){
        Drawable d=getResources().getDrawable(R.drawable.actionbarbg2);
        getSupportActionBar().setBackgroundDrawable(d);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /** Inflating the current activity's menu with res/menu/items.xml */
        getMenuInflater().inflate(R.menu.menu_share, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.share);

        /** Getting the actionprovider associated with the menu item whose id is share */
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        /** Setting a share intent */
        mShareActionProvider.setShareIntent(getDefaultShareIntent());

        return super.onCreateOptionsMenu(menu);

    }

    private Intent getDefaultShareIntent(){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, item.getTitle());
        i.putExtra(Intent.EXTRA_TEXT, item.getUrl());
        return i;
    }

    private void setActionBarTitle(String title){
        Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/GillSansUltraBold.ttf");
        SpannableStringBuilder ssb = new SpannableStringBuilder(title);
        ssb.setSpan(new CustomTypefaceSpan("", font), 0, title.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(ssb);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            Class.forName("android.webkit.WebView")
                    .getMethod("onPause", (Class[]) null)
                    .invoke(wv, (Object[]) null);

            } catch(ClassNotFoundException cnfe) {
                Log.d("YT err", cnfe.getMessage());
            } catch(NoSuchMethodException nsme) {
                Log.d("YT err", nsme.getMessage());
            } catch(InvocationTargetException ite) {
                Log.d("YT err", ite.getMessage());
            } catch (IllegalAccessException iae) {
                Log.d("YT err", iae.getMessage());
            }

    }
}
