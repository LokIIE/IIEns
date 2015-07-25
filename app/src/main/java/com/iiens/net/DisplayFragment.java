package com.iiens.net;

import android.view.View;

import org.json.JSONArray;

/**
 * DisplayFragment
 * Interface permettant de faire le lien entre ApiRequest et les classes appelantes
 */

interface DisplayFragment {

    void displayResult(View view, JSONArray result);

    void refreshDisplay();
}