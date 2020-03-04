package com.it_tech613.predator.apps;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.exoplayer2.offline.DownloadAction;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.offline.ProgressiveDownloadAction;
import com.google.android.exoplayer2.source.dash.offline.DashDownloadAction;
import com.google.android.exoplayer2.source.hls.offline.HlsDownloadAction;
import com.google.android.exoplayer2.source.smoothstreaming.offline.SsDownloadAction;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.it_tech613.predator.BuildConfig;
import com.it_tech613.predator.R;
import com.it_tech613.predator.models.CategoryModel;
import com.it_tech613.predator.models.EPGChannel;
import com.it_tech613.predator.models.FirstServer;
import com.it_tech613.predator.models.FullModel;
import com.it_tech613.predator.models.LoginModel;
import com.it_tech613.predator.models.MovieModel;
import com.it_tech613.predator.models.RecordingModel;
import com.it_tech613.predator.models.SeasonModel;
import com.it_tech613.predator.models.SeriesModel;
import com.it_tech613.predator.utils.DownloadTracker;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.joda.time.DateTimeConstants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import iptvclient.ApiClient;
import iptvclient.Iptvclient;

public class MyApp extends Application {
    public static SeasonModel selectedSeasonModel;
    public static EPGChannel selectedChannel;
    private MyPreference preference;
    public static MyApp instance;
    public static LoginModel loginModel;
    public static List<FullModel> fullModels = new ArrayList<>();
    public static List<CategoryModel> vod_categories = new ArrayList<>();
    public static List<CategoryModel> live_categories = new ArrayList<>();
    public static List<CategoryModel> series_categories = new ArrayList<>();
    public static List<FullModel> fullModels_filter = new ArrayList<>();
    public static List<CategoryModel> vod_categories_filter = new ArrayList<>();
    public static List<CategoryModel> live_categories_filter = new ArrayList<>();
    public static List<CategoryModel> series_categories_filter = new ArrayList<>();

    public static List<MovieModel> movieModels = new ArrayList<>();
    public static List<MovieModel> favMovieModels = new ArrayList<>();
    public static List<MovieModel> recentMovieModels = new ArrayList<>();

    public static List<SeriesModel> seriesModels = new ArrayList<>();
    public static List<SeriesModel> favSeriesModels = new ArrayList<>();
    public static List<SeriesModel> recentSeriesModels = new ArrayList<>();

    public static SeriesModel selectedSeriesModel;
    public static List<MovieModel> subMovieModels = new ArrayList<>();
    public static MovieModel vod_model;

    public static int num_screen=-1;
    public static int num_server = 1;
    public static List<String> maindatas;
    public static boolean is_local = false;
    public static boolean is_mac = false;

    public static Map backup_map;
    public static String version_name,mac_address,version_str,user,pass,created_at,status,is_trail,active_cons,max_cons,time_zone,INPUT_FILE,INPUT_NAME;
    public static int SCREEN_WIDTH, SCREEN_HEIGHT, ITEM_V_WIDTH, ITEM_V_HEIGHT,SURFACE_WIDTH,SURFACE_HEIGHT,top_margin,right_margin,
            channel_size,EPG_WIDTH,EPG_HEIGHT,EPG_TOP,EPG_RIGHT;
    public static boolean is_first,is_vpn = false,is_recording= false;

    public static FirstServer firstServer= FirstServer.first;
    public static boolean is_video_played = false;

    private static final String DOWNLOAD_ACTION_FILE = "actions";
    private static final String DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions";
    private static final String DOWNLOAD_CONTENT_DIRECTORY = "downloads";
    private static final int MAX_SIMULTANEOUS_DOWNLOADS = 2;
    private static final DownloadAction.Deserializer[] DOWNLOAD_DESERIALIZERS =
            new DownloadAction.Deserializer[] {
                    DashDownloadAction.DESERIALIZER,
                    HlsDownloadAction.DESERIALIZER,
                    SsDownloadAction.DESERIALIZER,
                    ProgressiveDownloadAction.DESERIALIZER
            };

    protected String userAgent;

    private File downloadDirectory;
    private Cache downloadCache;
    private DownloadManager downloadManager;
    private DownloadTracker downloadTracker;
    private ApiClient iptvclient;
    private KProgressHUD kpHUD;

    private JobScheduler jobScheduler;
    private static final String TAG = Constants.class.getSimpleName();
    Runnable mEpgTicker;
    Handler mEpgHandler = new Handler();
    public static int recording_min = 0;
    private int recording_pos = 0;
    Runnable mRecordTicker;
    Handler mRecordHandler = new Handler();
    public NotificationManager notification1;
    public NotificationManager notification2;
    public NotificationCompat.Builder builder1;
    public NotificationCompat.Builder builder2;
    int notification_id = 234234;
    CountDownTimer countTimer1;
    CountDownTimer countTimer2;
    public static boolean is_failed = false;
    public static boolean is_completed = false;
    public static boolean is_start = false;
    public static boolean is_finished = false;
    public static boolean is_canceled = false;
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        preference = new MyPreference(getApplicationContext(), Constants.APP_INFO);
        getScreenSize();
        getTimeZone();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String p = pref.getString("set_locale", "");
        if (!p.equals("")) {
            Locale locale;
            // workaround due to region code
            if(p.equals("zh-TW")) {
                locale = Locale.TRADITIONAL_CHINESE;
            } else if(p.startsWith("zh")) {
                locale = Locale.CHINA;
            } else if(p.equals("pt-BR")) {
                locale = new Locale("pt", "BR");
            } else if(p.equals("bn-IN") || p.startsWith("bn")) {
                locale = new Locale("bn", "IN");
            } else {
                /**
                 * Avoid a crash of
                 * java.lang.AssertionError: couldn't initialize LocaleData for locale
                 * if the user enters nonsensical region codes.
                 */
                if(p.contains("-"))
                    p = p.substring(0, p.indexOf('-'));
                locale = new Locale(p);
            }
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getResources().updateConfiguration(config,
                    getResources().getDisplayMetrics());
        }

        instance = this;
        iptvclient = Iptvclient.newApiClient();
        userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
    }


    public void showProgressNotification(){
        if(notification1!=null){
            notification1.cancelAll();
        }
        if(notification2!=null){
            notification2.cancelAll();
        }
        Toast.makeText(this,getResources().getString(R.string.download_started),Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification1 = getNotification();
            builder1 = new NotificationCompat.Builder(this,BuildConfig.APPLICATION_ID);
        }else {
            notification1 = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            builder1 = new NotificationCompat.Builder(this);
        }
        builder1.setContentTitle(getResources().getString(R.string.live_recording)).setProgress(100,0,true).setContentText(getResources().getString(R.string.recording_dots)).setSmallIcon(R.mipmap.ic_launcher);
        notification1.notify(notification_id,builder1.build());
        countTimer1 = new CountDownTimer(20000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                int a = recording_min*60*1000;
                final int[] iArr0 =  new int[1];
                if(is_start){
                    countTimer1.cancel();
                    countTimer2 = new CountDownTimer((long) a, 1000*60) {
                        @Override
                        public void onTick(long j) {
                            if(!is_canceled){
                                NotificationCompat.Builder e = builder1;
                                StringBuilder append = new StringBuilder().append(getResources().getString(R.string.recording_dots));
                                int i = iArr0[0];
                                iArr0[0] = i + 60;
                                e.setContentText(append.append(getRecordingTime(i)).append(" - ").append(getRecordingTime((int) (j / 1000))).toString());
                                notification1.notify(notification_id, builder1.build());
                                if(is_completed){
                                    notification1.cancelAll();
                                    countTimer2.cancel();
                                }
                                return;
                            }
                            notification1.cancelAll();
                            countTimer2.cancel();
                            showToast("stopped");
                        }

                        @Override
                        public void onFinish() {
                            is_finished = true;
                        }
                    }.start();
                }
            }

            @Override
            public void onFinish() {
                if(!is_failed){
                    showToast("stopped");
                }
            }
        }.start();
    }

    public void showToast(String str){
        notification1.cancelAll();
        if(countTimer2!=null){
            countTimer2.cancel();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification2 = getNotification();
            builder2 = new NotificationCompat.Builder(this,BuildConfig.APPLICATION_ID);
        }else {
            notification2 = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            builder2 = new NotificationCompat.Builder(this);
        }
        builder2.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(getResources().getString(R.string.live_recording));
        if(str.equalsIgnoreCase("completed")){
            is_completed = true;
            builder2.setContentText(getResources().getString(R.string.download_completed));
            Toast.makeText(this, getResources().getString(R.string.download_completed), Toast.LENGTH_SHORT).show();
        }else if(str.equalsIgnoreCase("failed")){
            builder2.setContentText(getResources().getString(R.string.download_failed));
            Toast.makeText(this, getResources().getString(R.string.download_failed), Toast.LENGTH_SHORT).show();
        }else if(str.equalsIgnoreCase("stopped")){
            builder2.setContentText(getResources().getString(R.string.download_stopped));
            Toast.makeText(this, getResources().getString(R.string.download_stopped), Toast.LENGTH_SHORT).show();
        }

        if (notification2 != null) {
            notification2.notify(455, builder2.build());
            return;
        }
        throw new AssertionError();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationManager getNotification() {
        String CHANNEL_ID = BuildConfig.APPLICATION_ID;
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "My Channel",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        return  notificationManager;
    }

    public void Unknowned() {
        builder1.addAction(R.drawable.icon, "Stop", NotificationActivity.pending(notification_id, this));
    }

    public String getRecordingTime(int i) {
        return String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf(i / DateTimeConstants.SECONDS_PER_HOUR), Integer.valueOf((i % DateTimeConstants.SECONDS_PER_HOUR) / 60), Integer.valueOf(i % 60)});
    }


    public void scheduleJob(int record,String time,boolean is_checked) {
        List<RecordingModel> models;
        if(MyApp.instance.getPreference().get(Constants.getRecordingChannels())==null){
            models = new ArrayList<>();
        }else {
            models = (List<RecordingModel>) MyApp.instance.getPreference().get(Constants.getRecordingChannels());
        }
        RecordingModel newRecord = new RecordingModel();
        newRecord.setName(INPUT_NAME+".ts");
        newRecord.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        if(time==null || time.isEmpty()){
            newRecord.setTime(Constants.clockFormat.format(new Date()));
        }else {
            newRecord.setTime(time);
        }
        newRecord.setUrl(INPUT_FILE);
        newRecord.setIs_checked(is_checked);
        newRecord.setDuration(record);
        models.add(newRecord);
        MyApp.instance.getPreference().put(Constants.getRecordingChannels(),models);
        Log.e("is_checked",String.valueOf(is_checked));
        if(!is_checked){
            startRecording(record);
        }else {
            recording_pos = GetRecordingModelPosition(INPUT_NAME+".ts");
            Log.e("recording_pos",recording_pos+"");
            RecordTimer(recording_pos);
        }
    }

    private int GetRecordingModelPosition(String name){
        int pos = 0;
        List<RecordingModel> models;
        if(MyApp.instance.getPreference().get(Constants.getRecordingChannels())==null){
            models = new ArrayList<>();
        }else {
            models = (List<RecordingModel>) MyApp.instance.getPreference().get(Constants.getRecordingChannels());
        }
        for(int i=0;i<models.size();i++){
            if(models.get(i).getName().equalsIgnoreCase(name)){
                pos = i;
                break;
            }
        }
        return pos;
    }

    private String GetRecordingUrl(int pos){
        List<RecordingModel> models;
        if(MyApp.instance.getPreference().get(Constants.getRecordingChannels())==null){
            models = new ArrayList<>();
        }else {
            models = (List<RecordingModel>) MyApp.instance.getPreference().get(Constants.getRecordingChannels());
        }
        return models.get(pos).getUrl();
    }

    private String GetRecordingStartTime(int pos){
        List<RecordingModel> models;
        if(MyApp.instance.getPreference().get(Constants.getRecordingChannels())==null){
            models = new ArrayList<>();
        }else {
            models = (List<RecordingModel>) MyApp.instance.getPreference().get(Constants.getRecordingChannels());
        }
        return models.get(pos).getTime();
    }

    private int GetRecordDuration(int pos){
        List<RecordingModel> models;
        if(MyApp.instance.getPreference().get(Constants.getRecordingChannels())==null){
            models = new ArrayList<>();
        }else {
            models = (List<RecordingModel>) MyApp.instance.getPreference().get(Constants.getRecordingChannels());
        }
        return models.get(pos).getDuration();
    }

    private void startRecording(int record){
        recording_min = record;
        jobScheduler = (JobScheduler) getApplicationContext().getSystemService(
                Context.JOB_SCHEDULER_SERVICE);
        // The JobService that we want to run
        final ComponentName name = new ComponentName(this, MyJobService.class);

        // Schedule the job
        final int result = jobScheduler.schedule(getJobInfo(123, recording_min, name));

        // If successfully scheduled, log this thing
        if (result == JobScheduler.RESULT_SUCCESS) {
            EpgTimer(record);
            Log.e(TAG, "Scheduled job successfully!");
        }
    }

    public void stopMyJob() {
        is_recording = false;
        mEpgHandler.removeCallbacks(mEpgTicker);
        jobScheduler = (JobScheduler) getSystemService(
                Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();
    }

    private JobInfo getJobInfo(final int id, final long hour, final ComponentName name) {
//        final long interval = TimeUnit.HOURS.toMillis(hour); // run every hour
        final boolean isPersistent = true; // persist through boot
        final int networkType = JobInfo.NETWORK_TYPE_ANY; // Requires some sort of connectivity

        final JobInfo jobInfo;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobInfo = new JobInfo.Builder(id, name)
//                    .setOverrideDeadline( hour * 60 * 1000)
                    .setRequiredNetworkType(networkType)
                    .setPersisted(isPersistent)
                    .build();
        } else {
            jobInfo = new JobInfo.Builder(id, name)
//                    .setOverrideDeadline(hour * 60 * 1000)
                    .setRequiredNetworkType(networkType)
                    .setPersisted(isPersistent)
                    .build();
        }

        return jobInfo;
    }

    int epg_time;
    private void EpgTimer(int recording_min){
        epg_time = recording_min;
        mEpgTicker = new Runnable() {
            @Override
            public void run() {
                if(epg_time==0){
                    mEpgHandler.removeCallbacks(mEpgTicker);
                    stopMyJob();
                    Toast.makeText(getApplicationContext(),"Successfully",Toast.LENGTH_SHORT).show();
                    return;
                }
                runNextEpgTicker();
            }
        };
        mEpgTicker.run();
    }

    private void runNextEpgTicker() {
        epg_time--;
        mEpgHandler.removeCallbacks(mEpgTicker);
        long next = SystemClock.uptimeMillis() + 1000*60;
        mEpgHandler.postAtTime(mEpgTicker, next);
    }

    private void RecordTimer(int pos){
        mRecordTicker = new Runnable() {
            @Override
            public void run() {
                String start_time = GetRecordingStartTime(pos);
                Log.e("current_time",new SimpleDateFormat("HH:mm").format(new Date()));
                Log.e("start_time",start_time);
                if(new SimpleDateFormat("HH:mm").format(new Date()).equalsIgnoreCase(start_time)){
                    INPUT_FILE = GetRecordingUrl(pos);
                    int record = GetRecordDuration(pos);
                    startRecording(record);
                    return;
                }
                runNextRecordTicker();
            }
        };
        mRecordTicker.run();
    }

    private void runNextRecordTicker(){
        mRecordHandler.removeCallbacks(mRecordTicker);
        long next = SystemClock.uptimeMillis() + 1000*60;
        mRecordHandler.postAtTime(mRecordTicker, next);
    }

    private void getTimeZone(){
        TimeZone tz = TimeZone.getDefault();
        time_zone = tz.getID();
        Log.e("time", String.valueOf(time_zone));
    }

    public void setKpHUD(Activity activity) {
        kpHUD = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
//                .setLabel("Login")
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);
    }

    public KProgressHUD getKpHUD(){
        return kpHUD;
    }

    private void getScreenSize() {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        SCREEN_WIDTH = displayMetrics.widthPixels;
        SCREEN_HEIGHT = displayMetrics.heightPixels;
        if(SCREEN_WIDTH < SCREEN_HEIGHT){
            int a = SCREEN_WIDTH;
            SCREEN_WIDTH = SCREEN_HEIGHT;
            SCREEN_HEIGHT = a;
        }
        SURFACE_WIDTH = (int)(SCREEN_WIDTH/3);
        SURFACE_HEIGHT = (int)(SURFACE_WIDTH*0.65);
        top_margin = SCREEN_HEIGHT/7;
        right_margin = SCREEN_WIDTH/14;
        ITEM_V_WIDTH = (int) (SCREEN_WIDTH /8);
        ITEM_V_HEIGHT = (int) (ITEM_V_WIDTH * 1.6);

        EPG_WIDTH = (int)(SCREEN_WIDTH/4);
        EPG_HEIGHT = (int)(EPG_WIDTH*0.65);
        EPG_TOP = SCREEN_HEIGHT/8;
        EPG_RIGHT = SCREEN_WIDTH/20;
    }

    public void loadVersion() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version_name = pInfo.versionName;
    }

    public void versionCheck(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public MyPreference getPreference() {
        return preference;
    }

    /** Returns a {@link DataSource.Factory}. */
    public DataSource.Factory buildDataSourceFactory(TransferListener<? super DataSource> listener) {
        DefaultDataSourceFactory upstreamFactory =
                new DefaultDataSourceFactory(this, listener, buildHttpDataSourceFactory(listener));
        return buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache());
    }

    /** Returns a {@link HttpDataSource.Factory}. */
    public HttpDataSource.Factory buildHttpDataSourceFactory(
            TransferListener<? super DataSource> listener) {
        return new DefaultHttpDataSourceFactory(userAgent, listener);
    }

    /** Returns whether extension renderers should be used. */
    public boolean useExtensionRenderers() {
        return "withExtensions".equals(BuildConfig.FLAVOR);
    }

    public DownloadManager getDownloadManager() {
        initDownloadManager();
        return downloadManager;
    }

    public DownloadTracker getDownloadTracker() {
        initDownloadManager();
        return downloadTracker;
    }

    private synchronized void initDownloadManager() {
        if (downloadManager == null) {
            DownloaderConstructorHelper downloaderConstructorHelper =
                    new DownloaderConstructorHelper(
                            getDownloadCache(), buildHttpDataSourceFactory(/* listener= */ null));
            downloadManager =
                    new DownloadManager(
                            downloaderConstructorHelper,
                            MAX_SIMULTANEOUS_DOWNLOADS,
                            DownloadManager.DEFAULT_MIN_RETRY_COUNT,
                            new File(getDownloadDirectory(), DOWNLOAD_ACTION_FILE),
                            DOWNLOAD_DESERIALIZERS);
            downloadTracker =
                    new DownloadTracker(
                            /* context= */ this,
                            buildDataSourceFactory(/* listener= */ null),
                            new File(getDownloadDirectory(), DOWNLOAD_TRACKER_ACTION_FILE),
                            DOWNLOAD_DESERIALIZERS);
            downloadManager.addListener(downloadTracker);
        }
    }

    private synchronized Cache getDownloadCache() {
        if (downloadCache == null) {
            File downloadContentDirectory = new File(getDownloadDirectory(), DOWNLOAD_CONTENT_DIRECTORY);
            downloadCache = new SimpleCache(downloadContentDirectory, new NoOpCacheEvictor());
        }
        return downloadCache;
    }

    private File getDownloadDirectory() {
        if (downloadDirectory == null) {
            downloadDirectory = getExternalFilesDir(null);
            if (downloadDirectory == null) {
                downloadDirectory = getFilesDir();
            }
        }
        return downloadDirectory;
    }

    private static CacheDataSourceFactory buildReadOnlyCacheDataSource(
            DefaultDataSourceFactory upstreamFactory, Cache cache) {
        return new CacheDataSourceFactory(
                cache,
                upstreamFactory,
                new FileDataSourceFactory(),
                /* cacheWriteDataSinkFactory= */ null,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
                /* eventListener= */ null);
    }

    public ApiClient getIptvclient() {
        return iptvclient;
    }
}