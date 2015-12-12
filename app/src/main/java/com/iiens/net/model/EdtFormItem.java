package com.iiens.net.model;

import org.json.JSONObject;

/**
 * EdtFormItem
 * Modèle d'un groupe d'options de recherche de l'emploi du temps
 */

public class EdtFormItem {
    private long id;
    private String name;
    private int promo;

    public EdtFormItem(int i, JSONObject jsonObject) {
        try {
            this.setId(i);
            this.setName(jsonObject.getString("edtForm_name"));
            this.setPromo(jsonObject.getInt("edtForm_promo"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getId() { return this.id; }

    public void setId(long id) { this.id = id; }

    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    public int getPromo() { return this.promo; }

    public void setPromo(int promo) { this.promo = promo; }
}