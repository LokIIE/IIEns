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

    /**
     * Enumération des identifiants des catégories de données
     */
    public enum EnumFormId {
        Group(1),
        Comm(2),
        Option21(3),
        Option22(4),
        Option23(5),
        Option24(6),
        Option25(7),
        Option26(8),
        Option31(9),
        Option32(10),
        Option33(11),
        Option34(12),
        Option35(13),
        Option36(14),
        OptionTc(15),
        Langue(16);

        /**
         * Identifiant de la catégorie
         */
        private int id;

        /**
         * Constructeur
         * @param _id Identifiant de la catégorie
         */
        EnumFormId(int _id) {
            this.id = _id;
        }

        /**
         * Retourne l'identifiant sous forme de int
         */
        public int getValue() {
            return this.id;
        }
    }
}