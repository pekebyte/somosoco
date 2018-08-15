package com.pekebyte.somosoco.ui.services;

import android.app.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pekebyte.somosoco.R;
import com.pekebyte.somosoco.data.AppDatabase;
import com.pekebyte.somosoco.data.dao.PostDao;
import com.pekebyte.somosoco.data.models.OcoPosts;
import com.pekebyte.somosoco.data.models.Post;
import com.pekebyte.somosoco.data.network.RestAdapter;
import com.pekebyte.somosoco.data.repository.PostRepository;
import com.pekebyte.somosoco.ui.activities.PostDetail;
import com.pekebyte.somosoco.ui.viewmodels.SavedViewModel;
import com.pekebyte.somosoco.ui.viewmodels.ServiceViewModel;

/**
 * Created by pedromolina on 2/4/18.
 */

public class PostService extends Service implements LifecycleOwner {
    // constant
    public static final long INTERVAL = 3600 * 1000; // 1 hour

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    private ServiceViewModel viewModel;
    private PostRepository postRepo;
    private PostDao dao;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        postRepo = new PostRepository(RestAdapter.createAPI());
        dao = AppDatabase.getDatabase(getApplicationContext()).postDao();

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.pekebyte.somosoco", MODE_PRIVATE);
        Boolean notificationsEnabled = sharedPreferences.getBoolean("notificationsEnabled",true);
        if (notificationsEnabled == true) {
            if (mTimer != null)
                mTimer.cancel();
            else
                mTimer = new Timer(); // recreate new timer


            mTimer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    retrievePosts();
                }
            }, 0, INTERVAL);// schedule task
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void retrievePosts(){
        Observer<OcoPosts> observer = new Observer<OcoPosts>() {
            @Override
            public void onChanged(@Nullable OcoPosts res) {
                for (int i=0; i< res.getPosts().size(); i++){
                    Post post = res.getPosts().get(i);
                    if (dao.exists(post.getId()) == 0){
                        notifyUser(post);
                        dao.save(post);
                    }
                }
                postRepo.getPosts(null).removeObserver(this);
            }
        };

        this.postRepo.getPosts(null).observe(this,observer);
    }

    private void notifyUser(final Post item){
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
        notificationManager.notify(Integer.parseInt(item.getId()), notification);

    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }
}