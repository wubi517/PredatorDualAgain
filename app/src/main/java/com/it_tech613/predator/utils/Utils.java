package com.it_tech613.predator.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.util.TypedValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class Utils {
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            assert children != null;
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            Log.e("FragmentHome","deleted files");
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public static String getPhoneMac(Context context) {
        try {
            String s = getEthMacfromEfuse("/sys/class/efuse/mac");
            if (s == null) {
                s = getEthMacfromEfuse("/sys/class/net/eth0/address");
            }
            if (s == null) {
                final Class<?> forName = Class.forName("android.os.SystemProperties");
                s = (String)forName.getMethod("get", String.class).invoke(forName, "ubootenv.var.ethaddr");
                if (s == null) {
                    final WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                    if (wifiManager != null) {
                        s = wifiManager.getConnectionInfo().getMacAddress();
                    }
                }
            }
            if(s==null || s.isEmpty()){
                try {
                    List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
                    for (NetworkInterface nif: all) {
                        if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                        byte[] macBytes = nif.getHardwareAddress();
                        if (macBytes == null) {
                            return "";
                        }

                        StringBuilder res1 = new StringBuilder();
                        for (byte b: macBytes) {
                            //res1.append(Integer.toHexString(b & 0xFF) + ":");
                            res1.append(String.format("%02X:", b));
                        }

                        if (res1.length() > 0) {
                            res1.deleteCharAt(res1.length() - 1);
                        }
                        return res1.toString();
                    }
                }catch (Exception e){
                    return "020000000000";
                }
            }
            if (s == null) {
                return "c44eac0561b5";
            }
            return s;
        }
        catch (Exception ex) {
            return "000000000099";
        }
    }
    private static String getEthMacfromEfuse(final String s) {
        String s2;
        try {
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(s), 12);
            try {
                final String line = bufferedReader.readLine();
                bufferedReader.close();
                s2 = line;
            }
            finally {
                bufferedReader.close();
            }
        }
        catch (Exception ex) {
            s2 = null;
        }
        return s2;
    }


    public static String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        if(hours > 0){
            finalTimerString = hours + ":";
        }
        if(seconds < 10){secondsString = "0" + seconds;
        }else{secondsString = "" + seconds;}
        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        return finalTimerString;
    }

    public static int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);
        percentage =(((double)currentSeconds)/totalSeconds) * 100;
        return percentage.intValue();
    }

    public static int progressToTimer(int progress, long totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);
        return currentDuration * 1000;
    }

    public static void startNewActivity(Context context, String packageName, String url, String ext) {
        Uri uri = Uri.parse(url);
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }else intent.setDataAndType(uri, "video/"+ext);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
