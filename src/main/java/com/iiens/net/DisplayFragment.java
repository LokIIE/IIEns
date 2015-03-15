package com.iiens.net;

import android.view.View;

import org.json.JSONArray;

/**
 * DisplayFragment
 * Interface permettant de faire le lien entre ApiRequest et les classes appelantes
 * Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 */

interface DisplayFragment {

    public void displayResult(View view, JSONArray result);

    public void refreshDisplay();
}