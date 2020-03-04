package com.it_tech613.predator.apps;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.it_tech613.predator.apps.MyApp.is_canceled;
import static com.it_tech613.predator.apps.MyApp.is_failed;
import static com.it_tech613.predator.apps.MyApp.is_finished;
import static com.it_tech613.predator.apps.MyApp.is_start;


public class HttpDownloadUtility {
    private static final String TAG = HttpDownloadUtility.class.getSimpleName();
    private static final int BUFFER_SIZE = 1024;
    private static HttpURLConnection connection = null;
    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
     */
    public static void downloadFile(String fileURL, String saveDir,String file_name)
            throws IOException {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .penaltyLog()
                .detectAll()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .penaltyLog()
                .detectAll()
                .build());
        new versionUpdate(fileURL,saveDir,file_name).execute();
    }
    public static void stopDownlaod(){
        if(connection!=null){
            connection.disconnect();
        }
    }

    public static class versionUpdate extends AsyncTask<String, Integer, String> {
        File file;
        String  fileUrl;
        String  fileName;
        String  saveDri;

        versionUpdate(String fileUrl, String  saveDri, String  fileName) {
            // list all the parameters like in normal class define
            this.fileUrl = fileUrl;
            this.fileName = fileName;
            this.saveDri = saveDri;
        }

        @Override
        protected void onPreExecute() {
            MyApp.instance.showProgressNotification();
            MyApp.instance.Unknowned();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream input = null;
            OutputStream output = null;
            URL url=null;
            try {
                url  = new URL(fileUrl);
                long currentTimeMillis = (System.currentTimeMillis() / 1000) + (MyApp.recording_min-1)*60+50;
                try {
                    Log.e(TAG,fileUrl);
                    Log.e(TAG,fileName);
                    Log.e(TAG,saveDri);
                    String destination = saveDri +"/" +fileName;
                    file = new File(destination);
//                if(file.exists()){
//                    file.delete();
//                }
                    Log.e(TAG,destination);
                    output = new FileOutputStream(file, false);
                    int result = doWork(output,currentTimeMillis,url);
                    switch (result){
                        case 0://success
                            return null;
                        case -1://cancelled by user
                            return null;
                    }
                    Log.e("result",result+"");
                } catch (Exception e) {
                    e.printStackTrace();
                    return e.toString();
                } finally {
                    if (System.currentTimeMillis() / 1000 >= currentTimeMillis){
                        Log.e("error2","error2");
                        try {
                            if (output != null)
                                output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (connection != null)
                            connection.disconnect();
                    }else {
                        try {
                            int result = doWork(output, currentTimeMillis,url);
                            switch (result){
                                case 0://success
                                    break;
                                case -1://cancelled by user
                                    break;
                            }
                            Log.e("result",result+"");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
                return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "error";
            }
        }

        private int doWork(OutputStream output,long currentTimeMillis, URL url) throws IOException {
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() != 200) {
                Log.e("Download Task", "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
                is_failed = true;
                MyApp.is_recording = false;
                MyApp.instance.stopMyJob();
                return -3;
            }else {
                InputStream input = connection.getInputStream();
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(output);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(connection.getInputStream());
                byte[] bArr = new byte[BUFFER_SIZE];
                int count;
                is_start = true;
                while ((count = bufferedInputStream.read(bArr)) != -1) {
                    if ( System.currentTimeMillis() / 1000 >= currentTimeMillis || is_finished) {
                        MyApp.is_recording = false;
                        MyApp.instance.stopMyJob();
                        try {
                            bufferedOutputStream.close();
                            bufferedInputStream.close();
                            if (input != null)
                                input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                    if(is_canceled){
                        MyApp.is_recording = false;
                        MyApp.instance.stopMyJob();
                        try {
                            bufferedOutputStream.close();
                            bufferedInputStream.close();
                            if (input != null)
                                input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return -1;
                    }
                    if (isCancelled()) {
                        input.close();
                        return doWork(output, currentTimeMillis,url);
                    }
                    bufferedOutputStream.write(bArr, 0, count);
                }
                return doWork(output, currentTimeMillis,url);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            MyApp.instance.stopMyJob();
            if (result != null) {
                is_failed = true;
                MyApp.instance.showToast("failed");
                Log.e(TAG,"Update Failed");
            } else{
                if(!is_canceled){
                    MyApp.instance.showToast("completed");
                }
                Log.e(TAG,"Update Successfully");
            }
        }
    }
}
