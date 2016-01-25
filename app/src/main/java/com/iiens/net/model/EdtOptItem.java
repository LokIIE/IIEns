package com.iiens.net.model;

import org.json.JSONObject;

/**
 * Modèle d'une option de recherche de l'emploi du temps
 */

public class EdtOptItem {
    private long id;
    private String code;
    private String name;
    private int fk_edtForm;

    public EdtOptItem() {}

    public static EdtOptItem getEmptyOptItem(String emptyName) {
        EdtOptItem emptyItem = new EdtOptItem();
        emptyItem.setName(emptyName);
        return emptyItem;
    }

    public EdtOptItem(JSONObject jObject) {
        try {
            this.setName(jObject.getString("edtOpt_name"));
            this.setCode(jObject.getString("edtOpt_code"));
            this.setFk_edtForm(jObject.getInt("FK_edtForm"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getName() { return this.name; }

    public void setName(String label) { this.name = label; }

    public String getCode() { return this.code; }

    public void setCode(String code) { this.code = code; }

    public int getFk_edtForm() { return this.fk_edtForm; }

    public void setFk_edtForm(int fk_edtForm) { this.fk_edtForm = fk_edtForm; }

    /***
     * Renvoie l'item sous forme de String
     * Utile pour déterminer l'affichage de l'item dans un spinner
     */
    public String toString() {
        return getName();
    }
}