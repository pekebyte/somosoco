package pekebyte.com.somosoco;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.CacheControl;
import pekebyte.com.somosoco.Helpers.API;
import pekebyte.com.somosoco.Helpers.Constants;
import pekebyte.com.somosoco.Helpers.Database;
import pekebyte.com.somosoco.Helpers.RestAdapter;
import pekebyte.com.somosoco.Model.Item;
import pekebyte.com.somosoco.Model.OcoPosts;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.POST;

/**
 * Created by pedromolina on 2/4/18.
 */

public class PostService extends Service {
    // constant
    public static final long INTERVAL = 10 * 1000; // 10 seconds

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    API api;

    Database db;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        api = RestAdapter.createAPI();
        db = new Database();

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("pekebyte.com.somosoco", MODE_PRIVATE);
        Boolean notificationsEnabled = sharedPreferences.getBoolean("notificationsEnabled",true);
        if (notificationsEnabled == true) {
            if (mTimer != null)
                mTimer.cancel();
            else
                mTimer = new Timer(); // recreate new timer


            mTimer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    Log.d("holi", "work");
                    retrievePosts();
                }
            }, 0, INTERVAL);// schedule task
        }
    }

    private void retrievePosts(){
        Call<OcoPosts> callbackCall = api.getPosts(Constants.BLOGGER_KEY, null);
        callbackCall.enqueue(new Callback<OcoPosts>() {
            @Override
            public void onResponse(Call<OcoPosts> call, Response<OcoPosts> response) {
                if (response.isSuccessful()){
                    if (response.body().getItems().size() > 0){
                        for (int i=0; i<response.body().getItems().size(); i++){
                            if (!db.checkIfExists(getApplicationContext(),response.body().getItems().get(i))){
                                db.insertPost(getApplicationContext(),response.body().getItems().get(i));
                                notifyUser(response.body().getItems().get(i));
                            }
                        }
                    }
                }
                else{
                    Log.d("post retrieving failed",response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<OcoPosts> call, Throwable t) {
                Log.d("post retrieving failed",t.getMessage());
            }
        });
    }

    private void notifyUser(final Item item){
        Log.d("aitana war", "lo malo");
        final Context ctx = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) ctx
                                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(ctx, PostDetail.class);
        Gson gson = new Gson();
        intent.putExtra("item", gson.toJson(item));

        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        Notification notification = new Notification.Builder(ctx)
                .setContentTitle(ctx.getResources().getString(R.string.app_name))
                .setContentText(item.getTitle())
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .build();

        // hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

         notification.contentIntent = pIntent;
         notificationManager.notify(1, notification);

    }

    private String extractUrls(String input) {

        String result = null;
        Pattern pattern = Pattern.compile(
                "<img[^>]*src=[\\\\\\\"']([^\\\\\\\"^']*)");

        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String src = matcher.group();
            int startIndex = src.indexOf("src=") + 5;
            if (result == null){
                result = src.substring(startIndex, src.length());
            }
        }
        return result;
    }

}
