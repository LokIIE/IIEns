package com.iiens.net;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Vérification des services Firebase
 */

public class CheckFCM extends AsyncTask<Void, Void, Boolean> {

    private Context context;

    public CheckFCM ( Context context ){
        this.context = context;
    }

    @Override
    protected Boolean doInBackground ( Void... voids ) {
        return false;
    }
}
