package com.iiens.net;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class GlobalState extends Application {

    private static Bundle appBundle = new Bundle();

    Bundle getBundle() {
        return appBundle;
    }

    void setBundle(Bundle bundle) {
        appBundle = bundle;
    }

    public String getScriptURL() {
        return getResources().getString(R.string.url_script);
    }

    boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnectedOrConnecting());
    }

    boolean isAriseOnline() {
        InetAddress addr;
        boolean ariseOnline = false;
        try {
            addr = InetAddress.getByName(getResources().getString(R.string.url_iiens));
            if (addr.isReachable(5000)) {
                ariseOnline = true;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.arise_unavailable), Toast.LENGTH_LONG).show();
        }
        return ariseOnline;
    }

    void writeToInternalStorage(String content, String fileName) {
        String eol = System.getProperty("line.separator");
        BufferedWriter writer = null;
        try {
            writer =
                    new BufferedWriter(new OutputStreamWriter(this.openFileOutput(fileName,
                            Context.MODE_PRIVATE)));
            writer.write(content + eol);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    String readFromInternalStorage(String fileName) {
        String eol = System.getProperty("line.separator");
        BufferedReader input;
        String fileString = "";
        try {
            input = new BufferedReader(new InputStreamReader(this.openFileInput(fileName)));
            String line;
            StringBuilder buffer = new StringBuilder();
            while ((line = input.readLine()) != null) {
                buffer.append(line).append(eol);
            }
            input.close();
            fileString = buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileString;
    }

    boolean fileExists(String fname) {
        File file = this.getFileStreamPath(fname);
        return file.exists();
    }
}