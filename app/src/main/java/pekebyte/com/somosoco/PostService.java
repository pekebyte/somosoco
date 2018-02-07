package pekebyte.com.somosoco;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        api = RestAdapter.createAPI();

        if(mTimer!=null)
            mTimer.cancel();
        else
            mTimer=new Timer(); // recreate new timer


        mTimer.scheduleAtFixedRate(new TimerTask(){

            @Override
            public void run() {
                Log.d("holi", "work");
                retrievePosts();
            }
        },0,INTERVAL);// schedule task
    }

    private void retrievePosts(){
        Call<OcoPosts> callbackCall = api.getPosts(Constants.BLOGGER_KEY, null);
        callbackCall.enqueue(new Callback<OcoPosts>() {
            @Override
            public void onResponse(Call<OcoPosts> call, Response<OcoPosts> response) {
                if (response.isSuccessful()){
                    if (response.body().getItems().size() > 0){
                        Log.d("First post", response.body().getItems().get(0).getTitle());
                        Log.d("First post", ""+response.body().getItems().get(0).getId());
                        for (int i=0; i<response.body().getItems().size(); i++){
                            if (!checkIfExists(response.body().getItems().get(i).getId())){
                                insertPost(response.body().getItems().get(i));
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

    private Boolean checkIfExists(Long id){
        SQLiteDatabase ocoDB = getApplicationContext().openOrCreateDatabase("somosoco", MODE_PRIVATE, null);
        String rawSQL = "SELECT * FROM ocoposts WHERE id="+id;
        Cursor c = ocoDB.rawQuery(rawSQL, null);
        Boolean response = false;
        if (c.getCount() > 0){
            response = true;
        }
        ocoDB.close();
        return response;
    }



    private void insertPost(Item post){
        SQLiteDatabase ocoDB = getApplicationContext().openOrCreateDatabase("somosoco", MODE_PRIVATE, null);
        Gson gson = new Gson();
        String json = gson.toJson(post);
        String sql = "INSERT INTO ocoposts (id, item) VALUES (?, ?)";
        SQLiteStatement statement = ocoDB.compileStatement(sql);

        statement.bindString(1, ""+post.getId());
        statement.bindString(2, json);

        statement.execute();
        statement.close();

        ocoDB.close();
    }

    private void notifyUser(final Item item){
        Log.d("aitana war", "lo malo");
        final Context ctx = getApplicationContext();
        Picasso.with(this)
                .load(extractUrls(item.getContent()))
                .resize(50,50)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Log.d("AQUI JUE", "ES LA NOTIJICACION BITCHO");
                        NotificationManager notificationManager = (NotificationManager) ctx
                                .getSystemService(Context.NOTIFICATION_SERVICE);

                        Intent intent = new Intent(ctx, PostDetail.class);
                        Gson gson = new Gson();
                        intent.putExtra("item", gson.toJson(item));

                        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


                        Notification notification = new Notification.Builder(ctx)
                                .setContentTitle(
                                        ctx.getResources().getString(R.string.app_name))
                                .setContentText(item.getTitle())
                                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                                .setLargeIcon(bitmap).build();

                        // hide the notification after its selected
                        notification.flags |= Notification.FLAG_AUTO_CANCEL;

                        notification.contentIntent = pIntent;
                        notificationManager.notify(1, notification);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        NotificationManager notificationManager = (NotificationManager) ctx
                                .getSystemService(Context.NOTIFICATION_SERVICE);

                        Intent intent = new Intent(ctx, PostDetail.class);
                        Gson gson = new Gson();
                        intent.putExtra("item", gson.toJson(item));

                        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


                        Notification notification = new Notification.Builder(ctx)
                                .setContentTitle(
                                        ctx.getResources().getString(R.string.app_name))
                                .setContentText(item.getTitle())
                                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                                .build();

                        // hide the notification after its selected
                        notification.flags |= Notification.FLAG_AUTO_CANCEL;

                        notification.contentIntent = pIntent;

                        notificationManager.notify(1, notification);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

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
