package com.it_tech613.predator.apps;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import static com.it_tech613.predator.apps.MyApp.INPUT_FILE;
import static com.it_tech613.predator.apps.MyApp.INPUT_NAME;


public class MyJobService extends JobService {
    JobParameters mParams;
    private static final String TAG = MyJobService.class.getSimpleName();
    static String recording_time = "";
    @Override
    public boolean onStartJob(final JobParameters params) {
        mParams = params;
        download();
        Log.e(TAG, "Running!!!!!!!!!!!!!"+System.currentTimeMillis());
        return true;
    }

    @Override
    public boolean onStopJob(final JobParameters params) {
        Log.e(TAG, "onStopJob() was called");
        jobFinished(params, false);
        try {
            HttpDownloadUtility.stopDownlaod();
        }catch (Exception e){

        }
        return false;
    }

    private void download() {
        String downloadPath = Environment.getExternalStorageDirectory() + "/" + "DOWNLOAD_AIO_VIDEO/";
        File dir = new File(downloadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String[] command1 = {"-y", "-i", INPUT_FILE, "-s", "1920x1080", "-r", "15", "-vcodec", "mpeg4" ,dir.toString() + "/"+INPUT_NAME+".mp4"};
        try {
            HttpDownloadUtility.downloadFile(INPUT_FILE, dir.toString(),INPUT_NAME+".ts");
        }catch (Exception e){

        }

    }
}
